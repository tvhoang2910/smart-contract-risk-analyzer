package vn.techmaster.nowj.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegistrationRequestDTO {
    private String email;
    private String password;
    private String confirmPassword;
    private String fullName;
}
