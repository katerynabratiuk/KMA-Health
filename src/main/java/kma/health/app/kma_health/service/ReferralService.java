package kma.health.app.kma_health.service;

import kma.health.app.kma_health.entity.*;
import kma.health.app.kma_health.repository.ReferralRepository;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
public class ReferralService {

    private final ReferralRepository referralRepository;

    public void createReferralForDoctorType(Doctor doctor, Patient patient, DoctorType doctorType) {
        Referral referral = createReferralBoilerplate(doctor, patient);
        referral.setDoctorType(doctorType);
        referralRepository.save(referral);
    }

    public void createReferralForExamination(Doctor doctor, Patient patient, Examination examination) {
        Referral referral = createReferralBoilerplate(doctor, patient);
        referral.setExamination(examination);
        referralRepository.save(referral);
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
}
