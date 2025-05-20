package vn.techmaster.nowj.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Import này
import org.springframework.stereotype.Service;
import vn.techmaster.nowj.entity.UserInfo;
import vn.techmaster.nowj.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserInfo user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Tạo danh sách quyền (authorities) từ trường 'role'
        List<SimpleGrantedAuthority> authorities = Collections.emptyList(); // Mặc định không có quyền
        if (user.getRole() != null && !user.getRole().trim().isEmpty()) {
            authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()));
        }


        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(), // Mật khẩu đã mã hóa
                authorities // Các quyền của người dùng
        );
    }
}