package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.Doctor;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {
    private String passportNumber;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String type;
    private String doctorType;

    public DoctorDto(Doctor doctor) {
        this.passportNumber = doctor.getPassportNumber();
        this.fullName = doctor.getFullName();
        this.phoneNumber = doctor.getPhoneNumber();
        this.email = doctor.getEmail();
        this.type = doctor.getType();
        this.doctorType = doctor.getDoctorType().getTypeName();
    }
}