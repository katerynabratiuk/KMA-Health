package kma.health.app.kma_health.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import kma.health.app.kma_health.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "doctor")
public class Doctor implements AuthUser {
    @Id
    @Column(name = "doctor_id")
    @GeneratedValue
    private UUID id;

    @Column(name = "passport_number")
    @Size(min=9, max=9)
    private String passportNumber;

    @Email(message = "Invalid email")
    private String email;
    private String password;
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

    @Transient
    private Double rating;

    @Nullable
    private String description;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feedback> feedback = new ArrayList<>();

    @Override
    public UserRole getRole() {
        return UserRole.DOCTOR;
    }
}
