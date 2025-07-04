package vn.techmaster.nowj.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Getter
@Setter
public class UserDTO {
    private String email;
    private String password;
    private String name;
    private List<RoleDTO> roles;
    private String RoleName;
    private String RoleCode;
    private Map<String, String> roleDTOs = new HashMap<>();
}
