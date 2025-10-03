package kma.health.app.kma_health.dto;

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
}

