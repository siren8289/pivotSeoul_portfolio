package com.pivotseoul.domain.admin.repository;

import com.pivotseoul.domain.admin.entity.AdminRole;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 관리자 역할 정보를 조회/저장하는 저장소입니다.
 */
public interface AdminRoleRepository extends JpaRepository<AdminRole, Long> {
}
