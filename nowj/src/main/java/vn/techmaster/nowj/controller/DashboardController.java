package vn.techmaster.nowj.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.techmaster.nowj.service.ContractInfoService;

@Controller
public class DashboardController {

    private final ContractInfoService contractInfoService;

    public DashboardController(ContractInfoService contractInfoService) {
        this.contractInfoService = contractInfoService;
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        model.addAttribute("username", username);

        // Lấy data thực từ service thay vì mock
        try {
            model.addAttribute("contractCount", contractInfoService.getAllContracts().size());
            model.addAttribute("recentContracts", contractInfoService.getAllContracts());
            model.addAttribute("lowRiskCount", contractInfoService.getAllLowRisks());
            model.addAttribute("mediumRiskCount", contractInfoService.getAllMediumRisks());
            model.addAttribute("highRiskCount", contractInfoService.getAllHighRisks());
        } catch (Exception e) {
            // Fallback to mock data if service fails
            model.addAttribute("contractCount", 0);
            model.addAttribute("lowRiskCount", 0);
            model.addAttribute("mediumRiskCount", 0);
            model.addAttribute("highRiskCount", 0);
        }

        return "dashboard";
    }
}
