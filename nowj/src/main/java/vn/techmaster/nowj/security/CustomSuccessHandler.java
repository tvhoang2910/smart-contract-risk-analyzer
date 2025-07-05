package vn.techmaster.nowj.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import vn.techmaster.nowj.constant.SystemConstant;
import vn.techmaster.nowj.security.utils.SecurityUtils;

import java.io.IOException;
import java.util.List;

@Setter
@Getter
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        String targetUrl = determineTargetUrl(authentication);
        if (response.isCommitted()) {
            System.out.println("Can't redirect");
            return;
        }
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    public String determineTargetUrl(Authentication authentication) {
        String url = "";
        List<String> roles = SecurityUtils.getAuthorities();
        if (isAdmin(roles)) {
            url = SystemConstant.ADMIN_HOME;
        } else {
            url = SystemConstant.HOME;
        }
        return url;
    }

    private boolean isAdmin(List<String> roles) {
        return roles.contains(SystemConstant.ADMIN_ROLE);
    }

    private boolean isUser(List<String> roles) {
        return roles.contains(SystemConstant.USER_ROLE);
    }

}