package vn.techmaster.nowj.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);
        String requestURI = request.getRequestURI();

        log.debug("Processing request to: {}", requestURI);
        log.debug("JWT token found: {}", jwt != null ? "Yes" : "No");

        if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
            log.debug("JWT token is valid");
            String username = jwtTokenProvider.getUsernameFromJWT(jwt);
            log.debug("Username from JWT: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Authentication set for user: {}", username);
            }
        } else {
            log.debug("JWT token is null or invalid");
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        // Đọc từ header Authorization trước
        String tokenFromHeader = getJwtFromHeader(request);
        if (tokenFromHeader != null) {
            return tokenFromHeader;
        }

        // Nếu không có trong header, đọc từ cookie
        return getJwtFromCookie(request);
    }

    private String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            if (!token.trim().isEmpty()) {
                log.debug("JWT found in Authorization header");
                return token;
            }
        }
        return null;
    }

    private String getJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            log.debug("No cookies found in request");
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if ("JWT_TOKEN".equals(cookie.getName())) {
                String tokenValue = cookie.getValue();
                if (isValidTokenValue(tokenValue)) {
                    log.debug("JWT found in cookie: Yes");
                    return tokenValue;
                } else {
                    log.debug("JWT cookie found but value is null/empty/invalid: {}", tokenValue);
                }
            }
        }

        log.debug("No valid JWT token found in cookies");
        return null;
    }

    private boolean isValidTokenValue(String tokenValue) {
        return tokenValue != null && !tokenValue.trim().isEmpty() && !"null".equals(tokenValue);
    }
}
