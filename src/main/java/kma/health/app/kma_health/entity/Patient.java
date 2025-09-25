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
@Table(name = "patient")
public class Patient {
    @Id
    @Column(name = "passport_number")
    private String passportNumber;

    private String email;
    private String phoneNumber;

    @Column(name = "full_name")
    private String fullName;

    private LocalDate birthDate;

    @OneToMany(mappedBy = "patient")
    private Set<Declaration> declarations;

    @OneToMany(mappedBy = "patient")
    private Set<Referral> referrals;

    @OneToMany(mappedBy = "patient")
    private Set<Reminder> reminders;

    @OneToMany(mappedBy = "patient")
    private Set<MedicalFile> medicalFiles;
}
