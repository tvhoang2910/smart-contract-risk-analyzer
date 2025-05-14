package vn.techmaster.nowj.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tika.Tika;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import vn.techmaster.nowj.dto.DetectedRiskDTO;
import vn.techmaster.nowj.entity.ContractInfo;
import vn.techmaster.nowj.entity.DetectedRiskInfo;
import vn.techmaster.nowj.repository.ContractInfoRepository;
import vn.techmaster.nowj.repository.DetectedRiskRepository;
import vn.techmaster.nowj.service.ContractInfoService;
import vn.techmaster.nowj.utils.AIDetectRisk;

@Service
@Transactional
public class ContractInfoServiceImpl implements ContractInfoService {

    private final ContractInfoRepository contractInfoRepository;
    private final DetectedRiskRepository detectedRiskRepository;
    private final Tika tika;
    private final ModelMapper modelMapper;
    private final AIDetectRisk aiDetectRisk;

    public ContractInfoServiceImpl(ContractInfoRepository contractInfoRepository, Tika tika,
            DetectedRiskRepository detectedRiskRepository, ModelMapper modelMapper, AIDetectRisk aiDetectRisk) {
        this.contractInfoRepository = contractInfoRepository;
        this.tika = tika;
        this.detectedRiskRepository = detectedRiskRepository;
        this.modelMapper = modelMapper;
        this.aiDetectRisk = aiDetectRisk;
    }

    @Override
    public ContractInfo saveContract(MultipartFile file) {
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
            throw new RuntimeException("Failed to process contract file", e);
        }
    }

}
