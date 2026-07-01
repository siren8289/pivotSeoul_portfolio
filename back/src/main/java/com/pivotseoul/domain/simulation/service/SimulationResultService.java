package com.pivotseoul.domain.simulation.service;

import com.pivotseoul.domain.simulation.dto.CreateSessionResponse;
import org.springframework.stereotype.Service;

@Service
public class SimulationResultService {

    public CreateSessionResponse getResult(Long simulationSessionId) {
        return new CreateSessionResponse("session-" + simulationSessionId);
    }
}
