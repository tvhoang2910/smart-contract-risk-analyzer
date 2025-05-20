package vn.techmaster.nowj.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import vn.techmaster.nowj.model.dto.RegistrationRequestDTO;
import vn.techmaster.nowj.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Trả về tên file HTML của trang đăng nhập
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequestDTO());
        return "register"; // Trả về tên file HTML của trang đăng ký
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("registrationRequest") RegistrationRequestDTO registrationRequest, Model model) {
        try {
            userService.registerUser(registrationRequest);
            return "redirect:/login?registrationSuccess=true"; // Chuyển hướng về trang đăng nhập sau khi đăng ký thành công
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register"; // Trả về trang đăng ký với thông báo lỗi
        }
    }

    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard"; // Trả về trang sau khi đăng nhập thành công
    }

    @GetMapping("/")
    public String homePage() {
        return "dashboard"; // Trả về trang chủ
    }
}