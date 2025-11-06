package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.enums.HospitalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalDto
{
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private String city;
    private HospitalType type;

    public static HospitalDto fromEntity(Hospital h) {
        HospitalDto d = new HospitalDto();
        d.setId(h.getId());
        d.setName(h.getName());
        d.setAddress(h.getAddress());
        d.setCity(h.getCity());
        d.setType(h.getType());
        return d;
    }
}

