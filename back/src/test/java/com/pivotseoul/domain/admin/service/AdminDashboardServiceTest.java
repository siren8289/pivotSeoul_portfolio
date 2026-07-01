package com.pivotseoul.domain.admin.service;

import com.pivotseoul.domain.data.service.DataSourceService;
import com.pivotseoul.domain.data.service.DataValidationService;
import com.pivotseoul.domain.simulation.repository.ScenarioResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceTest {

    @Mock
    private ScenarioResultRepository scenarioResultRepository;

    @Mock
    private DataSourceService dataSourceService;

    @Mock
    private DataValidationService dataValidationService;

    @InjectMocks
    private AdminDashboardService adminDashboardService;

    @Test
    void getDashboardShouldQueryValidationResultsOnce() {
        when(scenarioResultRepository.findAll()).thenReturn(List.of());
        when(dataValidationService.getValidationResults()).thenReturn(List.of());
        when(dataSourceService.getDatasetStatuses()).thenReturn(List.of());

        adminDashboardService.getDashboard();

        verify(dataValidationService, times(1)).getValidationResults();
    }
}
