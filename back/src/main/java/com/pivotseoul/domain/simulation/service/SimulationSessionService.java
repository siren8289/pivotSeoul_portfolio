package com.pivotseoul.domain.simulation.service;

import com.pivotseoul.domain.simulation.dto.CreateSessionRequest;
import com.pivotseoul.domain.simulation.dto.CreateSessionResponse;
import com.pivotseoul.domain.simulation.entity.SimulationSession;
import com.pivotseoul.domain.simulation.repository.SimulationSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class SimulationSessionService {

    private final SimulationSessionRepository simulationSessionRepository;

    public SimulationSessionService(SimulationSessionRepository simulationSessionRepository) {
        this.simulationSessionRepository = simulationSessionRepository;
    }

    @Transactional
    public CreateSessionResponse createSession(CreateSessionRequest request) {
        String sessionUuid = UUID.randomUUID().toString();
        SimulationSession session = SimulationSession.create(
                sessionUuid,
                request.lifeStageCode(),
                "CREATED",
                Instant.now()
        );

        simulationSessionRepository.save(session);
        return new CreateSessionResponse(sessionUuid);
    }
}