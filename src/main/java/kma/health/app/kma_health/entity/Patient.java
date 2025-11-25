package kma.health.app.kma_health.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import kma.health.app.kma_health.enums.UserRole;
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
@Table(name = "patient")
public class Patient implements AuthUser {
    @Id
    @Column(name = "patient_id")
    @GeneratedValue
    private UUID id;

    @Column(name = "passport_number", unique = true)
    @Size(min = 9, max = 9)
    private String passportNumber;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "full_name")
    private String fullName;

    private String profilePictureUrl;

    @PastOrPresent
    private LocalDate birthDate;

    @OneToMany(mappedBy = "patient")
    private Set<Declaration> declarations;

    @OneToMany(mappedBy = "patient")
    private Set<Referral> referrals;

    @OneToMany(mappedBy = "patient")
    private Set<Reminder> reminders;

    @OneToMany(mappedBy = "patient")
    private Set<MedicalFile> medicalFiles;

    @Override
    public UserRole getRole() {
        return UserRole.PATIENT;
    }
}
