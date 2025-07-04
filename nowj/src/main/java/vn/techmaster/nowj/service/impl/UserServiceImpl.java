package vn.techmaster.nowj.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.techmaster.nowj.entity.UserInfo;
import vn.techmaster.nowj.model.dto.RegistrationRequestDTO;
import vn.techmaster.nowj.model.dto.RoleDTO;
import vn.techmaster.nowj.model.dto.UserDTO;
import vn.techmaster.nowj.repository.UserRepository;
import vn.techmaster.nowj.service.UserService;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void registerUser(RegistrationRequestDTO registrationRequest) {
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists!");
        }
        if (!registrationRequest.getPassword().equals(registrationRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu và xác nhận mật khẩu không khớp!");
        }
        UserInfo user = new UserInfo();
        user.setEmail(registrationRequest.getEmail());
        user.setName(registrationRequest.getFullName());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        userRepository.save(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        UserInfo user = userRepository.getUserByEmail(email);
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setPassword(user.getPassword());
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            List<RoleDTO> roleDTOs = user.getRoles().stream().map(roleInfo -> {
                RoleDTO dto = new RoleDTO();
                dto.setCode(roleInfo.getCode());
                dto.setName(roleInfo.getName());
                return dto;
            }).toList();
            userDTO.setRoles(roleDTOs);
        } else {
            userDTO.setRoles(List.of());
        }
        return userDTO;
    }

}
