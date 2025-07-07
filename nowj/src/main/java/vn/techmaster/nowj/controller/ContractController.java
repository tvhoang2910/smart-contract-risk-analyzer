package vn.techmaster.nowj.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.techmaster.nowj.entity.ContractInfo;
import vn.techmaster.nowj.error.BadRequestException;
import vn.techmaster.nowj.model.dto.DetectedRiskDTO;
import vn.techmaster.nowj.model.response.ResponseDTO;
import vn.techmaster.nowj.service.ContractInfoService;

@RestController
@RequestMapping("/contracts")
public class ContractController {

    private final ContractInfoService contractInfoService;

    public ContractController(ContractInfoService contractInfoService) {
        this.contractInfoService = contractInfoService;
    }

    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadContract(@RequestParam("file") MultipartFile file) {
        ResponseDTO responseDTO = new ResponseDTO();
        if (file.isEmpty() || file.getSize() == 0) {
            throw new BadRequestException("File is empty or invalid");
        }
        ContractInfo saved = contractInfoService.saveContractFile(file);
        responseDTO.setData(saved);
        responseDTO.setMessage("Contract uploaded successfully");
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        ResponseDTO responseDTO = new ResponseDTO();
        if (file.isEmpty() || file.getSize() == 0) {
            throw new BadRequestException("File is empty or invalid");
        }
        ContractInfo saved = contractInfoService.saveContractImage(file);
        responseDTO.setData(saved);
        responseDTO.setMessage("Image uploaded successfully");
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @GetMapping("/conversation/{id}")
    public ResponseEntity<?> getContractDetail(@PathVariable Long id) {
        ResponseDTO responseDTO = new ResponseDTO();
        List<DetectedRiskDTO> detectedRiskInfos = contractInfoService.getContractDetail(id);
        responseDTO.setData(detectedRiskInfos);
        responseDTO.setMessage("Get contract detail successfully");
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @DeleteMapping("/conversations/{id}")
    public ResponseEntity<?> deleteContract(@PathVariable Long id) {
        ResponseDTO responseDTO = new ResponseDTO();
        contractInfoService.deleteContract(id);
        responseDTO.setMessage("Delete contract successfully");
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

}
