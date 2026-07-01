package com.pivotseoul.domain.admin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 관리자 권한/역할 정보를 담기 위한 엔티티입니다.
 * 현재는 기본 식별자만 정의되어 있으며, 역할명·권한 목록 등은 추후 확장 예정입니다.
 */
@Entity
@Table(name = "admin_role")
public class AdminRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected AdminRole() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
