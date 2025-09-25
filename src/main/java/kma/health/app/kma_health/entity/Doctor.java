package kma.health.app.kma_health.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "doctor")
public class Doctor {
    @Id
    @Column(name = "passport_number")
    private String passportNumber;

    private String email;
    private String phoneNumber;

    @Column(name = "full_name")
    private String fullName;

    private LocalDate birthDate;

    private String type; // adult/child

    @ManyToOne
    @JoinColumn(name = "doctor_type_id")
    private DoctorType doctorType;

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @OneToMany(mappedBy = "doctor")
    private Set<Declaration> declarations;

    @OneToMany(mappedBy = "doctor")
    private Set<Referral> referrals;

    @OneToMany(mappedBy = "doctor")
    private Set<Appointment> appointments;

    @OneToMany(mappedBy = "doctor")
    private Set<Feedback> feedbacks;
}
