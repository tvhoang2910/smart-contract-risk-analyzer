package vn.techmaster.nowj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.techmaster.nowj.entity.ContractInfo;
import vn.techmaster.nowj.model.dto.DetectedRiskDTO;
import vn.techmaster.nowj.service.ContractInfoService;

import java.util.List;
import java.util.Optional;

@Controller
public class ViewController {

    private final ContractInfoService contractInfoService;

    public ViewController(ContractInfoService contractInfoService) {
        this.contractInfoService = contractInfoService;
    }

    @GetMapping("/upload")
    public String getUploadPage() {
        return "contract-upload";
    }

    @GetMapping("/conversation")
    public String getConversationPage(Model model) {
        List<ContractInfo> contractInfos = contractInfoService.getAllContracts();
        model.addAttribute("contractInfos", contractInfos);
        return "contract-conversation";
    }

    @GetMapping("/conversation/{id}")
    public String getConversationDetailPage(Model model, @PathVariable Long id) {
        List<ContractInfo> contractInfos = contractInfoService.getAllContracts();
        model.addAttribute("contractInfos", contractInfos);

        Optional<ContractInfo> selectedContractOpt = contractInfos.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();

        if (selectedContractOpt.isPresent()) {
            ContractInfo selectedContract = selectedContractOpt.get();
            List<DetectedRiskDTO> detectedRiskDTOs = contractInfoService.getContractDetail(id);

            model.addAttribute("selectedContractId", id);
            model.addAttribute("selectedContractFilename", selectedContract.getFilename());
            model.addAttribute("detectedRisks", detectedRiskDTOs);
        } else {
            model.addAttribute("error", "Không tìm thấy hợp đồng với ID: " + id);
        }
        return "contract-conversation";
    }
}