package vn.techmaster.nowj.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.techmaster.nowj.entity.ContractInfo;
import vn.techmaster.nowj.service.ContractInfoService;

@RestController
@RequestMapping("/contracts")
public class DocumentController {

    private final ContractInfoService contractInfoService;

    public DocumentController(ContractInfoService contractInfoService) {
        this.contractInfoService = contractInfoService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadContract(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || file.getSize() == 0 || file == null) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        ContractInfo saved = contractInfoService.saveContract(file);
        return ResponseEntity.ok(saved);
    }
}
