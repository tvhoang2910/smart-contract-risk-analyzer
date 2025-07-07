package vn.techmaster.nowj.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import vn.techmaster.nowj.model.dto.ApiResponse;
import vn.techmaster.nowj.model.dto.JwtAuthenticationResponse;
import vn.techmaster.nowj.model.dto.LoginRequest;
import vn.techmaster.nowj.model.dto.RegistrationRequestDTO;
import vn.techmaster.nowj.security.JwtTokenProvider;
import vn.techmaster.nowj.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class JwtAuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public JwtAuthController(AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            String jwt = jwtTokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Invalid email or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody RegistrationRequestDTO registrationRequest) {
        try {
            userService.registerUser(registrationRequest);
            return ResponseEntity.ok(new ApiResponse(true, "User registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
