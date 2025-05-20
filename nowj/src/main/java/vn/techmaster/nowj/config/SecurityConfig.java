package vn.techmaster.nowj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import vn.techmaster.nowj.security.CustomAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler; 

    public SecurityConfig(UserDetailsService userDetailsService,
            CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF nếu bạn không sử dụng form submit truyền thống
                .authorizeHttpRequests(authorize -> authorize
                        // **Sửa đổi:** Thêm "/do-login" vào danh sách các URL được phép truy cập công khai
                        .requestMatchers("/register", "/login", "/", "/do-login").permitAll()
                        .anyRequest().authenticated() // Yêu cầu xác thực cho các request khác
                )
                .formLogin(form -> form
                        .loginPage("/login") // URL của trang đăng nhập
                        .loginProcessingUrl("/do-login") // URL mà form đăng nhập gửi dữ liệu đến
                        .successHandler(customAuthenticationSuccessHandler) // Sử dụng handler tùy chỉnh ở đây (để cập nhật lastLogin)
                        .defaultSuccessUrl("/dashboard", true) // Chuyển hướng đến /dashboard sau khi đăng nhập thành công (nếu không có saved request)
                        .failureUrl("/login?error=true") // URL chuyển hướng khi đăng nhập thất bại
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL để đăng xuất
                        .logoutSuccessUrl("/login?logout=true") // URL chuyển hướng sau khi đăng xuất
                        .permitAll())
                .userDetailsService(userDetailsService); // Sử dụng UserDetailsService tùy chỉnh của bạn

        return http.build();
    }
}