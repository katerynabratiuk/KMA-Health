package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.DoctorType;
import kma.health.app.kma_health.entity.Examination;
import kma.health.app.kma_health.entity.Referral;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ReferralDtoTest {

    @Test
    public void testFromEntity_ShouldMapAllFields() {
        UUID referralId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        LocalDate validUntil = LocalDate.now().plusDays(30);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setFullName("Dr. Smith");

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");

        Referral referral = new Referral();
        referral.setId(referralId);
        referral.setValidUntil(validUntil);
        referral.setDoctor(doctor);
        referral.setDoctorType(doctorType);

        ReferralDto dto = ReferralDto.fromEntity(referral);

        assertEquals(referralId, dto.getId());
        assertEquals(validUntil, dto.getValidUntil());
        assertEquals(doctorId, dto.getDoctorId());
        assertEquals("Dr. Smith", dto.getDoctorFullName());
        assertEquals(doctorType.getTypeName(), dto.getDoctorType());
        assertNull(dto.getExamination());
    }

    @Test
    public void testFromEntity_ShouldMapExamination() {
        UUID referralId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setFullName("Dr. Smith");

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");

        Examination examination = new Examination();
        examination.setExamName("Blood Test");
        examination.setUnit("ml");

        Referral referral = new Referral();
        referral.setId(referralId);
        referral.setValidUntil(LocalDate.now().plusDays(30));
        referral.setDoctor(doctor);
        referral.setDoctorType(doctorType);
        referral.setExamination(examination);

        ReferralDto dto = ReferralDto.fromEntity(referral);

        assertNotNull(dto.getExamination());
        assertEquals("Blood Test", dto.getExamination().getName());
        assertEquals("ml", dto.getExamination().getUnit());
    }

    @Test
    public void testSettersAndGetters() {
        UUID id = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        LocalDate validUntil = LocalDate.now().plusDays(30);
        DoctorType doctorType = new DoctorType();
        ExaminationDto examination = new ExaminationDto("Test", "ml");

        ReferralDto dto = new ReferralDto();
        dto.setId(id);
        dto.setValidUntil(validUntil);
        dto.setDoctorId(doctorId);
        dto.setDoctorFullName("Dr. Test");
        dto.setDoctorType(doctorType.getTypeName());
        dto.setExamination(examination);

        assertEquals(id, dto.getId());
        assertEquals(validUntil, dto.getValidUntil());
        assertEquals(doctorId, dto.getDoctorId());
        assertEquals("Dr. Test", dto.getDoctorFullName());
        assertEquals(doctorType.getTypeName(), dto.getDoctorType());
        assertEquals(examination, dto.getExamination());
    }

    @Test
    public void testDefaultConstructor() {
        ReferralDto dto = new ReferralDto();
        assertNotNull(dto);
    }

    @Test
    public void testFromEntity_WithNullDoctor() {
        UUID referralId = UUID.randomUUID();

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");

        Referral referral = new Referral();
        referral.setId(referralId);
        referral.setValidUntil(LocalDate.now().plusDays(30));
        referral.setDoctor(null);  // Null doctor
        referral.setDoctorType(doctorType);

        ReferralDto dto = ReferralDto.fromEntity(referral);

        assertEquals(referralId, dto.getId());
        assertNull(dto.getDoctorId());
        assertNull(dto.getDoctorFullName());
        assertEquals("Cardiologist", dto.getDoctorType());
    }

    @Test
    public void testFromEntity_WithNullDoctorType() {
        UUID referralId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setFullName("Dr. Smith");

        Referral referral = new Referral();
        referral.setId(referralId);
        referral.setValidUntil(LocalDate.now().plusDays(30));
        referral.setDoctor(doctor);
        referral.setDoctorType(null);  // Null doctor type

        ReferralDto dto = ReferralDto.fromEntity(referral);

        assertEquals(referralId, dto.getId());
        assertEquals(doctorId, dto.getDoctorId());
        assertEquals("Dr. Smith", dto.getDoctorFullName());
        assertNull(dto.getDoctorType());
    }

    @Test
    public void testFromEntity_WithNullDoctorAndNullDoctorType() {
        UUID referralId = UUID.randomUUID();

        Referral referral = new Referral();
        referral.setId(referralId);
        referral.setValidUntil(LocalDate.now().plusDays(30));
        referral.setDoctor(null);
        referral.setDoctorType(null);

        ReferralDto dto = ReferralDto.fromEntity(referral);

        assertEquals(referralId, dto.getId());
        assertNull(dto.getDoctorId());
        assertNull(dto.getDoctorFullName());
        assertNull(dto.getDoctorType());
    }

    @Test
    public void testFromEntity_WithNullExamination() {
        UUID referralId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setFullName("Dr. Smith");

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Therapist");

        Referral referral = new Referral();
        referral.setId(referralId);
        referral.setValidUntil(LocalDate.now().plusDays(30));
        referral.setDoctor(doctor);
        referral.setDoctorType(doctorType);
        referral.setExamination(null);

        ReferralDto dto = ReferralDto.fromEntity(referral);

        assertNull(dto.getExamination());
    }
}

