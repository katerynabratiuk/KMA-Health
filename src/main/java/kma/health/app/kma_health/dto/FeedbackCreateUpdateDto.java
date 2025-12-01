package kma.health.app.kma_health.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.entity.Patient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FeedbackCreateUpdateDto {

    private LocalDate date;

    @NotNull
    @Min(0)
    @Max(5)
    private Short score;

    private String comment;

    private Long hospital_id;

    private UUID doctor_id;
    private UUID patient_id;

    public static FeedbackCreateUpdateDto fromEntity(Feedback entity){
        FeedbackCreateUpdateDto dto = new FeedbackCreateUpdateDto();
        dto.setDate(entity.getDate());
        dto.setComment(entity.getComment());
        dto.setScore(entity.getScore());
        if (entity.getDoctor() != null)
        {
            dto.setDoctor_id(entity.getDoctor().getId());
        }
        else if (entity.getHospital() != null)
        {
            dto.setHospital_id(entity.getHospital().getId());
        }
        dto.setPatient_id(entity.getPatient().getId());
        return dto;
    }

    public static Feedback toEntity(FeedbackCreateUpdateDto dto){
        Feedback entity = new Feedback();
        entity.setDate(dto.getDate());
        entity.setComment(dto.getComment());
        entity.setScore(dto.getScore());
        if (dto.getDoctor_id() != null)
        {
            entity.setDoctor(new Doctor());
            entity.getDoctor().setId(dto.getDoctor_id());
        }
        else if (dto.getHospital_id() != null)
        {
            entity.setHospital(new Hospital());
            entity.getHospital().setId(dto.getHospital_id());
        }

        entity.setPatient(new Patient());
        entity.getPatient().setId(dto.getPatient_id());
        return entity;
    }

}
