package kma.health.app.kma_health.service;

import java.time.Period;
import jakarta.persistence.EntityNotFoundException;
import kma.health.app.kma_health.dto.AppointmentCreateUpdateDto;
import kma.health.app.kma_health.dto.AppointmentFullViewDto;
import kma.health.app.kma_health.dto.AppointmentShortViewDto;
import kma.health.app.kma_health.entity.Appointment;
import kma.health.app.kma_health.exception.AppointmentNotFoundException;
import kma.health.app.kma_health.exception.AppointmentTargetConflictException;
import kma.health.app.kma_health.exception.DoctorSpecializationAgeRestrictionException;
import kma.health.app.kma_health.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static kma.health.app.kma_health.service.HospitalService.EXAMINATION_TIME;

@Service
@AllArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final ReferralRepository referralRepository;

    public List<AppointmentShortViewDto> getAppointments(UUID patientId) {

        List<Appointment> queryRes = appointmentRepository.findByReferral_Patient_Id(patientId);
        List<AppointmentShortViewDto> res = new ArrayList<>();
        for (Appointment app : queryRes) {
            res.add(new AppointmentShortViewDto(app));
        }
        return res;
    }

    public AppointmentFullViewDto getFullAppointment(UUID id) {
        return appointmentRepository.findById(id)
                .map(AppointmentFullViewDto::new)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment is not found."));
    }

    public void deleteAppointment(UUID id) {
        if (appointmentRepository.existsById(id))
            appointmentRepository.deleteById(id);
        else throw new AppointmentNotFoundException("Appointment is not found.");
    }

    public void createAppointment(AppointmentCreateUpdateDto appointmentDto) {
        validateAppointmentTarget(appointmentDto);
        if (appointmentDto.getDoctorId() != null) {
            validateDoctorAndPatientAge(appointmentDto.getDoctorId(), appointmentDto.getPatientId());
        }
        Appointment appointment = buildAppointment(appointmentDto);
        appointmentRepository.save(appointment);
    }

    private Appointment buildAppointment(AppointmentCreateUpdateDto dto) {
        Appointment a = new Appointment();

        if (dto.getReferralId() != null) {
            a.setReferral(referralRepository.findById(dto.getReferralId())
                    .orElseThrow(() -> new EntityNotFoundException("Referral not found")));
        }

        if (dto.getDoctorId() != null) {
            a.setDoctor(doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new EntityNotFoundException("Doctor not found")));
            a.setDate(dto.getDate());
            a.setTime(dto.getTime());
        } else {
            a.setHospital(hospitalRepository.findById(dto.getHospitalId())
                    .orElseThrow(() -> new EntityNotFoundException("Hospital not found")));
            a.setDate(dto.getDate());
            a.setTime(EXAMINATION_TIME);
        }

        a.setId(UUID.randomUUID());
        return a;
    }



    public void validateDoctorAndPatientAge(UUID doctorID, UUID patientID) {
        if (doctorID == null) return;

        var patient = patientRepository.findById(patientID)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        var doctor = doctorRepository.findById(doctorID)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        int patientAge = Period.between(patient.getBirthDate(), LocalDate.now()).getYears();
        String doctorType = doctor.getType();

        if (patientAge < 18 && !"child".equals(doctorType)) {
            throw new DoctorSpecializationAgeRestrictionException(
                    "Incompatible patient age and doctor specialization: underage patient cannot be assigned to an adult doctor."
            );
        }
        if (patientAge >= 18 && !"adult".equals(doctorType)) {
            throw new DoctorSpecializationAgeRestrictionException(
                    "Incompatible patient age and doctor specialization: adult patient cannot be assigned to a pediatric doctor."
            );
        }
    }

    private void validateAppointmentTarget(AppointmentCreateUpdateDto appointmentDto) {
        boolean doctorAppointment = appointmentDto.getDoctorId() != null;
        boolean hospitalAppointment = appointmentDto.getHospitalId() != null;

        if (doctorAppointment && hospitalAppointment) {
            throw new AppointmentTargetConflictException(
                    "Cannot assign appointment to both doctor and hospital."
            );
        }

        if (!doctorAppointment && !hospitalAppointment) {
            throw new AppointmentTargetConflictException(
                    "Appointment must be assigned either to a doctor or to a hospital."
            );
        }
    }
}
