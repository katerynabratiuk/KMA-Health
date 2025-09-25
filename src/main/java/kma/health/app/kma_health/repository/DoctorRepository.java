package kma.health.app.kma_health.repository;

import kma.health.app.kma_health.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, String> {
    Optional<Doctor> findByEmail(String email);
    Optional<Doctor> findByPhoneNumber(String phoneNumber);
    Optional<Doctor> findByPassportNumber(String passportNumber);
}
