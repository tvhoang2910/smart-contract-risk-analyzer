package vn.techmaster.nowj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.techmaster.nowj.entity.RoleInfo;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleInfo, Long> {
    Optional<RoleInfo> findByCode(String code);
}
