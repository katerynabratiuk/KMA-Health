package kma.health.app.kma_health.entity;

import kma.health.app.kma_health.enums.AppointmentStatus;
import kma.health.app.kma_health.enums.FeedbackTargetType;
import kma.health.app.kma_health.enums.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EntityTest {

    @Test
    void testPatient_GettersAndSetters() {
        Patient patient = new Patient();
        UUID id = UUID.randomUUID();

        patient.setId(id);
        patient.setFullName("John Doe");
        patient.setEmail("john@example.com");
        patient.setPassword("password");
        patient.setPhoneNumber("+380991234567");
        patient.setPassportNumber("AB123456");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setProfilePictureUrl("/images/profile.png");

        assertEquals(id, patient.getId());
        assertEquals("John Doe", patient.getFullName());
        assertEquals("john@example.com", patient.getEmail());
        assertEquals("password", patient.getPassword());
        assertEquals("+380991234567", patient.getPhoneNumber());
        assertEquals("AB123456", patient.getPassportNumber());
        assertEquals(LocalDate.of(1990, 1, 1), patient.getBirthDate());
        assertEquals("/images/profile.png", patient.getProfilePictureUrl());
        assertEquals(UserRole.PATIENT, patient.getRole());
    }

    @Test
    void testDoctor_GettersAndSetters() {
        Doctor doctor = new Doctor();
        UUID id = UUID.randomUUID();

        doctor.setId(id);
        doctor.setFullName("Dr. Smith");
        doctor.setEmail("dr.smith@example.com");
        doctor.setPassword("password");
        doctor.setPhoneNumber("+380997654321");
        doctor.setPassportNumber("CD789012");
        doctor.setBirthDate(LocalDate.of(1980, 5, 15));
        doctor.setProfilePictureUrl("/images/doctor.png");
        doctor.setType("adult");
        doctor.setDescription("Experienced doctor");
        doctor.setStartedWorking(LocalDate.of(2010, 1, 1));

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");
        doctor.setDoctorType(doctorType);

        Hospital hospital = new Hospital();
        hospital.setName("Test Hospital");
        doctor.setHospital(hospital);

        assertEquals(id, doctor.getId());
        assertEquals("Dr. Smith", doctor.getFullName());
        assertEquals("adult", doctor.getType());
        assertEquals("Experienced doctor", doctor.getDescription());
        assertEquals(LocalDate.of(2010, 1, 1), doctor.getStartedWorking());
        assertEquals("Cardiologist", doctor.getDoctorType().getTypeName());
        assertEquals(UserRole.DOCTOR, doctor.getRole());
    }

    @Test
    void testLabAssistant_GettersAndSetters() {
        LabAssistant labAssistant = new LabAssistant();
        UUID id = UUID.randomUUID();

        labAssistant.setId(id);
        labAssistant.setFullName("Lab Tech");
        labAssistant.setEmail("lab@example.com");
        labAssistant.setPassword("password");
        labAssistant.setPhoneNumber("+380993456789");
        labAssistant.setPassportNumber("EF345678");
        labAssistant.setBirthDate(LocalDate.of(1985, 3, 20));

        Hospital hospital = new Hospital();
        hospital.setName("Test Hospital");
        labAssistant.setHospital(hospital);

        assertEquals(id, labAssistant.getId());
        assertEquals("Lab Tech", labAssistant.getFullName());
        assertEquals(UserRole.LAB_ASSISTANT, labAssistant.getRole());
    }

    @Test
    void testAppointment_GettersAndSetters() {
        Appointment appointment = new Appointment();
        UUID id = UUID.randomUUID();

        appointment.setId(id);
        appointment.setDate(LocalDate.now());
        appointment.setTime(LocalTime.of(10, 0));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setDiagnosis("Test diagnosis");

        Doctor doctor = new Doctor();
        doctor.setFullName("Dr. Test");
        appointment.setDoctor(doctor);

        Hospital hospital = new Hospital();
        hospital.setName("Test Hospital");
        appointment.setHospital(hospital);

        assertEquals(id, appointment.getId());
        assertEquals(LocalDate.now(), appointment.getDate());
        assertEquals(LocalTime.of(10, 0), appointment.getTime());
        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
        assertEquals("Test diagnosis", appointment.getDiagnosis());
        assertEquals("Dr. Test", appointment.getDoctor().getFullName());
    }

    @Test
    void testDoctorType_GettersAndSetters() {
        DoctorType doctorType = new DoctorType();
        doctorType.setId(1L);
        doctorType.setTypeName("Cardiologist");

        assertEquals(1L, doctorType.getId());
        assertEquals("Cardiologist", doctorType.getTypeName());
    }

    @Test
    void testHospital_GettersAndSetters() {
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("City Hospital");
        hospital.setAddress("123 Main St");
        hospital.setCity("Kyiv");
        hospital.setLatitude(50.45);
        hospital.setLongitude(30.52);
        hospital.setRating(4.5);

        assertEquals(1L, hospital.getId());
        assertEquals("City Hospital", hospital.getName());
        assertEquals("123 Main St", hospital.getAddress());
        assertEquals("Kyiv", hospital.getCity());
        assertEquals(50.45, hospital.getLatitude());
        assertEquals(30.52, hospital.getLongitude());
        assertEquals(4.5, hospital.getRating());
    }

    @Test
    void testReferral_GettersAndSetters() {
        Referral referral = new Referral();
        UUID id = UUID.randomUUID();

        referral.setId(id);
        referral.setValidUntil(LocalDate.now().plusDays(30));

        Patient patient = new Patient();
        patient.setFullName("Test Patient");
        referral.setPatient(patient);

        Doctor doctor = new Doctor();
        doctor.setFullName("Dr. Test");
        referral.setDoctor(doctor);

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Therapist");
        referral.setDoctorType(doctorType);

        assertEquals(id, referral.getId());
        assertEquals(LocalDate.now().plusDays(30), referral.getValidUntil());
        assertEquals("Test Patient", referral.getPatient().getFullName());
        assertEquals("Dr. Test", referral.getDoctor().getFullName());
    }

    @Test
    void testFeedback_GettersAndSetters() {
        Feedback feedback = new Feedback();
        feedback.setId(1L);
        feedback.setScore((short) 5);
        feedback.setComment("Great service!");
        feedback.setDate(LocalDate.now());
        feedback.setTargetType(FeedbackTargetType.DOCTOR);

        Patient patient = new Patient();
        patient.setFullName("Test Patient");
        feedback.setPatient(patient);

        Doctor doctor = new Doctor();
        doctor.setFullName("Dr. Test");
        feedback.setDoctor(doctor);

        assertEquals(1L, feedback.getId());
        assertEquals((short) 5, feedback.getScore());
        assertEquals("Great service!", feedback.getComment());
        assertEquals(FeedbackTargetType.DOCTOR, feedback.getTargetType());
    }

    @Test
    void testExamination_GettersAndSetters() {
        Examination examination = new Examination();
        examination.setId(1L);
        examination.setExamName("Blood Test");
        examination.setUnit("ml");

        assertEquals(1L, examination.getId());
        assertEquals("Blood Test", examination.getExamName());
        assertEquals("ml", examination.getUnit());
    }

    @Test
    void testMedicalFile_GettersAndSetters() {
        MedicalFile medicalFile = new MedicalFile();
        UUID id = UUID.randomUUID();
        medicalFile.setId(id);
        medicalFile.setName("Test File");
        medicalFile.setLink("/files/test.pdf");
        medicalFile.setExtension("pdf");
        medicalFile.setFileType("application/pdf");

        Patient patient = new Patient();
        patient.setFullName("Test Patient");
        medicalFile.setPatient(patient);

        assertEquals(id, medicalFile.getId());
        assertEquals("Test File", medicalFile.getName());
        assertEquals("/files/test.pdf", medicalFile.getLink());
        assertEquals("pdf", medicalFile.getExtension());
        assertEquals("application/pdf", medicalFile.getFileType());
    }

    @Test
    void testDeclaration_GettersAndSetters() {
        Declaration declaration = new Declaration();
        UUID id = UUID.randomUUID();
        declaration.setId(id);
        declaration.setDateSigned(LocalDate.now());

        Patient patient = new Patient();
        patient.setFullName("Test Patient");
        declaration.setPatient(patient);

        Doctor doctor = new Doctor();
        doctor.setFullName("Dr. Family");
        declaration.setDoctor(doctor);

        assertEquals(id, declaration.getId());
        assertEquals(LocalDate.now(), declaration.getDateSigned());
        assertNotNull(declaration.getPatient());
        assertNotNull(declaration.getDoctor());
    }

    @Test
    void testReminder_GettersAndSetters() {
        Reminder reminder = new Reminder();
        UUID id = UUID.randomUUID();
        reminder.setId(id);
        reminder.setText("Don't forget your appointment!");
        reminder.setReminderDate(LocalDate.now());

        Patient patient = new Patient();
        patient.setFullName("Test Patient");
        reminder.setPatient(patient);

        assertEquals(id, reminder.getId());
        assertEquals("Don't forget your appointment!", reminder.getText());
        assertEquals(LocalDate.now(), reminder.getReminderDate());
    }
}

