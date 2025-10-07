package kma.health.app.kma_health.entity;

import jakarta.persistence.*;
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
@Table(name = "examination")
public class Examination {
    @Id
    private Long id;

    @Column(name = "exam_name")
    private String examName;

    private String unit;

    @OneToMany(mappedBy = "examination")
    private Set<HospitalExamination> hospitalExaminations;
}


