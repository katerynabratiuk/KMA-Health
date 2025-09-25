package kma.health.app.kma_health.service;

import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.LabAssistant;
import kma.health.app.kma_health.entity.Patient;
import kma.health.app.kma_health.repository.DoctorRepository;
import kma.health.app.kma_health.repository.LabAssistantRepository;
import kma.health.app.kma_health.repository.PatientRepository;
import kma.health.app.kma_health.security.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@AllArgsConstructor
@Service
public class AuthService {

    private final DoctorRepository doctorRepository;
    private final LabAssistantRepository labAssistantRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public String login(String login, String password, String role) {
        switch (role.toLowerCase()) {
            case "PATIENT":
                Patient patient = patientRepository.findByEmail(login)
                        .or(() -> patientRepository.findByPhoneNumber(login))
                        .or(() -> patientRepository.findByPassportNumber(login))
                        .orElseThrow(() -> new RuntimeException("Patient not found"));

                if (!passwordEncoder.matches(password, patient.getPassword()))
                    throw new RuntimeException("Invalid credentials");

                return jwtUtils.generateToken(patient);

            case "DOCTOR":
                Doctor doctor = doctorRepository.findByEmail(login)
                        .or(() -> doctorRepository.findByPhoneNumber(login))
                        .or(() -> doctorRepository.findByPassportNumber(login))
                        .orElse(null);

                if (doctor == null) {
                    LabAssistant labAssistant = labAssistantRepository.findByEmail(login)
                            .or(() -> labAssistantRepository.findByPhoneNumber(login))
                            .or(() -> labAssistantRepository.findByPassportNumber(login))
                            .orElseThrow(() -> new RuntimeException("Doctor or LabAssistant not found"));

                    if (!passwordEncoder.matches(password, labAssistant.getPassword()))
                        throw new RuntimeException("Invalid credentials");

                    return jwtUtils.generateToken(labAssistant);
                }

                if (!passwordEncoder.matches(password, doctor.getPassword()))
                    throw new RuntimeException("Invalid credentials");

                return jwtUtils.generateToken(doctor);

            default:
                throw new RuntimeException("Unknown role");
        }
    }

    public void updateProfile(String token, Map<String, String> updates) {
        String subject = jwtUtils.getSubjectFromToken(token);
        String role = jwtUtils.getRoleFromToken(token);

        switch (role.toUpperCase()) {
            case "PATIENT":
                Patient patient = patientRepository.findByEmail(subject)
                        .or(() -> patientRepository.findByPhoneNumber(subject))
                        .or(() -> patientRepository.findByPassportNumber(subject))
                        .orElseThrow(() -> new RuntimeException("Patient not found"));

                applyUpdates(patient, updates);
                patientRepository.save(patient);
                break;

            case "DOCTOR":
                Doctor doctor = doctorRepository.findByEmail(subject)
                        .or(() -> doctorRepository.findByPhoneNumber(subject))
                        .or(() -> doctorRepository.findByPassportNumber(subject))
                        .orElse(null);

                if (doctor == null) {
                    LabAssistant labAssistant = labAssistantRepository.findByEmail(subject)
                            .or(() -> labAssistantRepository.findByPassportNumber(subject))
                            .or(() -> labAssistantRepository.findByPhoneNumber(subject))
                            .orElseThrow(() -> new RuntimeException("Doctor or LabAssistant not found"));
                    applyUpdates(labAssistant, updates);
                    labAssistantRepository.save(labAssistant);
                    break;
                }

                applyUpdates(doctor, updates);
                doctorRepository.save(doctor);
                break;

            default:
                throw new RuntimeException("Unknown role");
        }
    }

    private void applyUpdates(Object user, Map<String, String> updates) {
        if (user instanceof Patient patient) {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "email" -> patient.setEmail(value);
                    case "password" -> patient.setPassword(passwordEncoder.encode(value));
                    case "phoneNumber" -> patient.setPhoneNumber(value);
                    default -> throw new RuntimeException("Unknown field: " + key);
                }
            });
        } else if (user instanceof Doctor doctor) {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "email" -> doctor.setEmail(value);
                    case "password" -> doctor.setPassword(passwordEncoder.encode(value));
                    case "phoneNumber" -> doctor.setPhoneNumber(value);
                    default -> throw new RuntimeException("Unknown field: " + key);
                }
            });
        } else if (user instanceof LabAssistant assistant) {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "email" -> assistant.setEmail(value);
                    case "password" -> assistant.setPassword(passwordEncoder.encode(value));
                    case "phoneNumber" -> assistant.setPhoneNumber(value);
                    default -> throw new RuntimeException("Unknown field: " + key);
                }
            });
        }
    }

    public void deleteProfile(String token) {
        String subject = jwtUtils.getSubjectFromToken(token);
        String role = jwtUtils.getRoleFromToken(token);

        switch (role.toUpperCase()) {
            case "PATIENT":
                Patient patient = patientRepository.findByEmail(subject)
                        .or(() -> patientRepository.findByPhoneNumber(subject))
                        .or(() -> patientRepository.findByPassportNumber(subject))
                        .orElseThrow(() -> new RuntimeException("Patient not found"));
                patientRepository.delete(patient);
                break;

            case "DOCTOR":
                Doctor doctor = doctorRepository.findByEmail(subject)
                        .or(() -> doctorRepository.findByPhoneNumber(subject))
                        .or(() -> doctorRepository.findByPassportNumber(subject))
                        .orElseThrow(() -> new RuntimeException("Doctor not found"));
                doctorRepository.delete(doctor);
                break;

            case "LAB_ASSISTANT":
                LabAssistant assistant = labAssistantRepository.findByEmail(subject)
                        .or(() -> labAssistantRepository.findByPhoneNumber(subject))
                        .or(() -> labAssistantRepository.findByPassportNumber(subject))
                        .orElseThrow(() -> new RuntimeException("LabAssistant not found"));
                labAssistantRepository.delete(assistant);
                break;

            default:
                throw new RuntimeException("Unknown role");
        }
    }
}
