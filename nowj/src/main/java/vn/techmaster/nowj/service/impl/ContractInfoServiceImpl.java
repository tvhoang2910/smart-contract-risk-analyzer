package vn.techmaster.nowj.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.Tika;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import vn.techmaster.nowj.entity.ContractInfo;
import vn.techmaster.nowj.entity.DetectedRiskInfo;
import vn.techmaster.nowj.error.AppException;
import vn.techmaster.nowj.error.BadRequestException;
import vn.techmaster.nowj.error.ResourceNotFoundException;
import vn.techmaster.nowj.model.dto.DetectedRiskDTO;
import vn.techmaster.nowj.model.response.ContractDetailResponseDTO;
import vn.techmaster.nowj.repository.ContractInfoRepository;
import vn.techmaster.nowj.repository.DetectedRiskRepository;
import vn.techmaster.nowj.service.ContractInfoService;
import vn.techmaster.nowj.utils.AIDetectRisk;
import vn.techmaster.nowj.utils.SmartTextExtractor;

@Service
@Transactional
public class ContractInfoServiceImpl implements ContractInfoService {

    private final ContractInfoRepository contractInfoRepository;
    private final DetectedRiskRepository detectedRiskRepository;
    private final Tika tika;
    private final ModelMapper modelMapper;
    private final AIDetectRisk aiDetectRisk;
    private final SmartTextExtractor smartTextExtractor;

    public ContractInfoServiceImpl(ContractInfoRepository contractInfoRepository, Tika tika,
            DetectedRiskRepository detectedRiskRepository, ModelMapper modelMapper, AIDetectRisk aiDetectRisk,
            SmartTextExtractor smartTextExtractor) {
        this.contractInfoRepository = contractInfoRepository;
        this.tika = tika;
        this.detectedRiskRepository = detectedRiskRepository;
        this.modelMapper = modelMapper;
        this.aiDetectRisk = aiDetectRisk;
        this.smartTextExtractor = smartTextExtractor;
    }

    @Override
    public ContractInfo saveContractFile(MultipartFile file) {
        try {
            String extractedText = tika.parseToString(file.getInputStream());

            ContractInfo contract = new ContractInfo();
            contract.setFilename(file.getOriginalFilename());
            contract.setContentType(file.getContentType());
            contract.setFileSize(file.getSize());
            contract.setExtractedText(extractedText);
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
        } catch (Exception e) {
            System.err.println("💥 Lỗi khi xử lý file: " + e.getMessage());
            e.printStackTrace();
            throw new AppException("Failed to process contract file", e);
        }
    }

    @Override
    public ContractDetailResponseDTO getContractDetail(Long id) {
        if (id == null) {
            throw new BadRequestException("Contract ID cannot be null");
        }

        ContractInfo contract = contractInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract", "id", id));

        ContractDetailResponseDTO contractDetailResponseDTO = new ContractDetailResponseDTO();
        contractDetailResponseDTO.setFilenameString(contract.getFilename());
        List<DetectedRiskInfo> detectedRiskInfos = detectedRiskRepository.findAllByContract_Id(id);
        List<DetectedRiskDTO> detectedRisks = new ArrayList<>();
        for (DetectedRiskInfo riskInfo : detectedRiskInfos) {
            DetectedRiskDTO detectedRiskDTO = modelMapper.map(riskInfo, DetectedRiskDTO.class);
            detectedRisks.add(detectedRiskDTO);
        }
        contractDetailResponseDTO.setDetectedRisks(detectedRisks);
        return contractDetailResponseDTO;
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
        try {
            String extractedText = smartTextExtractor.getTextFromImage(file);

            ContractInfo contract = new ContractInfo();
            contract.setFilename(file.getOriginalFilename());
            contract.setContentType(file.getContentType());
            contract.setFileSize(file.getSize());
            contract.setExtractedText(extractedText);
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
        } catch (Exception e) {
            System.err.println("💥 Lỗi khi xử lý ảnh: " + e.getMessage());
            e.printStackTrace();
            throw new AppException("Failed to process image contract file", e);
        }
    }

}
