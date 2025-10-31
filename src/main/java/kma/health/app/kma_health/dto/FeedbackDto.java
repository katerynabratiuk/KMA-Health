package kma.health.app.kma_health.dto;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.entity.Hospital;
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
public class FeedbackDto {
    private Long id;

    private LocalDate date;

    private Short score;

    private String comment;

    private Long hospital_id;

    private UUID doctor_id;

    public static FeedbackDto fromEntity(Feedback feedback){
        FeedbackDto dto = new FeedbackDto();
        dto.setId(feedback.getId());
        dto.setDate(feedback.getDate());
        dto.setComment(feedback.getComment());
        dto.setScore(feedback.getScore());
        if (feedback.getDoctor() != null)
        {
            dto.setDoctor_id(feedback.getDoctor().getId());
        }
        else if (feedback.getHospital() != null)
        {
            dto.setHospital_id(feedback.getHospital().getId());
        }
        return dto;
    }

    public static Feedback toEntity(FeedbackDto dto){
        Feedback entity = new Feedback();
        entity.setId(dto.getId());
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
        return entity;
    }

}
