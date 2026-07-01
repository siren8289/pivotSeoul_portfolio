package com.pivotseoul.domain.admin.service;

import com.pivotseoul.domain.admin.dto.AdminDashboardResponse;
import com.pivotseoul.domain.admin.dto.AdminSummaryResponse;
import com.pivotseoul.domain.data.dto.DatasetResponse;
import com.pivotseoul.domain.data.dto.ValidationResultResponse;
import com.pivotseoul.domain.data.service.DataSourceService;
import com.pivotseoul.domain.data.service.DataValidationService;
import com.pivotseoul.domain.simulation.entity.ScenarioResult;
import com.pivotseoul.domain.simulation.repository.ScenarioResultRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

/**
 * 관리자 대시보드에서 필요한 집계·상태값을 조합하는 애플리케이션 서비스입니다.
 * 비즈니스 로직은 이 계층에서 처리하고, 컨트롤러는 그 결과를 단순히 전달합니다.
 */
@Service
public class AdminDashboardService {

    private static final String STATUS_NORMAL = "normal";
    private static final String STATUS_WARNING = "warning";
    private static final String VALIDATION_LEVEL_ERROR = "error";
    private static final String VALIDATION_LEVEL_WARN = "warn";

    private final ScenarioResultRepository scenarioResultRepository;
    private final DataSourceService dataSourceService;
    private final DataValidationService dataValidationService;

    public AdminDashboardService(
            ScenarioResultRepository scenarioResultRepository,
            DataSourceService dataSourceService,
            DataValidationService dataValidationService
    ) {
        this.scenarioResultRepository = scenarioResultRepository;
        this.dataSourceService = dataSourceService;
        this.dataValidationService = dataValidationService;
    }

    public AdminDashboardResponse getDashboard() {
        DashboardData dashboardData = loadDashboardData();
        AdminSummaryResponse summary = buildSummary(dashboardData.results(), dashboardData.validationResults());
        AdminDashboardResponse.DataOperationStatus dataOperationStatus = buildDataOperationStatus(
                dashboardData.datasets(),
                dashboardData.validationResults());

        return new AdminDashboardResponse(
                summary,
                getServiceStatuses(dataOperationStatus),
                getAiStatuses(),
                dataOperationStatus,
                getPromptRecommendationStatus()
        );
    }

    public AdminSummaryResponse getSummary() {
        DashboardData dashboardData = loadDashboardData();
        return buildSummary(dashboardData.results(), dashboardData.validationResults());
    }

    public List<AdminDashboardResponse.ServiceMetric> getServiceStatuses() {
        return getServiceStatuses(getDataOperationStatus());
    }

    private List<AdminDashboardResponse.ServiceMetric> getServiceStatuses(AdminDashboardResponse.DataOperationStatus dataOperationStatus) {
        return List.of(
                new AdminDashboardResponse.ServiceMetric("api", "Spring API", 48, "ms", STATUS_NORMAL, 200),
                new AdminDashboardResponse.ServiceMetric("simulation", "결과 조회", 34, "%", STATUS_NORMAL, 100),
                new AdminDashboardResponse.ServiceMetric("data", "데이터 운영", 1, "건", dataOperationStatus.status(), 5)
        );
    }

    public List<AdminDashboardResponse.AiStatus> getAiStatuses() {
        return List.of(
                new AdminDashboardResponse.AiStatus("gateway", "AI Gateway", STATUS_NORMAL, 82, "방금 전"),
                new AdminDashboardResponse.AiStatus("recommendation", "추천 모델", STATUS_NORMAL, 144, "1분 전"),
                new AdminDashboardResponse.AiStatus("explanation", "결과 해석", STATUS_WARNING, 310, "3분 전")
        );
    }

    public AdminDashboardResponse.DataOperationStatus getDataOperationStatus() {
        DashboardData dashboardData = loadDashboardData();
        return buildDataOperationStatus(dashboardData.datasets(), dashboardData.validationResults());
    }

    public AdminDashboardResponse.PromptRecommendationStatus getPromptRecommendationStatus() {
        return new AdminDashboardResponse.PromptRecommendationStatus(
                STATUS_NORMAL,
                "v1.0-placeholder",
                "v1.0-placeholder",
                Instant.now().toString()
        );
    }

    private DashboardData loadDashboardData() {
        List<ScenarioResult> results = scenarioResultRepository.findAll();
        List<ValidationResultResponse> validationResults = dataValidationService.getValidationResults();
        List<DatasetResponse> datasets = dataSourceService.getDatasetStatuses();
        return new DashboardData(results, validationResults, datasets);
    }

    private AdminSummaryResponse buildSummary(
            List<ScenarioResult> results,
            List<ValidationResultResponse> validationResults) {
        double averageRiskScore = calculateAverageRiskScore(results);
        return new AdminSummaryResponse(
                0,
                results.size(),
                round(averageRiskScore),
                resolveErrorRate(validationResults)
        );
    }

    private AdminDashboardResponse.DataOperationStatus buildDataOperationStatus(
            List<DatasetResponse> datasets,
            List<ValidationResultResponse> validationResults) {
        long warningCount = countWarnings(validationResults);
        String status = warningCount == 0 ? STATUS_NORMAL : STATUS_WARNING;

        return new AdminDashboardResponse.DataOperationStatus(
                status,
                datasets.size(),
                warningCount,
                Instant.now().toString()
        );
    }

    private double calculateAverageRiskScore(List<ScenarioResult> results) {
        return results.stream()
                .map(ScenarioResult::getRiskScore)
                .filter(score -> score != null)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0);
    }

    private long countWarnings(List<ValidationResultResponse> validationResults) {
        return validationResults.stream()
                .filter(result -> VALIDATION_LEVEL_WARN.equals(result.level()) || VALIDATION_LEVEL_ERROR.equals(result.level()))
                .count();
    }

    private double resolveErrorRate(List<ValidationResultResponse> validationResults) {
        if (validationResults.isEmpty()) {
            return 0;
        }
        long errorCount = validationResults.stream()
                .filter(result -> VALIDATION_LEVEL_ERROR.equals(result.level()))
                .count();
        return round((double) errorCount / validationResults.size() * 100);
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private record DashboardData(
            List<ScenarioResult> results,
            List<ValidationResultResponse> validationResults,
            List<DatasetResponse> datasets) {
    }
}
