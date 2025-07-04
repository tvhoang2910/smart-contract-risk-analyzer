package vn.techmaster.nowj.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.techmaster.nowj.entity.UserInfo;

public interface UserRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmail(String email);

    boolean existsByEmail(String email);

    UserInfo getUserByEmail(String email);
}
