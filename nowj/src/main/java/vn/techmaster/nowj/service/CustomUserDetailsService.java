package vn.techmaster.nowj.service; // Thay đổi tên package phù hợp

import vn.techmaster.nowj.entity.UserInfo; // Sử dụng UserInfo
import vn.techmaster.nowj.repository.UserRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository; // Sử dụng UserRepository phù hợp

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserInfo user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Tạo một danh sách GrantedAuthority cố định cho role "ROLE_USER"
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        // Tạo UserDetails từ thông tin User entity và role cố định
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // Email là username
                user.getPassword(), // Mật khẩu đã mã hóa
                authorities // Danh sách role cố định
        );
    }
}