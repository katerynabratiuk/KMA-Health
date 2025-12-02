package kma.health.app.kma_health.service;

import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.Patient;
import kma.health.app.kma_health.exception.DoctorSpecializationAgeRestrictionException;
import kma.health.app.kma_health.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import(AppointmentServiceImportTest.TestConfig.class)
public class AppointmentServiceImportTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Test
    public void testValidateDoctorAndPatientAge_ShouldThrowExceptionWhenChildPatientWithAdultDoctor() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(patientId);
        patient.setBirthDate(LocalDate.now().minusYears(10));

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setType("adult");

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        assertThrows(DoctorSpecializationAgeRestrictionException.class,
                () -> appointmentService.validateDoctorAndPatientAge(doctorId, patientId));

        verify(patientRepository).findById(patientId);
        verify(doctorRepository).findById(doctorId);
    }

    @Configuration
    static class TestConfig {

        @Bean
        public AppointmentRepository appointmentRepository() {
            return Mockito.mock(AppointmentRepository.class);
        }

        @Bean
        public PatientRepository patientRepository() {
            return Mockito.mock(PatientRepository.class);
        }

        @Bean
        public DoctorRepository doctorRepository() {
            return Mockito.mock(DoctorRepository.class);
        }

        @Bean
        public HospitalRepository hospitalRepository() {
            return Mockito.mock(HospitalRepository.class);
        }

        @Bean
        public ReferralRepository referralRepository() {
            return Mockito.mock(ReferralRepository.class);
        }

        @Bean
        public MedicalFileRepository medicalFileRepository() {
            return Mockito.mock(MedicalFileRepository.class);
        }

        @Bean
        public LabAssistantRepository labAssistantRepository() {
            return Mockito.mock(LabAssistantRepository.class);
        }

        @Bean
        public DoctorTypeRepository doctorTypeRepository() {
            return Mockito.mock(DoctorTypeRepository.class);
        }

        @Bean
        public HospitalService hospitalService() {
            return Mockito.mock(HospitalService.class);
        }

        @Bean
        public ReferralService referralService() {
            return Mockito.mock(ReferralService.class);
        }

        @Bean
        public AppointmentService appointmentService(
                AppointmentRepository appointmentRepository,
                PatientRepository patientRepository,
                DoctorRepository doctorRepository,
                HospitalRepository hospitalRepository,
                ReferralRepository referralRepository,
                MedicalFileRepository medicalFileRepository,
                LabAssistantRepository labAssistantRepository,
                DoctorTypeRepository doctorTypeRepository,
                HospitalService hospitalService,
                ReferralService referralService) {
            return new AppointmentService(
                    appointmentRepository,
                    patientRepository,
                    doctorRepository,
                    hospitalRepository,
                    referralRepository,
                    medicalFileRepository,
                    labAssistantRepository,
                    doctorTypeRepository,
                    hospitalService,
                    referralService);
        }
    }
}
