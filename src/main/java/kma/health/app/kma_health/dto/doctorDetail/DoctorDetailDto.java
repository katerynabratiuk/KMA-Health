package kma.health.app.kma_health.dto.doctorDetail;

import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.Feedback;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DoctorDetailDto {

    private UUID id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String type;
    private String doctorType;
    private LocalDate startedWorking;

    private String profilePictureUrl;
    private Double rating;
    private int yearsOfExperience;
    private String description;
    private List<Feedback> feedback;
    private HospitalDto hospital;

    private Boolean canGetAppointment;
    private Boolean canRate;

    public DoctorDetailDto(Doctor doctor) {
        this.id = doctor.getId();
        this.fullName = doctor.getFullName();
        this.phoneNumber = doctor.getPhoneNumber();
        this.email = doctor.getEmail();
        this.type = doctor.getType();
        this.doctorType = doctor.getDoctorType().getTypeName();
        this.startedWorking = doctor.getStartedWorking();
        this.profilePictureUrl = doctor.getProfilePictureUrl();
        this.rating = doctor.getRating();
        this.yearsOfExperience = doctor.getYearsOfExperience();
        this.description = doctor.getDescription();
        this.hospital = new HospitalDto(doctor.getHospital());
        this.feedback = new ArrayList<>();
    }
}