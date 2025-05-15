package vn.techmaster.nowj.service;

import org.springframework.web.multipart.MultipartFile;

import vn.techmaster.nowj.entity.ContractInfo;
import vn.techmaster.nowj.model.response.ContractDetailResponseDTO;

public interface ContractInfoService {
    ContractInfo saveContract(MultipartFile file);

    ContractDetailResponseDTO getContractDetail(Long id);

    void deleteContract(Long id);

}
