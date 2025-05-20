package vn.techmaster.nowj.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.techmaster.nowj.entity.UserInfo;
import vn.techmaster.nowj.repository.UserRepository;
import vn.techmaster.nowj.service.UserService;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<UserInfo> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserInfo save(UserInfo user) {
        return userRepository.save(user);
    }

    @Override
    public UserInfo registerLocalUser(String email, String password, String name) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        UserInfo user = new UserInfo();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setProvider("local");
        user.setName(name);
        System.out.println("Attempting to save user: " + user.getEmail() + ", Name: " + user.getName());
        try {
            UserInfo savedUser = userRepository.save(user);
            System.out.println("User saved successfully with ID: " + savedUser.getId()); // Log sau khi save thành công
            return savedUser;
        } catch (Exception e) {
            System.err.println("Error saving user: " + user.getEmail());
            e.printStackTrace(); // In ra stack trace của lỗi khi save
            throw e; // Ném lại exception để controller hoặc @Transactional xử lý
        }
    }
}
