package vn.techmaster.nowj.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import vn.techmaster.nowj.entity.ContractInfo;
import vn.techmaster.nowj.model.dto.DetectedRiskDTO;

public interface ContractInfoService {
    ContractInfo saveContractFile(MultipartFile file);

    ContractInfo saveContractImage(MultipartFile file);

    List<DetectedRiskDTO> getContractDetail(Long id);

    void deleteContract(Long id);

    List<ContractInfo> getAllContracts();

}
