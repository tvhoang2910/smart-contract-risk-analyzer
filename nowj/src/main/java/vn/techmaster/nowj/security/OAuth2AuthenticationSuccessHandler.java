package vn.techmaster.nowj.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techmaster.nowj.constant.SystemConstant;
import vn.techmaster.nowj.entity.RoleInfo;
import vn.techmaster.nowj.entity.UserInfo;
import vn.techmaster.nowj.repository.RoleRepository;
import vn.techmaster.nowj.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Component
@Slf4j
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RoleRepository roleRepository;

    public OAuth2AuthenticationSuccessHandler(UserRepository userRepository, JwtTokenProvider jwtTokenProvider,
            RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        log.info("OAuth2 authentication success for email: {}", email);

        // Tìm hoặc tạo user
        UserInfo user = findOrCreateUser(email, name);

        // Tạo JWT token
        String jwt = createJwtToken(user);

        // Lưu JWT vào cookie
        Cookie jwtCookie = new Cookie("JWT_TOKEN", jwt);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60); // 24 hours
        response.addCookie(jwtCookie);

        // Redirect theo role
        String redirectUrl = determineRedirectUrl(user);
        response.sendRedirect(redirectUrl);
    }

    private UserInfo findOrCreateUser(String email, String name) {
        Optional<UserInfo> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            UserInfo user = optionalUser.get();
            user.setLastLogin(LocalDateTime.now());
            // Nếu user chưa có role thì gán role USER
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                RoleInfo userRole = roleRepository.findByCode("USER")
                        .orElseThrow(() -> new RuntimeException("Role USER not found"));
                user.getRoles().add(userRole);
            }
            return userRepository.save(user);
        } else {
            // Tạo user mới từ OAuth2
            UserInfo newUser = new UserInfo();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPassword(""); // OAuth2 user không cần password
            newUser.setLastLogin(LocalDateTime.now());
            // Gán role USER mặc định
            RoleInfo userRole = roleRepository.findByCode("USER")
                    .orElseThrow(() -> new RuntimeException("Role USER not found"));
            newUser.getRoles().add(userRole);
            log.info("Creating new user from OAuth2: {}", email);
            return userRepository.save(newUser);
        }
    }

    private String createJwtToken(UserInfo user) {
        // Tạo authorities cho user
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode())));
        } else {
            // Nếu không có role, gán role USER mặc định
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // Tạo UserDetails để tạo JWT
        UserDetails userDetails = new User(user.getEmail(), "", authorities);

        // Tạo Authentication object
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken authToken = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null, authorities);

        return jwtTokenProvider.generateToken(authToken);
    }

    private String determineRedirectUrl(UserInfo user) {
        // Kiểm tra role để redirect
        if (user.getRoles() != null &&
                user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getCode()))) {
            return SystemConstant.ADMIN_HOME;
        }
        return SystemConstant.HOME;
    }
}
