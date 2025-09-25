package kma.health.app.kma_health.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "appointment")
public class Appointment {
    @Id
    private UUID id;

    private LocalDate date;
    private LocalTime time;

    @ManyToOne
    @JoinColumn(name = "doctor_passport")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @ManyToOne
    @JoinColumn(name = "referral_id")
    private Referral referral;

    @ManyToOne
    @JoinColumn(name = "lab_assistant_id")
    private LabAssistant labAssistant;

    private String diagnosis;

    @OneToMany(mappedBy = "appointment")
    private Set<MedicalFile> medicalFiles;
}

