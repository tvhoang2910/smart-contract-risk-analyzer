package vn.techmaster.nowj.service;

import vn.techmaster.nowj.model.dto.RegistrationRequestDTO;
import vn.techmaster.nowj.model.dto.UserDTO;

public interface UserService {
    void registerUser(RegistrationRequestDTO registrationRequest);
    UserDTO getUserByEmail(String email);

}
