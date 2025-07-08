package vn.techmaster.nowj.service;

import vn.techmaster.nowj.entity.UserInfo;
import vn.techmaster.nowj.entity.RoleInfo;
import vn.techmaster.nowj.repository.UserRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserInfo user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<RoleInfo> roles = user.getRoles();
        if (roles == null) {
            roles = List.of();
        }
        List<GrantedAuthority> authorities = roles.stream()
                .map(RoleInfo::getCode)
                .map(code -> "ROLE_" + code)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new User(
                user.getEmail(),
                user.getPassword(),
                authorities);
    }
}