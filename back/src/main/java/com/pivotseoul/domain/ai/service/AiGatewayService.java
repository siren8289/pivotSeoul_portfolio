package com.pivotseoul.domain.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.pivotseoul.domain.ai.application.port.out.FastApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * FastAPI 게이트웨이의 비즈니스 플로우를 담당하는 서비스입니다.
 * 컨트롤러는 HTTP 요청을 그대로 위임하고, 서비스는 경로 매핑과 외부 호출을 조합합니다.
 */
@Service
public class AiGatewayService {

    private static final String API_V1 = "/api/v1";

    private final FastApiClient fastApiClient;
    private final String fastApiBaseUrl;

    public AiGatewayService(
            FastApiClient fastApiClient,
            @Value("${pivotseoul.ai.fastapi-base-url:http://127.0.0.1:8000}") String fastApiBaseUrl) {
        this.fastApiClient = fastApiClient;
        this.fastApiBaseUrl = trimTrailingSlash(fastApiBaseUrl);
    }

    private static String trimTrailingSlash(String url) {
        if (url == null || url.isEmpty()) {
            return "http://127.0.0.1:8000";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public ResponseEntity<JsonNode> postJson(String fastApiPath, JsonNode body) {
        return fastApiClient.post(fastApiPath, body);
    }

    public ResponseEntity<JsonNode> getJson(String fastApiPath) {
        return fastApiClient.get(fastApiPath);
    }

    public Map<String, Object> bridgeStatus() {
        return fastApiClient.bridgeStatus(fastApiBaseUrl);
    }

    public ResponseEntity<JsonNode> housingAnalyze(JsonNode body) {
        return postJson(API_V1 + "/housing/analyze", body);
    }

    public ResponseEntity<JsonNode> careerRecommend(JsonNode body) {
        return postJson(API_V1 + "/career/recommend", body);
    }

    public ResponseEntity<JsonNode> childcareAnalyze(JsonNode body) {
        return postJson(API_V1 + "/childcare/analyze", body);
    }

    public ResponseEntity<JsonNode> seniorAnalyze(JsonNode body) {
        return postJson(API_V1 + "/senior/analyze", body);
    }

    public ResponseEntity<JsonNode> policyRecommend(JsonNode body) {
        return postJson(API_V1 + "/policy/recommend", body);
    }

    public ResponseEntity<JsonNode> simulationRun(JsonNode body) {
        return postJson(API_V1 + "/simulation/run", body);
    }

    public ResponseEntity<JsonNode> llmExplanationGenerate(JsonNode body) {
        return postJson(API_V1 + "/llm-explanation/generate", body);
    }

    public ResponseEntity<JsonNode> dataSourceSources() {
        return getJson(API_V1 + "/data-source/sources");
    }

    public ResponseEntity<JsonNode> dataSourceIngest(JsonNode body) {
        return postJson(API_V1 + "/data-source/ingest", body);
    }
}
