package vn.techmaster.nowj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.techmaster.nowj.service.UserService;

@Controller
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "password_mismatch");
            return "redirect:/login";
        }
        try {
            // 'username' từ form đang là email, gọi phương thức register với email và name
            userService.registerLocalUser(username, password, fullName);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "email_exists");
            return "redirect:/login";
        }
        redirectAttributes.addFlashAttribute("success", "registered");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "success", required = false) String success,
            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Đăng nhập thất bại. Vui lòng kiểm tra email và mật khẩu.");
            if ("password_mismatch".equals(error)) {
                model.addAttribute("errorMessage", "Mật khẩu xác nhận không khớp.");
            } else if ("email_exists".equals(error)) {
                model.addAttribute("errorMessage", "Email đã tồn tại.");
            }
        }
        if (success != null) {
            model.addAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
        }
        return "login";
    }
}