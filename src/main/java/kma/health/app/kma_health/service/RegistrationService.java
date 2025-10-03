package kma.health.app.kma_health.service;

import kma.health.app.kma_health.dto.RegisterRequest;
import kma.health.app.kma_health.entity.*;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final LabAssistantRepository labAssistantRepository;
    private final DoctorTypeRepository doctorTypeRepository;
    private final HospitalRepository hospitalRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${REGISTER_KEY}")
    private String registerKey;

    public String register(RegisterRequest request) {
        switch (request.getRole()) {
            case PATIENT -> {
                Patient patient = new Patient();
                patient.setPassportNumber(request.getPassportNumber());
                patient.setEmail(request.getEmail());
                patient.setPassword(passwordEncoder.encode(request.getPassword()));
                patient.setPhoneNumber(request.getPhoneNumber());
                patient.setFullName(request.getFullName());
                patient.setBirthDate(request.getBirthDate());
                patientRepository.save(patient);
                return "Patient registered successfully";
            }
            case DOCTOR, LAB_ASSISTANT -> {
                validateRegisterKey(request.getRegisterKey());

                if (request.getRole() == UserRole.DOCTOR) {
                    Doctor doctor = new Doctor();
                    doctor.setPassportNumber(request.getPassportNumber());
                    doctor.setEmail(request.getEmail());
                    doctor.setPassword(passwordEncoder.encode(request.getPassword()));
                    doctor.setPhoneNumber(request.getPhoneNumber());
                    doctor.setFullName(request.getFullName());
                    doctor.setBirthDate(request.getBirthDate());
                    doctor.setType(request.getType());
                    doctor.setDoctorType(
                            doctorTypeRepository.findById(request.getDoctorTypeId())
                                    .orElseThrow(() -> new RuntimeException("Doctor type not found"))
                    );
                    doctor.setHospital(
                            hospitalRepository.findById(request.getHospitalId())
                                    .orElseThrow(() -> new RuntimeException("Hospital not found"))
                    );
                    doctorRepository.save(doctor);
                    return "Doctor registered successfully";
                } else {
                    LabAssistant labAssistant = new LabAssistant();
                    labAssistant.setPassportNumber(request.getPassportNumber());
                    labAssistant.setEmail(request.getEmail());
                    labAssistant.setPassword(passwordEncoder.encode(request.getPassword()));
                    labAssistant.setPhoneNumber(request.getPhoneNumber());
                    labAssistant.setFullName(request.getFullName());
                    labAssistant.setHospital(
                            hospitalRepository.findById(request.getLabHospitalId())
                                    .orElseThrow(() -> new RuntimeException("Hospital not found"))
                    );
                    labAssistantRepository.save(labAssistant);
                    return "Lab Assistant registered successfully";
                }
            }
            default -> throw new IllegalArgumentException("Unsupported role");
        }
    }

    private void validateRegisterKey(String providedKey) {
        if (providedKey == null || !providedKey.equals(registerKey)) {
            throw new RuntimeException("Invalid register key");
        }
    }
}
