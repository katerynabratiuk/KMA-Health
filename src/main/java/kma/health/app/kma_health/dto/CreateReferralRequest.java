package kma.health.app.kma_health.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateReferralRequest {
    private UUID patientId;
    private String doctorTypeName;
    private Long examinationId;
}
