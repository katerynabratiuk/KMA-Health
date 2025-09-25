package kma.health.app.kma_health.entity;

import jakarta.persistence.*;
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
@Table(name = "reminder")
public class Reminder {
    @Id
    private UUID id;

    @Column(name = "reminder_date")
    private LocalDate reminderDate;

    private String text;

    @ManyToOne
    @JoinColumn(name = "patient_passport")
    private Patient patient;
}

