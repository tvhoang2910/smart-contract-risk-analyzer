package vn.techmaster.nowj.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Import BindingResult
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid; // Import @Valid
import vn.techmaster.nowj.entity.ContractInfo;
import vn.techmaster.nowj.model.dto.RegistrationRequestDTO;
import vn.techmaster.nowj.service.ContractInfoService;
import vn.techmaster.nowj.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;
    private final ContractInfoService contractInfoService;

    public AuthController(UserService userService, ContractInfoService contractInfoService) {
        this.userService = userService;
        this.contractInfoService = contractInfoService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequestDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("registrationRequest") @Valid RegistrationRequestDTO registrationRequest,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            bindingResult.hasGlobalErrors();
            return "register";
        }

        // Kiểm tra mật khẩu khớp
        if (!registrationRequest.getPassword().equals(registrationRequest.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.registrationRequest", "Mật khẩu xác nhận không khớp.");

            return "register";
        }

        try {
            userService.registerUser(registrationRequest);
            return "redirect:/login?registrationSuccess=true";
        } catch (IllegalArgumentException e) {
            bindingResult.addError(new FieldError("registrationRequest", "email", registrationRequest.getEmail(), false,
                    null, null, e.getMessage()));
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    // @GetMapping("/dashboard")
    // public String dashboardPage(Model model) {
    // model.addAttribute("contractCount",
    // contractInfoService.getAllContracts().size());
    // model.addAttribute("lowRiskCount", contractInfoService.getAllLowRisks());
    // model.addAttribute("mediumRiskCount",
    // contractInfoService.getAllMediumRisks());
    // model.addAttribute("highRiskCount", contractInfoService.getAllHighRisks());
    // return "dashboard";
    // }

    @GetMapping("/")
    public String homePage() {
        return "redirect:/login";
    }
}