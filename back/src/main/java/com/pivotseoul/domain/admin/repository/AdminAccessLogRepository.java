package com.pivotseoul.domain.admin.repository;

import com.pivotseoul.domain.admin.entity.AdminAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 관리자 접근 이력 조회/저장을 담당하는 저장소입니다.
 */
public interface AdminAccessLogRepository extends JpaRepository<AdminAccessLog, Long> {
}
