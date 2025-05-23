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

    @GetMapping("/dashboard")
    public String getDashboardPage(Model model) {
        model.addAttribute("contractCount", contractInfoService.getAllContracts().size());
        model.addAttribute("recentContracts", contractInfoService.getAllContracts());
        model.addAttribute("lowRisksCount", contractInfoService.getAllLowRisks());
        model.addAttribute("mediumRisksCount", contractInfoService.getAllMediumRisks());
        model.addAttribute("highRisksCount", contractInfoService.getAllHighRisks());

        return "dashboard";
    }

    @GetMapping("/upload")
    public String getUploadPage() {
        return "contract-upload";
    }

    @GetMapping("/conversation")
    public String getConversationPage(Model model) {
        model.addAttribute("contractInfos", contractInfoService.getAllContracts());
        return "contract-conversation";
    }

    @GetMapping("/conversation/{id}")
    public String getConversationDetailPage(Model model, @PathVariable Long id) {
        model.addAttribute("contractInfos", contractInfoService.getAllContracts());

        Optional<ContractInfo> selectedContractOpt = contractInfoService.getAllContracts().stream()
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

    @GetMapping("/settings")
    public String getSettingsPage() {
        return "settings";
    }
}