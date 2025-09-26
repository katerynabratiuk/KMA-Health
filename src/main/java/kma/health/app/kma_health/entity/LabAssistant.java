package kma.health.app.kma_health.entity;

import jakarta.persistence.*;
import kma.health.app.kma_health.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "lab_assistant")
public class LabAssistant implements AuthUser {
    @Id
    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name = "full_name")
    private String fullName;

    private String email;
    private String password;
    private String phoneNumber;

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

