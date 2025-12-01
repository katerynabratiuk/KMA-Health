package kma.health.app.kma_health.service;

import jakarta.persistence.EntityNotFoundException;
import kma.health.app.kma_health.dto.ReferralDto;
import kma.health.app.kma_health.entity.*;
import kma.health.app.kma_health.enums.AppointmentStatus;
import kma.health.app.kma_health.exception.InvalidFamilyDoctorReferralMethodException;
import kma.health.app.kma_health.exception.MissingOpenAppointmentException;
import kma.health.app.kma_health.repository.AppointmentRepository;
import kma.health.app.kma_health.repository.DoctorTypeRepository;
import kma.health.app.kma_health.repository.ReferralRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ReferralService {

    private final ReferralRepository referralRepository;
    private final DoctorTypeRepository doctorTypeRepository;
    private final ExaminationService examinationService;
    private final AppointmentRepository appointmentRepository;

    public void createReferral(Doctor doctor, Patient patient, String doctorTypeName) {
        if (!haveOpenAppointment(doctor.getId(), patient.getId()))
            throw new MissingOpenAppointmentException("Cannot create a referral with no open appointments");

        Referral referral = createReferralBoilerplate(doctor, patient);
        if (doctorTypeName.equals("Family doctor"))
            throw new InvalidFamilyDoctorReferralMethodException("Wrong method used for referral creation");

        referral.setDoctorType(doctorTypeRepository.findByTypeName(doctorTypeName)
                .orElseThrow(() -> new RuntimeException("Doctor type" + doctorTypeName + "not found")));

        referralRepository.save(referral);
    }

    public void createReferral(Doctor doctor, Patient patient, Long examinationId) {
        if (!haveOpenAppointment(doctor.getId(), patient.getId()))
            throw new MissingOpenAppointmentException("Cannot create a referral with no open appointments");

        Referral referral = createReferralBoilerplate(doctor, patient);
        try {
            referral.setExamination(examinationService.findExaminationById(examinationId));
        } catch (Exception e) {
            throw new EntityNotFoundException("Cannot create a referral with examination " + examinationId);
        }

        referralRepository.save(referral);
    }

    public Referral createReferralForFamilyDoctor(Patient patient, LocalDate appointmentDate) {
        Referral referral = new Referral();
        referral.setDoctorType(doctorTypeRepository.findByTypeName("Family doctor")
                .orElseThrow(() -> new RuntimeException("Doctor type Family doctor not found")));
        referral.setPatient(patient);
        referral.setValidUntil(appointmentDate.plusDays(1));
        return referralRepository.save(referral);
    }

    private Referral createReferralBoilerplate(Doctor doctor, Patient patient) {
        Referral referral = new Referral();
        referral.setDoctor(doctor);
        referral.setPatient(patient);
        referral.setValidUntil(LocalDate.now().plusYears(1));
        return referral;
    }

    public void deleteReferral(Referral referral) {
        if (referralRepository.existsById(referral.getId()))
            referralRepository.delete(referral);
    }

    public List<Referral> getAllReferrals(UUID patientId) {
        return referralRepository.findByPatientId(patientId);
    }

    public List<ReferralDto> getActiveReferrals(UUID patientId) {
        List<Referral> activeReferrals = referralRepository.findByPatientIdAndValidUntilGreaterThanEqual(
                patientId,
                LocalDate.now()
        );
        return activeReferrals.stream()
                .map(ReferralDto::fromEntity)
                .collect(Collectors.toList());
    }

    private boolean haveOpenAppointment(UUID doctorId, UUID patientId) {
        List<Appointment> appointments = appointmentRepository
                .findByDoctor_IdAndReferral_Patient_Id(doctorId, patientId);
        return appointments.stream()
                .anyMatch(app -> app.getStatus() != null &&
                        app.getStatus().equals(AppointmentStatus.OPEN));
    }
}

