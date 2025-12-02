package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.dto.doctorDetail.AppointmentDto;
import kma.health.app.kma_health.entity.*;
import kma.health.app.kma_health.enums.AppointmentStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
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

    @Test
    void testAppointmentFullViewDto_WithReferralDoctor() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setFullName("John Doe");

        DoctorType referralDoctorType = new DoctorType();
        referralDoctorType.setTypeName("General Practitioner");

        Doctor referralDoctor = new Doctor();
        referralDoctor.setId(UUID.randomUUID());
        referralDoctor.setFullName("Dr. Referrer");
        referralDoctor.setProfilePictureUrl("/images/referrer.jpg");
        referralDoctor.setDoctorType(referralDoctorType);

        Referral referral = new Referral();
        referral.setId(UUID.randomUUID());
        referral.setPatient(patient);
        referral.setDoctor(referralDoctor);

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setReferral(referral);

        AppointmentFullViewDto dto = new AppointmentFullViewDto(appointment);

        assertEquals("Dr. Referrer", dto.getReferralDoctorName());
        assertEquals("/images/referrer.jpg", dto.getReferralDoctorPhoto());
        assertEquals("General Practitioner", dto.getReferralDoctorType());
    }

    @Test
    void testAppointmentFullViewDto_WithReferralDoctorNullDoctorType() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Doctor referralDoctor = new Doctor();
        referralDoctor.setId(UUID.randomUUID());
        referralDoctor.setFullName("Dr. Referrer");
        referralDoctor.setDoctorType(null);

        Referral referral = new Referral();
        referral.setId(UUID.randomUUID());
        referral.setPatient(patient);
        referral.setDoctor(referralDoctor);

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setReferral(referral);

        AppointmentFullViewDto dto = new AppointmentFullViewDto(appointment);

        assertEquals("Dr. Referrer", dto.getReferralDoctorName());
        assertNull(dto.getReferralDoctorType());
    }

    @Test
    void testAppointmentFullViewDto_WithExamination() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Examination examination = new Examination();
        examination.setId(1L);
        examination.setExamName("Blood Test");

        Referral referral = new Referral();
        referral.setId(UUID.randomUUID());
        referral.setPatient(patient);
        referral.setExamination(examination);

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setReferral(referral);

        AppointmentFullViewDto dto = new AppointmentFullViewDto(appointment);

        assertEquals("Blood Test", dto.getExaminationName());
    }

    @Test
    void testAppointmentFullViewDto_WithNullExamination() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Referral referral = new Referral();
        referral.setId(UUID.randomUUID());
        referral.setPatient(patient);
        referral.setExamination(null);

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setReferral(referral);

        AppointmentFullViewDto dto = new AppointmentFullViewDto(appointment);

        assertNull(dto.getExaminationName());
    }

    @Test
    void testAppointmentFullViewDto_WithDoctorNullDoctorType() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Referral referral = new Referral();
        referral.setId(UUID.randomUUID());
        referral.setPatient(patient);

        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setFullName("Dr. Test");
        doctor.setDoctorType(null);

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setDoctor(doctor);
        appointment.setReferral(referral);

        AppointmentFullViewDto dto = new AppointmentFullViewDto(appointment);

        assertEquals("Dr. Test", dto.getDoctorName());
        assertNull(dto.getDoctorType());
    }

    @Test
    void testAppointmentFullViewDto_WithMedicalFiles() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Referral referral = new Referral();
        referral.setId(UUID.randomUUID());
        referral.setPatient(patient);

        MedicalFile file1 = new MedicalFile();
        file1.setId(UUID.randomUUID());
        file1.setName("test_file");
        file1.setExtension("pdf");
        file1.setFileType("document");

        Set<MedicalFile> files = new HashSet<>();
        files.add(file1);

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setReferral(referral);
        appointment.setMedicalFiles(files);

        AppointmentFullViewDto dto = new AppointmentFullViewDto(appointment);

        assertNotNull(dto.getMedicalFiles());
        assertEquals(1, dto.getMedicalFiles().size());
    }

    @Test
    void testAppointmentFullViewDto_WithNullMedicalFiles() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Referral referral = new Referral();
        referral.setId(UUID.randomUUID());
        referral.setPatient(patient);

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setReferral(referral);
        appointment.setMedicalFiles(null);

        AppointmentFullViewDto dto = new AppointmentFullViewDto(appointment);

        assertNotNull(dto.getMedicalFiles());
        assertTrue(dto.getMedicalFiles().isEmpty());
    }

    @Test
    void testAppointmentFullViewDto_WithNullPatient() {
        Referral referral = new Referral();
        referral.setId(UUID.randomUUID());
        referral.setPatient(null);

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setReferral(referral);

        AppointmentFullViewDto dto = new AppointmentFullViewDto(appointment);

        assertNull(dto.getPatientId());
        assertNull(dto.getPatientName());
        assertNull(dto.getPatientPhoto());
    }

    // Tests for AppointmentDto (doctorDetail)
    @Test
    void testAppointmentDto_WithDoctor() {
        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());

        Appointment appointment = new Appointment();
        appointment.setDate(LocalDate.now());
        appointment.setTime(LocalTime.of(10, 0));
        appointment.setDoctor(doctor);

        AppointmentDto dto = new AppointmentDto(appointment);

        assertEquals(doctor.getId(), dto.getDoctorId());
        assertNull(dto.getHospitalId());
    }

    @Test
    void testAppointmentDto_WithHospital() {
        Hospital hospital = new Hospital();
        hospital.setId(1L);

        Appointment appointment = new Appointment();
        appointment.setDate(LocalDate.now());
        appointment.setTime(LocalTime.of(10, 0));
        appointment.setHospital(hospital);

        AppointmentDto dto = new AppointmentDto(appointment);

        assertNull(dto.getDoctorId());
        assertEquals(1L, dto.getHospitalId());
    }

    @Test
    void testAppointmentDto_WithNullDoctorAndHospital() {
        Appointment appointment = new Appointment();
        appointment.setDate(LocalDate.now());
        appointment.setTime(LocalTime.of(10, 0));
        appointment.setDoctor(null);
        appointment.setHospital(null);

        AppointmentDto dto = new AppointmentDto(appointment);

        assertNull(dto.getDoctorId());
        assertNull(dto.getHospitalId());
    }

    @Test
    void testAppointmentDto_WithBothDoctorAndHospital() {
        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());

        Hospital hospital = new Hospital();
        hospital.setId(1L);

        Appointment appointment = new Appointment();
        appointment.setDate(LocalDate.now());
        appointment.setTime(LocalTime.of(10, 0));
        appointment.setDoctor(doctor);
        appointment.setHospital(hospital);

        AppointmentDto dto = new AppointmentDto(appointment);

        assertEquals(doctor.getId(), dto.getDoctorId());
        assertEquals(1L, dto.getHospitalId());
    }
}

