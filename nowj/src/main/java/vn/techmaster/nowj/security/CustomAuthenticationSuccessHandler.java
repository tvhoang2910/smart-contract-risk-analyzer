package vn.techmaster.nowj.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techmaster.nowj.entity.UserInfo;
import vn.techmaster.nowj.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public CustomAuthenticationSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String userEmail = authentication.getName();
        Optional<UserInfo> optionalUser = userRepository.findByEmail(userEmail);

        optionalUser.ifPresent(user -> {
            user.setLastLogin(LocalDateTime.now());
            userRepository.saveAndFlush(user);
            System.out.println("[DEBUG] Updated lastLogin for user: " + user.getEmail() + " at " + user.getLastLogin());
        });
    }
}