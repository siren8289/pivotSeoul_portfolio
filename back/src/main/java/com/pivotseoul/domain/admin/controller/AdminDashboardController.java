package com.pivotseoul.domain.admin.controller;

import com.pivotseoul.domain.admin.dto.AdminDashboardResponse;
import com.pivotseoul.domain.admin.dto.AdminSummaryResponse;
import com.pivotseoul.domain.admin.service.AdminDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 관리자 대시보드의 HTTP 진입점입니다.
 * 컨트롤러는 요청을 받아 서비스에 위임하고, 응답 DTO를 그대로 반환하는 역할만 담당합니다.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/dashboard")
    public AdminDashboardResponse getDashboard() {
        return adminDashboardService.getDashboard();
    }

    @GetMapping("/summary")
    public AdminSummaryResponse getSummary() {
        return adminDashboardService.getSummary();
    }

    @GetMapping("/services")
    public List<AdminDashboardResponse.ServiceMetric> getServiceStatuses() {
        return adminDashboardService.getServiceStatuses();
    }

    @GetMapping("/ai/status")
    public List<AdminDashboardResponse.AiStatus> getAiStatuses() {
        return adminDashboardService.getAiStatuses();
    }

    @GetMapping("/data/status")
    public AdminDashboardResponse.DataOperationStatus getDataOperationStatus() {
        return adminDashboardService.getDataOperationStatus();
    }

    @GetMapping("/recommendations/status")
    public AdminDashboardResponse.PromptRecommendationStatus getPromptRecommendationStatus() {
        return adminDashboardService.getPromptRecommendationStatus();
    }
}
