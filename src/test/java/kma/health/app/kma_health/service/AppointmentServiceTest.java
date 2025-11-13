package kma.health.app.kma_health.service;

import kma.health.app.kma_health.dto.AppointmentCreateUpdateDto;
import kma.health.app.kma_health.exception.AppointmentTargetConflictException;
import kma.health.app.kma_health.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private ReferralRepository referralRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    public void testCreateAppointment_ShouldThrowExceptionWhenBothDoctorAndHospitalProvided() {
        AppointmentCreateUpdateDto appointmentDto = new AppointmentCreateUpdateDto();
        appointmentDto.setDoctorId(UUID.randomUUID());
        appointmentDto.setHospitalId(1L);
        appointmentDto.setPatientId(UUID.randomUUID());

        assertThrows(AppointmentTargetConflictException.class, () -> {
            appointmentService.createAppointment(appointmentDto);
        });
    }
}
