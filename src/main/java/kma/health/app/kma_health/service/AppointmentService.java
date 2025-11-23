package kma.health.app.kma_health.service;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import kma.health.app.kma_health.dto.*;
import kma.health.app.kma_health.entity.Appointment;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.MedicalFile;
import kma.health.app.kma_health.enums.AppointmentStatus;
import kma.health.app.kma_health.exception.AppointmentNotFoundException;
import kma.health.app.kma_health.exception.AppointmentTargetConflictException;
import kma.health.app.kma_health.exception.DoctorSpecializationAgeRestrictionException;
import kma.health.app.kma_health.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static kma.health.app.kma_health.service.HospitalService.EXAMINATION_TIME;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final ReferralRepository referralRepository;
    private final MedicalFileRepository medicalFileRepository;

    @Value("${root.file.path}")
    private String filePath;

    public List<AppointmentFullViewDto> getAppointmentsForPatient(UUID patientId) {
        return appointmentRepository.findByReferral_Patient_Id(patientId)
                .stream()
                .map(AppointmentFullViewDto::new)
                .toList();
    }

    public List<AppointmentShortViewDto> getAppointmentsForPatient(UUID patientId, LocalDate start, LocalDate end) {
        return appointmentRepository.findByReferral_Patient_idAndDateBetween(patientId, start, end)
                .stream()
                .map(AppointmentShortViewDto::new)
                .toList();
    }

    public List<AppointmentShortViewDto> getAppointmentsForPatient(UUID patientId, LocalDate date) {
        return getAppointmentsForPatient(patientId, date, date);
    }

    private List<AppointmentShortViewDto> getAppointmentsForDoctor(UUID doctorId) {
        return appointmentRepository.findByDoctor_Id(doctorId)
                .stream()
                .map(AppointmentShortViewDto::new)
                .toList();
    }

    public List<AppointmentShortViewDto> getAppointmentsForDoctor(UUID doctorId, LocalDate start, LocalDate end) {
        return appointmentRepository.findByDoctor_IdAndDateBetween(doctorId, start, end)
                .stream()
                .map(AppointmentShortViewDto::new)
                .toList();
    }

    public List<AppointmentShortViewDto> getAppointmentsForDoctor(UUID doctorId, LocalDate date) {
        return getAppointmentsForDoctor(doctorId, date, date);
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
        if (appointmentDto.getDoctorId() != null)
            validateDoctorAndPatientAge(appointmentDto.getDoctorId(), appointmentDto.getPatientId());
        Appointment appointment = buildAppointment(appointmentDto);
        appointmentRepository.save(appointment);
    }

    @Transactional
    public void finishAppointment(UUID doctorId, UUID appointmentId, String diagnosis, List<MedicalFileUploadDto> medicalFilesDto)
            throws IOException {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment " + appointmentId + " not found."));

        if (!appointment.getDoctor().getId().equals(doctorId))
            throw new AccessDeniedException("Appointment " + appointmentId + " doesn't belong to doctor " + doctorId);
        if (appointment.getStatus() != AppointmentStatus.OPEN)
            throw new AccessDeniedException("Appointment " + appointmentId + " is not open.");

        appointment.setDiagnosis(diagnosis);
        appointment.setStatus(AppointmentStatus.FINISHED);

        if (medicalFilesDto != null && !medicalFilesDto.isEmpty()) {
            Set<MedicalFile> medicalFiles = new HashSet<>();
            for (MedicalFileUploadDto dto : medicalFilesDto) {
                String timestamp = java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
                MedicalFile file = new MedicalFile();
                file.setFileType(dto.getFileType());
                file.setName(dto.getName());
                file.setExtension(dto.getExtension());
                file.setLink("/med_files/" + timestamp + "." + dto.getExtension());
                file.setAppointment(appointment);
                file.setPatient(appointment.getReferral().getPatient());

                Path storagePath = Paths.get(filePath + "/med_files").resolve(timestamp + "." + dto.getExtension());
                Files.copy(dto.getFile().getInputStream(), storagePath, StandardCopyOption.REPLACE_EXISTING);

                medicalFileRepository.save(file);
                medicalFiles.add(file);
            }
            appointment.setMedicalFiles(medicalFiles);
        }
        appointmentRepository.save(appointment);
    }

    public void cancelAppointment(UUID doctorId, UUID patientId, UUID appointmentId) throws AccessDeniedException {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment" + appointmentId + " not found."));

        if (doctorId != null) {
            if (!appointment.getDoctor().getId().equals(doctorId))
                throw new AccessDeniedException("Appointment " + appointmentId + "doesn't belong to " + doctorId);
            else if (appointment.getStatus() != AppointmentStatus.OPEN)
                throw new AccessDeniedException("Appointment " + appointmentId + "is not open.");
        }

        if (patientId != null) {
            if (!appointment.getReferral().getPatient().getId().equals(patientId))
                throw new AccessDeniedException("Appointment " + appointmentId + "doesn't belong to " + patientId);
            else if (appointment.getStatus() == AppointmentStatus.OPEN)
                throw new AccessDeniedException("Appointment " + appointmentId + "is already open.");
        }

        appointmentRepository.delete(appointment);
    }

    private Appointment buildAppointment(AppointmentCreateUpdateDto dto) {
        Appointment a = new Appointment();
        if (dto.getReferralId() != null)
            a.setReferral(referralRepository.findById(dto.getReferralId())
                    .orElseThrow(() -> new EntityNotFoundException("Referral not found")));

        LocalDate date = dto.getDate();
        LocalTime time;

        if (dto.getDoctorId() != null) {
            Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));
            a.setDoctor(doctor);

            time = dto.getTime();
            validateFutureDateTime(date, time);
            a.setDate(date);
            a.setTime(time);
        } else {
            a.setHospital(hospitalRepository.findById(dto.getHospitalId())
                    .orElseThrow(() -> new EntityNotFoundException("Hospital not found")));

            time = EXAMINATION_TIME;
            validateFutureDateTime(date, time);

            a.setDate(date);
            a.setTime(time);
        }
        a.setStatus(AppointmentStatus.SCHEDULED);
        return a;
    }

    private void validateFutureDateTime(LocalDate date, LocalTime time) {
        if (date == null || time == null)
            throw new IllegalArgumentException("Date and time must be provided");
        LocalDateTime appointmentDateTime = LocalDateTime.of(date, time);
        if (!appointmentDateTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment date and time must be in the future");
        }
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

    public boolean haveOpenAppointment(UUID doctorId, UUID patientId) {
        List<Appointment> appointments = appointmentRepository
                .findByDoctor_IdAndReferral_Patient_Id(doctorId, patientId);

        return appointments.stream()
                .anyMatch(app -> {
                    LocalDateTime start = LocalDateTime.of(app.getDate(), app.getTime());
                    LocalDateTime end = start.plusMinutes(20);
                    LocalDateTime now = LocalDateTime.now();
                    return !start.isAfter(now) && !end.isBefore(now);
                });
    }

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void openAppointments() {
        LocalDateTime now = LocalDateTime.now();
        List<Appointment> scheduledAppointments = appointmentRepository
                .findByStatus(AppointmentStatus.SCHEDULED);
        for (Appointment a : scheduledAppointments) {
            LocalDateTime appointmentDateTime = LocalDateTime.of(a.getDate(), a.getTime());
            if (!appointmentDateTime.isAfter(now))
                a.setStatus(AppointmentStatus.OPEN);
        }
        appointmentRepository.saveAll(scheduledAppointments);
    }
}
