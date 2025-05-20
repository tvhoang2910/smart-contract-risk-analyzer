package vn.techmaster.nowj.service;

import vn.techmaster.nowj.entity.UserInfo;

import java.util.Optional;

public interface UserService {
    Optional<UserInfo> findByEmail(String email);
    UserInfo save(UserInfo user);
    UserInfo registerLocalUser(String email, String password, String name);
}
