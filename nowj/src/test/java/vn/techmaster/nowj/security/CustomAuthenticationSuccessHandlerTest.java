package vn.techmaster.nowj.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.Authentication;
import vn.techmaster.nowj.entity.UserInfo;
import vn.techmaster.nowj.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationSuccessHandlerTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private CustomAuthenticationSuccessHandler handler;

    @Test
    @DisplayName("onAuthenticationSuccess cập nhật lastLogin cho user")
    void onAuthenticationSuccess_updatesLastLogin() throws Exception {
        UserInfo user = new UserInfo();
        user.setEmail("test@example.com");
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userRepository).saveAndFlush(user);
        assert user.getLastLogin() != null;
    }

    @Test
    @DisplayName("onAuthenticationSuccess không lưu nếu user không tồn tại")
    void onAuthenticationSuccess_userNotFound() throws Exception {
        when(authentication.getName()).thenReturn("notfound@example.com");
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userRepository, never()).saveAndFlush(any());
    }
}
