package vn.techmaster.nowj.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.techmaster.nowj.entity.ContractInfo;
import vn.techmaster.nowj.entity.UserInfo;
import vn.techmaster.nowj.model.dto.DetectedRiskDTO;
import vn.techmaster.nowj.repository.UserRepository;
import vn.techmaster.nowj.service.ContractInfoService;

import java.util.List;
import java.util.Optional;

@Controller
public class ViewController {

    private final ContractInfoService contractInfoService;
    private final UserRepository userRepository;

    public ViewController(ContractInfoService contractInfoService, UserRepository userRepository) {
        this.contractInfoService = contractInfoService;
        this.userRepository = userRepository;
    }

    @GetMapping("/upload")
    public String getUploadPage() {
        return "contract-upload";
    }

    @GetMapping("/conversation")
    public String getConversationPage(Model model) {
        try {
            // Lấy thông tin người dùng hiện tại từ Authentication
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            // Tìm user bằng email thay vì ID số
            Optional<UserInfo> userOptional = userRepository.findByEmail(userEmail);

            if (userOptional.isEmpty()) {
                model.addAttribute("error", "Không tìm thấy thông tin người dùng");
                return "contract-conversation";
            }

            model.addAttribute("contractInfos", contractInfoService.getAllContracts());
            return "contract-conversation";
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "contract-conversation";
        }
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