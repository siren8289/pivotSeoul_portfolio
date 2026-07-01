package com.pivotseoul.domain.ai.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.pivotseoul.domain.ai.service.AiGatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * AI 기능에 대한 HTTP 진입점입니다.
 * 컨트롤러는 요청을 받아 서비스에 위임하고, 응답을 그대로 반환하는 역할만 담당합니다.
 */
@RestController
@RequestMapping("/api/ai")
public class AiGatewayController {

    private final AiGatewayService aiGatewayService;

    public AiGatewayController(AiGatewayService aiGatewayService) {
        this.aiGatewayService = aiGatewayService;
    }

    /**
     * 게이트웨이 및 FastAPI 연결 상태를 확인합니다.
     */
    @GetMapping("/status")
    public Map<String, Object> status() {
        return aiGatewayService.bridgeStatus();
    }

    /**
     * 요청 본문을 그대로 FastAPI 파이프라인에 전달합니다.
     */
    @PostMapping("/housing/analyze")
    public ResponseEntity<JsonNode> housingAnalyze(@RequestBody(required = false) JsonNode body) {
        return aiGatewayService.housingAnalyze(body);
    }

    @PostMapping("/career/recommend")
    public ResponseEntity<JsonNode> careerRecommend(@RequestBody(required = false) JsonNode body) {
        return aiGatewayService.careerRecommend(body);
    }

    @PostMapping("/childcare/analyze")
    public ResponseEntity<JsonNode> childcareAnalyze(@RequestBody(required = false) JsonNode body) {
        return aiGatewayService.childcareAnalyze(body);
    }

    @PostMapping("/senior/analyze")
    public ResponseEntity<JsonNode> seniorAnalyze(@RequestBody(required = false) JsonNode body) {
        return aiGatewayService.seniorAnalyze(body);
    }

    @PostMapping("/policy/recommend")
    public ResponseEntity<JsonNode> policyRecommend(@RequestBody(required = false) JsonNode body) {
        return aiGatewayService.policyRecommend(body);
    }

    @PostMapping("/simulation/run")
    public ResponseEntity<JsonNode> simulationRun(@RequestBody(required = false) JsonNode body) {
        return aiGatewayService.simulationRun(body);
    }

    @PostMapping("/llm-explanation/generate")
    public ResponseEntity<JsonNode> llmExplanationGenerate(@RequestBody(required = false) JsonNode body) {
        return aiGatewayService.llmExplanationGenerate(body);
    }

    @GetMapping("/data-source/sources")
    public ResponseEntity<JsonNode> dataSourceSources() {
        return aiGatewayService.dataSourceSources();
    }

    @PostMapping("/data-source/ingest")
    public ResponseEntity<JsonNode> dataSourceIngest(@RequestBody(required = false) JsonNode body) {
        return aiGatewayService.dataSourceIngest(body);
    }
}
