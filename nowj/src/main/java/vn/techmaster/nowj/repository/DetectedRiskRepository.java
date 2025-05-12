package vn.techmaster.nowj.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.techmaster.nowj.entity.DetectedRiskInfo;

public interface DetectedRiskRepository extends JpaRepository<DetectedRiskInfo, Long> {

}
