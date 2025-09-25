package kma.health.app.kma_health.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Pattern;

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
    @Size(min=9, max=9)
    private String passportNumber;

    @Email(message = "Invalid email")
    private String email;
    private String phoneNumber;

    @Column(name = "full_name")
    private String fullName;

    @PastOrPresent
    private LocalDate birthDate;

    @Pattern(regexp = "adult|child", message = "Type must be either 'adult' or 'child'")
    private String type;

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
