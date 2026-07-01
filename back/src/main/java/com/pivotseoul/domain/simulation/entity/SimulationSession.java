package com.pivotseoul.domain.simulation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "simulation_session")
public class SimulationSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "session_uuid", nullable = false, unique = true, length = 64)
    private String sessionUuid;

    @Column(name = "life_stage_code", nullable = false, length = 32)
    private String lifeStageCode;

    @Column(name = "session_status", nullable = false, length = 32)
    private String sessionStatus;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected SimulationSession() {
    }

    public static SimulationSession create(
            String sessionUuid,
            String lifeStageCode,
            String sessionStatus,
            Instant createdAt
    ) {
        SimulationSession session = new SimulationSession();
        session.setSessionUuid(sessionUuid);
        session.setLifeStageCode(lifeStageCode);
        session.setSessionStatus(sessionStatus);
        session.setCreatedAt(createdAt);
        return session;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionUuid() {
        return sessionUuid;
    }

    public void setSessionUuid(String sessionUuid) {
        this.sessionUuid = sessionUuid;
    }

    public String getLifeStageCode() {
        return lifeStageCode;
    }

    public void setLifeStageCode(String lifeStageCode) {
        this.lifeStageCode = lifeStageCode;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}
