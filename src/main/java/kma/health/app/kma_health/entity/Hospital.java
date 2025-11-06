package kma.health.app.kma_health.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kma.health.app.kma_health.enums.HospitalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "hospital")
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;
    @NotNull
    private String address;
    @NotNull
    private double latitude;
    @NotNull
    private double longitude;
    @NotNull
    private String city;
    @NotNull
    private HospitalType type;
    @Transient
    private Double rating;

    @ManyToMany
    @JoinTable(
            name = "hospital_examination",
            joinColumns = @JoinColumn(name = "hospital_id"),
            inverseJoinColumns = @JoinColumn(name = "examination_id")
    )
    private Set<Examination> examinations;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feedback> feedback = new ArrayList<>();
}


