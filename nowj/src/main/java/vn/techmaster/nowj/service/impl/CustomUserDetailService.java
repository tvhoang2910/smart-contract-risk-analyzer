package vn.techmaster.nowj.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import vn.techmaster.nowj.entity.RoleInfo;
import vn.techmaster.nowj.entity.UserInfo;
import vn.techmaster.nowj.model.dto.MyUserDetail;
import vn.techmaster.nowj.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("[DEBUG] Loading user: " + username);
        UserInfo user = userRepository.getUserByEmail(username);
        if (user == null) {
            System.out.println("[DEBUG] User not found: " + username);
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        System.out.println("[DEBUG] User found: " + user.getEmail());
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<RoleInfo> roles = user.getRoles();
        if (roles == null) {
            roles = new ArrayList<>();
        }
        for (RoleInfo role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
        }
        MyUserDetail myUserDetail = new MyUserDetail(user.getEmail(), user.getPassword(), authorities);
        myUserDetail.setName(user.getName());
        return myUserDetail;
    }

}
