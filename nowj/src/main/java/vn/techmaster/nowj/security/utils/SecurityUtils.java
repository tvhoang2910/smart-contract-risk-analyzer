package vn.techmaster.nowj.security.utils;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.techmaster.nowj.model.dto.MyUserDetail;

public class SecurityUtils {

    public static MyUserDetail getPrincipal() {
        return (MyUserDetail) (SecurityContextHolder
                .getContext()).getAuthentication().getPrincipal();
    }

    public static List<String> getAuthorities() {
        List<String> results = new ArrayList<>();
        for (GrantedAuthority authority : SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities()) {
            results.add(authority.getAuthority());
        }
        return results;
    }
}