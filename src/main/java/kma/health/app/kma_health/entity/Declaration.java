package kma.health.app.kma_health.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "declaration")
public class Declaration {
    @Id
    private UUID id;

    @PastOrPresent
    @Column(name = "date_signed")
    private LocalDate dateSigned;

    @ManyToOne
    @JoinColumn(name = "doctor_passport")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_passport")
    private Patient patient;
}
