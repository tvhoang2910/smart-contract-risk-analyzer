package vn.techmaster.nowj.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vn.techmaster.nowj.entity.UserInfo;
import vn.techmaster.nowj.model.dto.MyUserDetail;
import vn.techmaster.nowj.repository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailService.class);
    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Loading user: {}", email);

        UserInfo user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", email);
                    return new UsernameNotFoundException("User not found: " + email);
                });

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> "ROLE_" + role.getCode())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        MyUserDetail myUserDetail = new MyUserDetail(user.getEmail(), user.getPassword(), authorities);
        myUserDetail.setName(user.getName());
        return myUserDetail;
    }
}