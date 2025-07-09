package vn.techmaster.nowj.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.techmaster.nowj.constant.SystemConstant;
import vn.techmaster.nowj.model.dto.LoginRequest;
import vn.techmaster.nowj.model.dto.RegistrationRequestDTO;
import vn.techmaster.nowj.security.JwtTokenProvider;
import vn.techmaster.nowj.service.UserService;

import jakarta.validation.Valid;

@Controller
public class WebAuthController {

    private static final Logger logger = LoggerFactory.getLogger(WebAuthController.class);
    private static final String LOGIN_VIEW = "login";
    private static final String REGISTER_VIEW = "register";
    private static final String JWT_TOKEN_COOKIE_NAME = "JWT_TOKEN";
    private static final String ERROR_ATTRIBUTE = "error";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public WebAuthController(AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(Model model,
            @RequestParam(value = "unauthorized", required = false) String unauthorized,
            @RequestParam(value = "logout", required = false) String logout) {
        model.addAttribute("loginRequest", new LoginRequest());

        if ("true".equals(unauthorized)) {
            model.addAttribute(ERROR_ATTRIBUTE, "You are not authorized");
        }

        return LOGIN_VIEW;
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute("loginRequest") @Valid LoginRequest loginRequest,
            BindingResult bindingResult,
            HttpServletResponse response,
            Model model) {

        if (bindingResult.hasErrors()) {
            return LOGIN_VIEW;
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            // Tạo JWT token
            String jwt = jwtTokenProvider.generateToken(authentication);

            // Lưu JWT vào cookie
            Cookie jwtCookie = new Cookie(JWT_TOKEN_COOKIE_NAME, jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60); // 24 hours
            response.addCookie(jwtCookie);

            // Phân quyền redirect theo role
            String redirectUrl = determineRedirectUrl(authentication);
            return "redirect:" + redirectUrl;
        } catch (AuthenticationException e) {
            model.addAttribute(ERROR_ATTRIBUTE, "Email hoặc mật khẩu không đúng");
            return LOGIN_VIEW;
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequestDTO());
        return REGISTER_VIEW;
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("registrationRequest") @Valid RegistrationRequestDTO registrationRequest,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return REGISTER_VIEW;
        }

        // Kiểm tra mật khẩu khớp
        if (!registrationRequest.getPassword().equals(registrationRequest.getConfirmPassword())) {
            bindingResult
                    .addError(new FieldError("registrationRequest", "confirmPassword", "Mật khẩu xác nhận không khớp"));
            return REGISTER_VIEW;
        }

        try {
            userService.registerUser(registrationRequest);
            model.addAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
            model.addAttribute("loginRequest", new LoginRequest());
            return LOGIN_VIEW;
        } catch (IllegalArgumentException e) {
            model.addAttribute(ERROR_ATTRIBUTE, e.getMessage());
            return REGISTER_VIEW;
        }
    }

    @GetMapping("/")
    public String homePage() {
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Logout requested from IP: {}", request.getRemoteAddr());

        // Log existing cookies before logout
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (JWT_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    logger.info("Found JWT_TOKEN cookie with value: {}",
                            cookie.getValue() != null ? "present" : "null");
                }
            }
        }

        // Clear security context first
        SecurityContextHolder.clearContext();
        logger.info("Security context cleared");

        // Invalidate HTTP session (if any exists)
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
            logger.info("HTTP session invalidated");
        }

        // Xóa JWT cookie với nhiều cách khác nhau để đảm bảo
        // Cách 1: Xóa với empty value
        Cookie jwtCookie1 = new Cookie(JWT_TOKEN_COOKIE_NAME, "");
        jwtCookie1.setHttpOnly(true);
        jwtCookie1.setPath("/");
        jwtCookie1.setMaxAge(0);
        response.addCookie(jwtCookie1);

        // Cách 2: Xóa với null value
        Cookie jwtCookie2 = new Cookie(JWT_TOKEN_COOKIE_NAME, null);
        jwtCookie2.setHttpOnly(true);
        jwtCookie2.setPath("/");
        jwtCookie2.setMaxAge(0);
        response.addCookie(jwtCookie2);

        // Cách 3: Xóa cookie mà không set HttpOnly (một số browser cần)
        Cookie jwtCookie3 = new Cookie(JWT_TOKEN_COOKIE_NAME, "");
        jwtCookie3.setPath("/");
        jwtCookie3.setMaxAge(0);
        response.addCookie(jwtCookie3);

        // Thêm cache control headers để ngăn browser cache
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        logger.info("JWT_TOKEN cookies deleted with multiple methods");

        return "redirect:/login?logout=true";
    }

    @GetMapping("/dashboard")
    public String handleDashboardAccess(Model model) {
        // Kiểm tra quyền truy cập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            // Nếu chưa đăng nhập, chuyển hướng về login
            return "redirect:/login?unauthorized=true";
        }

        // Kiểm tra có phải admin không
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> SystemConstant.ADMIN_ROLE.equals(authority.getAuthority()));

        if (isAdmin) {
            // Nếu là admin, chuyển hướng đến admin dashboard
            return "redirect:/admin/dashboard";
        } else {
            // Nếu không phải admin, chuyển hướng về login với thông báo
            return "redirect:/login?unauthorized=true";
        }
    }

    private String determineRedirectUrl(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> SystemConstant.ADMIN_ROLE.equals(authority.getAuthority()))
                        ? SystemConstant.ADMIN_HOME
                        : SystemConstant.HOME;
    }
}
