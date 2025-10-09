package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.Hospital;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalDto
{
    private String name;
    private String address;
    private String phoneNumber;
    private String city;

    public static HospitalDto fromEntity(Hospital h) {
        HospitalDto d = new HospitalDto();
        d.setName(h.getName());
        d.setAddress(h.getAddress());
        d.setCity(h.getCity());
        return d;
    }
}

