package com.pivotseoul.domain.simulation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivotseoul.domain.simulation.application.port.out.AiHousingAnalysisClient;
import com.pivotseoul.domain.simulation.repository.ScenarioRepository;
import com.pivotseoul.domain.simulation.repository.SimulationRunRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SimulationEngineServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void parseAiAnalysisShouldUseDefaultsWhenFieldsMissing() {
        SimulationEngineService service = new SimulationEngineService(
                mock(AiHousingAnalysisClient.class),
                objectMapper,
                mock(SimulationRunRepository.class),
                mock(CalculationLogService.class),
                mock(SimulationResultSaveService.class),
                mock(ScenarioRepository.class)
        );

        JsonNode aiResult = objectMapper.createObjectNode();

        SimulationEngineService.AiAnalysisResult analysis = service.parseAiAnalysis(aiResult);

        assertThat(analysis.resultStatus()).isEqualTo("UNKNOWN");
        assertThat(analysis.riskScore()).isEqualTo(0);
        assertThat(analysis.confidenceScore()).isEqualTo(0.0);
        assertThat(analysis.rir()).isNull();
        assertThat(analysis.isRedZone()).isFalse();
    }
}
