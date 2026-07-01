package com.pivotseoul.domain.admin.dto;

import java.util.List;

/**
 * 관리자 대시보드의 최상위 응답 포맷입니다.
 * 서비스 계층에서 계산한 요약/서비스 상태/AI 상태/데이터 상태를 한 번에 전달합니다.
 */
public record AdminDashboardResponse(
        AdminSummaryResponse summary,
        List<ServiceMetric> serviceStatuses,
        List<AiStatus> aiStatuses,
        DataOperationStatus dataOperation,
        PromptRecommendationStatus promptRecommendation
) {

    public record ServiceMetric(
            String id,
            String label,
            int value,
            String unit,
            String status,
            int max
    ) {
    }

    public record AiStatus(
            String id,
            String label,
            String status,
            int latencyMs,
            String updatedAt
    ) {
    }

    public record DataOperationStatus(
            String status,
            long datasetCount,
            long validationWarningCount,
            String lastValidatedAt
    ) {
    }

    public record PromptRecommendationStatus(
            String status,
            String promptVersion,
            String recommendationVersion,
            String updatedAt
    ) {
    }
}
