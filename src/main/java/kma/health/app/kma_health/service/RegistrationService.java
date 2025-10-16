package kma.health.app.kma_health.service;

import kma.health.app.kma_health.dto.RegisterRequest;
import kma.health.app.kma_health.entity.*;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.*;
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

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);
    private static final Marker SECURITY = MarkerFactory.getMarker("SECURITY");

    public String register(RegisterRequest request) {
        MDC.put("userRole", request.getRole().toString());
        MDC.put("email", request.getEmail());

        try {
            switch (request.getRole()) {
                case PATIENT -> {
                    Patient patient = new Patient();
                    fillCommonFields(patient, request);
                    patientRepository.save(patient);
                    MDC.put("userId", String.valueOf(patient.getId()));
                    MDC.put("status", "SUCCESS");
                    log.info("Patient registered successfully");
                    return "Patient registered successfully";
                }
                case DOCTOR, LAB_ASSISTANT -> {
                    validateRegisterKey(request.getRegisterKey());

                    if (request.getRole() == UserRole.DOCTOR) {
                        Doctor doctor = new Doctor();
                        fillCommonFields(doctor, request);
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
                        MDC.put("userId", String.valueOf(doctor.getId()));
                        MDC.put("status", "SUCCESS");
                        log.info("Doctor registered successfully");
                        return "Doctor registered successfully";
                    } else {
                        LabAssistant labAssistant = new LabAssistant();
                        fillCommonFields(labAssistant, request);
                        labAssistant.setHospital(
                                hospitalRepository.findById(request.getLabHospitalId())
                                        .orElseThrow(() -> new RuntimeException("Hospital not found"))
                        );
                        labAssistantRepository.save(labAssistant);
                        MDC.put("userId", String.valueOf(labAssistant.getId()));
                        MDC.put("status", "SUCCESS");
                        log.info("Lab Assistant registered successfully");
                        return "Lab Assistant registered successfully";
                    }
                }
                default -> throw new IllegalArgumentException("Unsupported role");
            }
        } catch (RuntimeException e) {
            MDC.put("status", "FAILED");
            MDC.put("reason", e.getMessage());
            if (e.getMessage().contains("Invalid register key")) {
                log.warn(SECURITY, "Registration failed for email {} (role {}). Reason: invalid key.", request.getEmail(), request.getRole());
            } else {
                log.warn("Registration failed for email {} (role {}). Reason: {}", request.getEmail(), request.getRole(), e.getMessage());
            }
            throw e;
        } finally {
            MDC.clear();
        }
    }

    private void fillCommonFields(AuthUser user, RegisterRequest request) {
        user.setPassportNumber(request.getPassportNumber());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setFullName(request.getFullName());
        user.setBirthDate(request.getBirthDate());
    }

    private void validateRegisterKey(String providedKey) {
        if (providedKey == null || !providedKey.equals(registerKey)) {
            throw new RuntimeException("Invalid register key");
        }
    }
}

