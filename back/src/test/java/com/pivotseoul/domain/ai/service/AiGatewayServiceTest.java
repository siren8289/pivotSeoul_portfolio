package com.pivotseoul.domain.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivotseoul.domain.ai.application.port.out.FastApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiGatewayServiceTest {

    @Test
    void bridgeStatusShouldDelegateToFastApiClient() {
        FastApiClient fastApiClient = mock(FastApiClient.class);
        when(fastApiClient.bridgeStatus("http://127.0.0.1:8000"))
                .thenReturn(Map.of("role", "gateway"));

        AiGatewayService service = new AiGatewayService(fastApiClient, "http://127.0.0.1:8000");

        Map<String, Object> status = service.bridgeStatus();

        assertThat(status).containsEntry("role", "gateway");
        verify(fastApiClient).bridgeStatus("http://127.0.0.1:8000");
    }

    @Test
    void housingAnalyzeShouldDelegateToFastApiClient() {
        FastApiClient fastApiClient = mock(FastApiClient.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode body = objectMapper.createObjectNode();
        ResponseEntity<JsonNode> expected = ResponseEntity.ok(body);
        when(fastApiClient.post("/api/v1/housing/analyze", body)).thenReturn(expected);

        AiGatewayService service = new AiGatewayService(fastApiClient, "http://127.0.0.1:8000");

        ResponseEntity<JsonNode> result = service.housingAnalyze(body);

        assertThat(result).isEqualTo(expected);
        verify(fastApiClient).post("/api/v1/housing/analyze", body);
    }
}
