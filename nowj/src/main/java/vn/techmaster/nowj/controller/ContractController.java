package vn.techmaster.nowj.controller;

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
import vn.techmaster.nowj.model.response.ContractDetailResponseDTO;
import vn.techmaster.nowj.model.response.ResponseDTO;
import vn.techmaster.nowj.service.ContractInfoService;

@RestController
@RequestMapping("/contracts")
public class ContractController {

    private final ContractInfoService contractInfoService;

    public ContractController(ContractInfoService contractInfoService) {
        this.contractInfoService = contractInfoService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadContract(@RequestParam("file") MultipartFile file) {
        ResponseDTO responseDTO = new ResponseDTO();
        if (file.isEmpty() || file.getSize() == 0 || file == null) {
            throw new BadRequestException("File is empty or invalid");
        }
        ContractInfo saved = contractInfoService.saveContract(file);
        responseDTO.setData(saved);
        responseDTO.setMessage("Contract uploaded successfully");
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @GetMapping("/conversation/{id}")
    public ResponseEntity<?> getContractDetail(@PathVariable Long id) {
        ResponseDTO responseDTO = new ResponseDTO();
        ContractDetailResponseDTO contractDetailResponseDTO = contractInfoService.getContractDetail(id);
        // ResourceNotFoundException được xử lý bởi GlobalExceptionHandler
        responseDTO.setData(contractDetailResponseDTO);
        responseDTO.setMessage("Get contract detail successfully");
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContract(@PathVariable Long id) {
        ResponseDTO responseDTO = new ResponseDTO();
        contractInfoService.deleteContract(id);
        responseDTO.setMessage("Delete contract successfully");
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

}
