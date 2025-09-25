package kma.health.app.kma_health.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "hospital")
public class Hospital {
    @Id
    private UUID id;

    private String name;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal rating;

    @OneToMany(mappedBy = "hospital")
    private Set<HospitalExamination> hospitalExaminations;
}

