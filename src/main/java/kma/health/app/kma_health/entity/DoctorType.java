package kma.health.app.kma_health.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "doctor_type")
public class DoctorType {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String typeName;

    @OneToMany(mappedBy = "doctorType")
    private Set<Doctor> doctors;

    // Конструкторы, геттеры/сеттеры
}
