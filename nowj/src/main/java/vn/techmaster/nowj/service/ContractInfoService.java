package vn.techmaster.nowj.service;

import org.springframework.web.multipart.MultipartFile;

import vn.techmaster.nowj.entity.ContractInfo;

public interface ContractInfoService {
    ContractInfo saveContract(MultipartFile file);

}
