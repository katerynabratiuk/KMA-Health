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
    private Long id;

    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String city;

    @ManyToMany
    @JoinTable(
            name = "hospital_examination",
            joinColumns = @JoinColumn(name = "hospital_id"),
            inverseJoinColumns = @JoinColumn(name = "examination_id")
    )
    private Set<Examination> examinations;
}


