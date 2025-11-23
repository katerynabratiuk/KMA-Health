package kma.health.app.kma_health.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "medical_file")
public class MedicalFile {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "file_type")
    private String fileType;

    private String name;
    private String extension;
    private String link;

    @ManyToOne
    @JoinColumn(name = "patient_passport")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
}

