package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.DoctorType;
import kma.health.app.kma_health.entity.Referral;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
public class ReferralDto {
    private UUID id;
    private LocalDate validUntil;
    private UUID doctorId;
    private String doctorFullName;
    private DoctorType doctorType;
    private ExaminationDto examination;

    public static ReferralDto fromEntity(Referral referral) {
        ReferralDto referralDto = new ReferralDto();
        referralDto.setId(referral.getId());
        referralDto.setValidUntil(referral.getValidUntil());
        referralDto.setDoctorId(referral.getDoctor().getId());
        referralDto.setDoctorFullName(referral.getDoctor().getFullName());
        referralDto.setDoctorType(referral.getDoctorType());
        if (referral.getExamination() != null)
            referralDto.setExamination(new ExaminationDto(
                    referral.getExamination().getExamName(),
                    referral.getExamination().getUnit()
            ));
        else
            referralDto.setExamination(null);
        return referralDto;
    }
}
