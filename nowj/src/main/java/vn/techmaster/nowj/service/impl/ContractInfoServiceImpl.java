package vn.techmaster.nowj.service.impl;

import org.apache.tika.Tika;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.techmaster.nowj.entity.ContractInfo;
import vn.techmaster.nowj.entity.DetectedRiskInfo;
import vn.techmaster.nowj.entity.UserInfo;
import vn.techmaster.nowj.error.AppException;
import vn.techmaster.nowj.error.BadRequestException;
import vn.techmaster.nowj.error.ResourceNotFoundException;
import vn.techmaster.nowj.model.dto.DetectedRiskDTO;
import vn.techmaster.nowj.repository.ContractInfoRepository;
import vn.techmaster.nowj.repository.DetectedRiskRepository;
import vn.techmaster.nowj.repository.UserRepository;
import vn.techmaster.nowj.service.ContractInfoService;
import vn.techmaster.nowj.utils.AIDetectRisk;
import vn.techmaster.nowj.utils.SmartTextExtractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ContractInfoServiceImpl implements ContractInfoService {

    private final ContractInfoRepository contractInfoRepository;
    private final DetectedRiskRepository detectedRiskRepository;
    private final Tika tika;
    private final ModelMapper modelMapper;
    private final AIDetectRisk aiDetectRisk;
    private final SmartTextExtractor smartTextExtractor;
    private final UserRepository userRepository;

    public ContractInfoServiceImpl(ContractInfoRepository contractInfoRepository, Tika tika,
            DetectedRiskRepository detectedRiskRepository, ModelMapper modelMapper, AIDetectRisk aiDetectRisk,
            SmartTextExtractor smartTextExtractor, UserRepository userRepository) {
        this.contractInfoRepository = contractInfoRepository;
        this.tika = tika;
        this.detectedRiskRepository = detectedRiskRepository;
        this.modelMapper = modelMapper;
        this.aiDetectRisk = aiDetectRisk;
        this.smartTextExtractor = smartTextExtractor;
        this.userRepository = userRepository;
    }

    @Override
    public ContractInfo saveContractFile(MultipartFile file) {
        try {
            String extractedText = tika.parseToString(file.getInputStream());

            return getContractInfo(extractedText, file);
        } catch (Exception e) {
            System.err.println("💥 Lỗi khi xử lý file: " + e.getMessage());
            e.printStackTrace();
            throw new AppException("Failed to process contract file", e);
        }
    }

    @Override
    public List<DetectedRiskDTO> getContractDetail(Long id) {
        if (id == null) {
            throw new BadRequestException("Contract ID cannot be null");
        }

        List<DetectedRiskInfo> detectedRiskInfos = detectedRiskRepository.findAllByContract_Id(id);
        List<DetectedRiskDTO> detectedRisks = new ArrayList<>();
        for (DetectedRiskInfo riskInfo : detectedRiskInfos) {
            DetectedRiskDTO detectedRiskDTO = modelMapper.map(riskInfo, DetectedRiskDTO.class);
            detectedRisks.add(detectedRiskDTO);
        }
        return detectedRisks;
    }

    @Override
    public void deleteContract(Long id) {
        ContractInfo contract = contractInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract", "id", id));
        detectedRiskRepository.deleteAllByContract_Id(id);
        contractInfoRepository.delete(contract);
    }

    @Override
    public ContractInfo saveContractImage(MultipartFile file) {
        String extractedText;
        try {
            extractedText = smartTextExtractor.getTextFromImage(file);
        } catch (RuntimeException e) {
            System.err.println("💥 Lỗi khi xử lý ảnh: " + e.getMessage());
            e.printStackTrace();
            throw new AppException("Failed to process image contract file", e);
        }
        try {
            return getContractInfo(extractedText, file);
        } catch (IOException e) {
            System.err.println("💥 Lỗi khi lưu hợp đồng: " + e.getMessage());
            e.printStackTrace();
            throw new AppException("Failed to save contract info", e);
        }
    }

    @Override
    public List<ContractInfo> getAllContracts() {
        List<ContractInfo> contractInfos = contractInfoRepository.findAll();
        if (contractInfos.isEmpty()) {
            throw new ResourceNotFoundException("Contract", "id", null);
        }
        return contractInfos;
    }

    private ContractInfo getContractInfo(String extractedText, MultipartFile file) throws IOException {
        ContractInfo contract = new ContractInfo();
        contract.setFilename(file.getOriginalFilename());
        contract.setContentType(file.getContentType());
        contract.setFileSize(file.getSize());
        contract.setExtractedText(extractedText);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", authentication.getName()));
        contract.setUser(user);
        contractInfoRepository.save(contract);

        List<DetectedRiskDTO> detectedRisks = aiDetectRisk.analyzeContractRisks(extractedText);
        List<DetectedRiskInfo> riskInfos = new ArrayList<>();
        for (DetectedRiskDTO risk : detectedRisks) {
            DetectedRiskInfo riskInfo = modelMapper.map(risk, DetectedRiskInfo.class);
            riskInfo.setContract(contract);
            riskInfos.add(riskInfo);
        }
        contract.setDetectedRisks(riskInfos);
        detectedRiskRepository.saveAll(riskInfos);
        return contract;
    }

}
