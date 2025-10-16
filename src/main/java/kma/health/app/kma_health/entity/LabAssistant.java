package kma.health.app.kma_health.entity;

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
@Table(name = "lab_assistant")
public class LabAssistant implements AuthUser {
    @Id
    @Column(name = "lab_assistant_id")
    @GeneratedValue
    private UUID id;

    @Column(name = "passport_number")
    @Size(min=9, max=9)
    private String passportNumber;

    @Column(name = "full_name")
    private String fullName;

    private String email;
    private String password;
    private String phoneNumber;

    @PastOrPresent
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @OneToMany(mappedBy = "labAssistant")
    private Set<Appointment> appointments;

    @Override
    public UserRole getRole() {
        return UserRole.LAB_ASSISTANT;
    }
}

