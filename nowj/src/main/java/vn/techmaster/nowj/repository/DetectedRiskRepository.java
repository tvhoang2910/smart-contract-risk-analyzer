package vn.techmaster.nowj.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.techmaster.nowj.entity.DetectedRiskInfo;

public interface DetectedRiskRepository extends JpaRepository<DetectedRiskInfo, Long> {
    List<DetectedRiskInfo> findAllByContract_Id(Long id);

    void deleteAllByContract_Id(Long id);
}
