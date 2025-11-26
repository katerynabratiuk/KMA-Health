package kma.health.app.kma_health.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "referral")
public class Referral {
    @Id
    private UUID id;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_type_id")
    private DoctorType doctorType;

    @ManyToOne
    @JoinColumn(name = "examination_type_id")
    private Examination examination;

    @OneToMany(mappedBy = "referral")
    private Set<Appointment> appointments;
}

