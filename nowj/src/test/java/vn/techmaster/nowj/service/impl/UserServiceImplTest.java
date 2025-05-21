package vn.techmaster.nowj.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.techmaster.nowj.entity.UserInfo;
import vn.techmaster.nowj.model.dto.RegistrationRequestDTO;
import vn.techmaster.nowj.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock PasswordEncoder passwordEncoder;
    @Mock UserRepository userRepository;
    @InjectMocks UserServiceImpl userService;

    RegistrationRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new RegistrationRequestDTO();
        validRequest.setEmail("test@email.com");
        validRequest.setPassword("password123");
        validRequest.setConfirmPassword("password123");
        validRequest.setFullName("Test User");
    }

    @Test
    void registerUser_success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(UserInfo.class))).thenAnswer(i -> i.getArgument(0));
        assertDoesNotThrow(() -> userService.registerUser(validRequest));
        verify(userRepository).save(any(UserInfo.class));
    }

    @Test
    void registerUser_emailExists_throwsException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(validRequest));
        assertTrue(ex.getMessage().contains("Email already exists"));
    }

    @Test
    void registerUser_passwordMismatch_throwsException() {
        validRequest.setConfirmPassword("wrong");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(validRequest));
        assertTrue(ex.getMessage().contains("Mật khẩu và xác nhận mật khẩu không khớp"));
    }
}
