package kma.health.app.kma_health.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kma.health.app.kma_health.enums.AppointmentStatus;
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
    @GeneratedValue
    private UUID id;

    @NotNull
    private LocalDate date;
    @NotNull
    private LocalTime time;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AppointmentStatus status;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
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

