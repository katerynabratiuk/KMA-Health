package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.Hospital;

public record HospitalFormDto(Long id, String name){
    public static HospitalFormDto fromEntity(Hospital hospital){
        return new HospitalFormDto(hospital.getId(), hospital.getName());
    }
};
