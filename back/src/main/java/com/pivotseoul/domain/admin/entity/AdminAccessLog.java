package com.pivotseoul.domain.admin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 관리자 접근 이력을 저장하기 위한 엔티티입니다.
 * 현재는 기본 구조만 존재하며, 실제 접근 시각·대상·결과 같은 필드는 이후 확장 예정입니다.
 */
@Entity
@Table(name = "admin_access_log")
public class AdminAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected AdminAccessLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
