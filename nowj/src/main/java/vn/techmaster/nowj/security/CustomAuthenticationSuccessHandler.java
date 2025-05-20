package vn.techmaster.nowj.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
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
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String userEmail = authentication.getName(); // Spring Security sử dụng getName() để lấy principal's name (ở đây là email)


        Optional<UserInfo> optionalUser = userRepository.findByEmail(userEmail);


        optionalUser.ifPresent(user -> {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user); 
        });

        // Chuyển hướng người dùng đến trang mặc định sau khi đăng nhập thành công
        // Bạn có thể tùy chỉnh URL chuyển hướng ở đây hoặc để SecurityConfig xử lý
        // response.sendRedirect("/dashboard"); // Ví dụ: Chuyển hướng đến /dashboard
        // Hoặc gọi handler mặc định của Spring Security nếu cần
        // new
        // org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler().onAuthenticationSuccess(request,
        // response, authentication);

        // Để Spring Security tiếp tục xử lý chuyển hướng (như đã cấu hình trong
        // SecurityConfig defaultSuccessUrl)
        // chúng ta không cần gọi response.sendRedirect() ở đây nếu đã cấu hình
        // defaultSuccessUrl trong SecurityConfig
        // Nếu bạn muốn ghi đè hoàn toàn việc chuyển hướng, hãy sử dụng
        // response.sendRedirect()
        // Nếu không gọi response.sendRedirect() ở đây, Spring Security sẽ dùng
        // defaultSuccessUrl hoặc saved request URL
    }
}