package com.pivotseoul.domain.admin.dto;

/**
 * 관리자 대시보드의 요약 지표를 담는 응답 DTO입니다.
 */
public record AdminSummaryResponse(
        long activeUsers,
        long totalSimulations,
        double averageRiskScore,
        double errorRate
) {
}
