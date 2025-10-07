package kma.health.app.kma_health.service;

import jakarta.persistence.EntityNotFoundException;
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

    public List<AppointmentShortViewDto> getAppointments(String patientPassportNumber) {

        List<Appointment> queryRes = appointmentRepository.findAllByReferralPatientPassportNumber(patientPassportNumber);
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

    public void createAppointment(AppointmentFullViewDto appointmentDto) {
        validateDoctorAndPatientAge(appointmentDto);
        validateAppointmentTarget(appointmentDto);

        Appointment appointment = buildAppointment(appointmentDto);
        appointmentRepository.save(appointment);
    }

    private Appointment buildAppointment(AppointmentFullViewDto appointmentDto) {
        Appointment appointment = new Appointment();
        appointment.setReferral(
                referralRepository.findById(appointmentDto.getReferralId())
                        .orElseThrow(() -> new EntityNotFoundException("Referral not found")));

        if (appointmentDto.getDoctorId() != null) {
            appointment.setDoctor(
                    doctorRepository.findById(appointmentDto.getDoctorId())
                            .orElseThrow(() -> new EntityNotFoundException("Doctor not found"))
            );
            appointment.setDate(appointmentDto.getDate());
            appointment.setTime(appointmentDto.getTime());

        } else {
            appointment.setHospital(
                    hospitalRepository.findById(appointmentDto.getHospitalId())
                            .orElseThrow(() -> new EntityNotFoundException("Hospital not found"))
            );
            appointment.setDate(appointmentDto.getDate());
            appointment.setTime(EXAMINATION_TIME);
        }

        return appointment;
    }

    private void validateDoctorAndPatientAge(AppointmentFullViewDto appointmentDto) {
        LocalDate patientBirthDate = patientRepository.findById(appointmentDto.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"))
                .getBirthDate();

        int patientAge = LocalDate.now().getYear() - patientBirthDate.getYear();

        String doctorType = doctorRepository.findById(appointmentDto.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"))
                .getType();

        if (patientAge < 18 && !"child".equals(doctorType)) {
            throw new DoctorSpecializationAgeRestrictionException(
                    "Incompatible patient age and doctor specialization: underage patient cannot be assigned to an adult doctor."
            );
        }

        if (patientAge > 18 && !"adult".equals(doctorType)) {
            throw new DoctorSpecializationAgeRestrictionException(
                    "Incompatible patient age and doctor specialization: adult patient cannot be assigned to a pediatric doctor."
            );
        }
    }

    private void validateAppointmentTarget(AppointmentFullViewDto appointmentDto) {
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
