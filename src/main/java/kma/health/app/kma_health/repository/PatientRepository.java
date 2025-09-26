package kma.health.app.kma_health.repository;

import kma.health.app.kma_health.entity.Patient;

import java.util.Optional;

public interface PatientRepository extends AuthUserRepository<Patient> {
    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByPhoneNumber(String phoneNumber);
    Optional<Patient> findByPassportNumber(String passportNumber);
}
