package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.*;
import kma.health.app.kma_health.enums.AppointmentStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentDtoTest {

    @Test
    void testAppointmentFullViewDto_FromAppointment() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setFullName("John Doe");

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");

        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setFullName("Dr. Smith");
        doctor.setDoctorType(doctorType);

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("City Hospital");

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
        appointment.setHospital(hospital);
        appointment.setReferral(referral);

        AppointmentFullViewDto dto = new AppointmentFullViewDto(appointment);

        assertEquals(appointment.getId(), dto.getId());
        assertEquals("Dr. Smith", dto.getDoctorName());
        assertEquals("Cardiologist", dto.getDoctorType());
        assertEquals("City Hospital", dto.getHospitalName());
        assertEquals(AppointmentStatus.SCHEDULED, dto.getStatus());
        assertEquals(patient.getId(), dto.getPatientId());
        assertEquals("John Doe", dto.getPatientName());
    }

    @Test
    void testAppointmentFullViewDto_WithNullDoctor() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setFullName("John Doe");

        Referral referral = new Referral();
        referral.setId(UUID.randomUUID());
        referral.setPatient(patient);

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setDoctor(null);
        appointment.setReferral(referral);

        AppointmentFullViewDto dto = new AppointmentFullViewDto(appointment);

        assertNull(dto.getDoctorName());
        assertNull(dto.getDoctorType());
    }

    @Test
    void testAppointmentFullViewDto_WithNullHospital() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setFullName("Test");

        Referral referral = new Referral();
        referral.setId(UUID.randomUUID());
        referral.setPatient(patient);

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setHospital(null);
        appointment.setReferral(referral);

        AppointmentFullViewDto dto = new AppointmentFullViewDto(appointment);

        assertNull(dto.getHospitalName());
        assertNull(dto.getHospitalId());
    }

    @Test
    void testAppointmentFullViewDto_WithNullReferral() {
        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setReferral(null);

        AppointmentFullViewDto dto = new AppointmentFullViewDto(appointment);

        assertNull(dto.getPatientId());
        assertNull(dto.getPatientName());
        assertNull(dto.getReferralId());
    }

    @Test
    void testAppointmentFullViewDto_SettersAndGetters() {
        AppointmentFullViewDto dto = new AppointmentFullViewDto();

        UUID id = UUID.randomUUID();
        dto.setId(id);
        dto.setDoctorName("Dr. Test");
        dto.setDoctorType("Therapist");
        dto.setHospitalName("Test Hospital");
        dto.setStatus(AppointmentStatus.OPEN);
        dto.setDate(LocalDate.now());
        dto.setTime(LocalTime.of(14, 30));

        assertEquals(id, dto.getId());
        assertEquals("Dr. Test", dto.getDoctorName());
        assertEquals("Therapist", dto.getDoctorType());
        assertEquals("Test Hospital", dto.getHospitalName());
        assertEquals(AppointmentStatus.OPEN, dto.getStatus());
    }
}

