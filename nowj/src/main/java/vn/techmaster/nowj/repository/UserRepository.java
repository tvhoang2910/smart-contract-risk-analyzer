package vn.techmaster.nowj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.techmaster.nowj.entity.UserInfo;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmail(String email);
}
