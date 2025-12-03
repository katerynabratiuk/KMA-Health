package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.*;
import kma.health.app.kma_health.enums.AppointmentStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentShortViewDtoTest {

    @Test
    void testAppointmentShortViewDto_FromAppointmentWithDoctor() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setFullName("John Doe");

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");

        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setFullName("Dr. Smith");
        doctor.setDoctorType(doctorType);

        Referral referral = new Referral();
        referral.setId(UUID.randomUUID());
        referral.setPatient(patient);
        referral.setDoctorType(doctorType);

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setDate(LocalDate.now());
        appointment.setTime(LocalTime.of(10, 0));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setDoctor(doctor);
        appointment.setReferral(referral);

        AppointmentShortViewDto dto = new AppointmentShortViewDto(appointment);

        assertEquals(appointment.getId(), dto.getId());
        assertEquals("Dr. Smith", dto.getDoctorName());
        assertEquals(doctor.getId(), dto.getDoctorId());
    }

    @Test
    void testAppointmentShortViewDto_FromAppointmentWithHospital() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setFullName("John Doe");

        Examination examination = new Examination();
        examination.setExamName("Blood Test");

        Referral referral = new Referral();
        referral.setId(UUID.randomUUID());
        referral.setPatient(patient);
        referral.setExamination(examination);

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("City Hospital");

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setDate(LocalDate.now());
        appointment.setTime(LocalTime.of(10, 0));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setHospital(hospital);
        appointment.setReferral(referral);

        AppointmentShortViewDto dto = new AppointmentShortViewDto(appointment);

        assertEquals(appointment.getId(), dto.getId());
        assertEquals(1L, dto.getHospitalId());
        assertEquals("Blood Test", dto.getExaminationName());
    }

    @Test
    void testAppointmentShortViewDto_WithNullDoctor() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Referral referral = new Referral();
        referral.setId(UUID.randomUUID());
        referral.setPatient(patient);

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setDoctor(null);
        appointment.setReferral(referral);

        AppointmentShortViewDto dto = new AppointmentShortViewDto(appointment);

        assertNull(dto.getDoctorName());
        assertNull(dto.getDoctorId());
    }

    @Test
    void testAppointmentShortViewDto_SettersAndGetters() {
        AppointmentShortViewDto dto = new AppointmentShortViewDto();

        UUID id = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        dto.setId(id);
        dto.setDoctorName("Dr. Test");
        dto.setDoctorId(doctorId);
        dto.setHospitalId(1L);
        dto.setExaminationName("X-Ray");
        dto.setDate(LocalDate.now());
        dto.setTime(LocalTime.of(14, 30));

        assertEquals(id, dto.getId());
        assertEquals("Dr. Test", dto.getDoctorName());
        assertEquals(doctorId, dto.getDoctorId());
        assertEquals(1L, dto.getHospitalId());
        assertEquals("X-Ray", dto.getExaminationName());
    }

    @Test
    void testAppointmentShortViewDto_AllArgsConstructor() {
        UUID id = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(10, 0);

        AppointmentShortViewDto dto = new AppointmentShortViewDto(
                id, date, time, "Dr. Test", doctorId, 1L, "Blood Test", "Patient Name",
                kma.health.app.kma_health.enums.AppointmentStatus.SCHEDULED, "Test diagnosis");

        assertEquals(id, dto.getId());
        assertEquals("Dr. Test", dto.getDoctorName());
        assertEquals(doctorId, dto.getDoctorId());
        assertEquals(1L, dto.getHospitalId());
        assertEquals("Blood Test", dto.getExaminationName());
        assertEquals("Patient Name", dto.getPatientName());
        assertEquals(kma.health.app.kma_health.enums.AppointmentStatus.SCHEDULED, dto.getStatus());
        assertEquals("Test diagnosis", dto.getDiagnosis());
    }
}
