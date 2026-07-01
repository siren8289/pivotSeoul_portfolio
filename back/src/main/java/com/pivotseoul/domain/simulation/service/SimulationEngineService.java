package com.pivotseoul.domain.simulation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivotseoul.domain.simulation.application.port.out.AiHousingAnalysisClient;
import com.pivotseoul.domain.simulation.dto.RunSimulationRequest;
import com.pivotseoul.domain.simulation.dto.RunSimulationResponse;
import com.pivotseoul.domain.simulation.entity.Scenario;
import com.pivotseoul.domain.simulation.entity.SimulationRun;
import com.pivotseoul.domain.simulation.repository.ScenarioRepository;
import com.pivotseoul.domain.simulation.repository.SimulationRunRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class SimulationEngineService {

    private static final double HOUSING_RED_ZONE_THRESHOLD = 0.4;
    private static final String RUNNING_STATUS = "RUNNING";
    private static final String COMPLETED_STATUS = "COMPLETED";
    private static final String FAILED_STATUS = "FAILED";
    private static final String UNKNOWN_STATUS = "UNKNOWN";
    private static final String CALC_ENGINE_VERSION = "SPRING_ENGINE_V1";
    private static final String AI_PIPELINE_VERSION = "FASTAPI_HOUSING_V1";
    private static final String MODEL_VERSION = "RULE_BASED_V1";
    private static final String FASTAPI_ERROR = "FASTAPI_ERROR";
    private static final String FASTAPI_DETAIL = "FastAPI request failed";

    private final AiHousingAnalysisClient aiHousingAnalysisClient;
    private final ObjectMapper objectMapper;
    private final SimulationRunRepository simulationRunRepository;
    private final CalculationLogService calculationLogService;
    private final SimulationResultSaveService simulationResultSaveService;
    private final ScenarioRepository scenarioRepository;

    public SimulationEngineService(
            AiHousingAnalysisClient aiHousingAnalysisClient,
            ObjectMapper objectMapper,
            SimulationRunRepository simulationRunRepository,
            CalculationLogService calculationLogService,
            SimulationResultSaveService simulationResultSaveService,
            ScenarioRepository scenarioRepository) {
        this.aiHousingAnalysisClient = aiHousingAnalysisClient;
        this.objectMapper = objectMapper;
        this.simulationRunRepository = simulationRunRepository;
        this.calculationLogService = calculationLogService;
        this.simulationResultSaveService = simulationResultSaveService;
        this.scenarioRepository = scenarioRepository;
    }

    public ResponseEntity<RunSimulationResponse> runSimulation(
            String sessionId,
            RunSimulationRequest request) {
        Long numericSessionId = parseSessionId(sessionId);
        SimulationRun simulationRun = createRunningSimulationRun(numericSessionId);
        JsonNode aiRequestBody = objectMapper.valueToTree(request);

        ResponseEntity<JsonNode> aiResponse = aiHousingAnalysisClient.analyzeHousing(aiRequestBody);
        JsonNode aiResult = aiResponse.getBody();

        if (!aiResponse.getStatusCode().is2xxSuccessful()) {
            return handleFailure(sessionId, simulationRun, aiRequestBody, aiResult, aiResponse);
        }

        AiAnalysisResult analysis = parseAiAnalysis(aiResult);
        completeRun(simulationRun, analysis.confidenceScore());
        calculationLogService.saveSuccessLog(
                simulationRun.getSimulationRunId(),
                aiRequestBody,
                aiResult);

        Long scenarioId = resolveScenarioId(numericSessionId);
        simulationResultSaveService.saveHousingResult(
                simulationRun.getSimulationRunId(),
                scenarioId,
                aiResult);

        return ResponseEntity.status(aiResponse.getStatusCode())
                .body(buildSuccessResponse(sessionId, analysis, aiResult));
    }

    public AiAnalysisResult parseAiAnalysis(JsonNode aiResult) {
        return new AiAnalysisResult(
                extractText(aiResult, "housing_status", UNKNOWN_STATUS).toUpperCase(),
                extractInt(aiResult, "risk_score", 0),
                extractDouble(aiResult, "confidence_score", 0.0),
                extractDouble(aiResult, "rir", null),
                extractBoolean(aiResult, "is_red_zone", false));
    }

    public record AiAnalysisResult(
            String resultStatus,
            Integer riskScore,
            Double confidenceScore,
            Double rir,
            Boolean isRedZone) {
    }

    private ResponseEntity<RunSimulationResponse> handleFailure(
            String sessionId,
            SimulationRun simulationRun,
            JsonNode aiRequestBody,
            JsonNode aiResult,
            ResponseEntity<JsonNode> aiResponse) {
        failRun(simulationRun);
        calculationLogService.saveFailureLog(
                simulationRun.getSimulationRunId(),
                aiRequestBody,
                aiResult,
                extractText(aiResult, "error", FASTAPI_ERROR),
                extractText(aiResult, "detail", FASTAPI_DETAIL));

        return ResponseEntity.status(aiResponse.getStatusCode())
                .body(buildFailureResponse(sessionId, aiResult));
    }

    private SimulationRun createRunningSimulationRun(Long sessionId) {
        SimulationRun simulationRun = new SimulationRun();
        simulationRun.setSessionId(sessionId);
        simulationRun.setRunStatus(RUNNING_STATUS);
        simulationRun.setCalculationEngineVersion(CALC_ENGINE_VERSION);
        simulationRun.setAiPipelineVersion(AI_PIPELINE_VERSION);
        simulationRun.setModelVersion(MODEL_VERSION);
        simulationRun.setStartedAt(Instant.now());

        return simulationRunRepository.save(simulationRun);
    }

    private void failRun(SimulationRun simulationRun) {
        simulationRun.setRunStatus(FAILED_STATUS);
        simulationRun.setCompletedAt(Instant.now());
        simulationRunRepository.save(simulationRun);
    }

    private void completeRun(SimulationRun simulationRun, Double confidenceScore) {
        simulationRun.setRunStatus(COMPLETED_STATUS);
        simulationRun.setTotalConfidenceScore(BigDecimal.valueOf(confidenceScore));
        simulationRun.setCompletedAt(Instant.now());
        simulationRunRepository.save(simulationRun);
    }

    private RunSimulationResponse buildFailureResponse(String sessionId, JsonNode aiResult) {
        return new RunSimulationResponse(
                sessionId,
                FAILED_STATUS,
                UNKNOWN_STATUS,
                0,
                0.0,
                List.of(),
                aiResult);
    }

    private RunSimulationResponse buildSuccessResponse(
            String sessionId,
            AiAnalysisResult analysis,
            JsonNode aiResult) {
        RunSimulationResponse.ThresholdResultItem housingThreshold = new RunSimulationResponse.ThresholdResultItem(
                "HOUSING",
                analysis.rir(),
                HOUSING_RED_ZONE_THRESHOLD,
                analysis.resultStatus(),
                analysis.isRedZone());

        return new RunSimulationResponse(
                sessionId,
                COMPLETED_STATUS,
                analysis.resultStatus(),
                analysis.riskScore(),
                analysis.confidenceScore(),
                List.of(housingThreshold),
                aiResult);
    }

    private Long resolveScenarioId(Long sessionId) {
        Scenario scenario = scenarioRepository
                .findFirstBySessionIdOrderByDisplayOrderAscScenarioIdAsc(sessionId)
                .orElseThrow(() -> new IllegalStateException(
                        "Scenario is not initialized for sessionId=" + sessionId));

        return scenario.getScenarioId();
    }

    private Long parseSessionId(String sessionId) {
        try {
            return Long.parseLong(sessionId);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private String extractText(JsonNode node, String fieldName, String defaultValue) {
        if (node == null || node.get(fieldName) == null || node.get(fieldName).isNull()) {
            return defaultValue;
        }

        return node.get(fieldName).asText(defaultValue);
    }

    private Integer extractInt(JsonNode node, String fieldName, Integer defaultValue) {
        if (node == null || node.get(fieldName) == null || node.get(fieldName).isNull()) {
            return defaultValue;
        }

        return node.get(fieldName).asInt(defaultValue);
    }

    private Double extractDouble(JsonNode node, String fieldName, Double defaultValue) {
        if (node == null || node.get(fieldName) == null || node.get(fieldName).isNull()) {
            return defaultValue;
        }

        return node.get(fieldName).asDouble(defaultValue);
    }

    private Boolean extractBoolean(JsonNode node, String fieldName, Boolean defaultValue) {
        if (node == null || node.get(fieldName) == null || node.get(fieldName).isNull()) {
            return defaultValue;
        }

        return node.get(fieldName).asBoolean(defaultValue);
    }
}
