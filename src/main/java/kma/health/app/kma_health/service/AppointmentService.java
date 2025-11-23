package kma.health.app.kma_health.service;

import java.io.IOException;
import java.nio.file.*;
import java.sql.Ref;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import kma.health.app.kma_health.dto.*;
import kma.health.app.kma_health.entity.*;
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
    private final LabAssistantRepository labAssistantRepository;
    private final DoctorTypeRepository doctorTypeRepository;
    private final HospitalService hospitalService;

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

    public AppointmentFullViewDto getFullAppointment(UUID id, UUID userId) throws AccessDeniedException {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        UUID doctorId = appointment.getDoctor() != null ? appointment.getDoctor().getId() : null;
        UUID patientId = appointment.getReferral().getPatient().getId();

        boolean isDoctor = userId.equals(doctorId);
        boolean isPatient = userId.equals(patientId);

        if (!isDoctor && !isPatient)
            throw new AccessDeniedException("Appointment does not belong to user " + userId);

        return appointmentRepository.findById(id)
                .map(AppointmentFullViewDto::new)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment is not found."));
    }

    public void deleteAppointment(UUID id) {
        if (appointmentRepository.existsById(id))
            appointmentRepository.deleteById(id);
        else throw new AppointmentNotFoundException("Appointment is not found.");
    }

    public void createAppointment(AppointmentCreateUpdateDto appointmentDto, UUID userId) throws AccessDeniedException {
        if (!userId.equals(appointmentDto.getPatientId()))
            throw new AccessDeniedException("One user cannot create an appointment for another user");

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

        UUID doctorIdFromAppointment = appointment.getDoctor() != null ? appointment.getDoctor().getId() : null;
        UUID labAssistantIdFromAppointment = appointment.getLabAssistant() != null ? appointment.getLabAssistant().getId() : null;

        if (!doctorId.equals(doctorIdFromAppointment) &&
            !doctorId.equals(labAssistantIdFromAppointment)) {
            throw new AccessDeniedException(
                    "Appointment " + appointmentId + " doesn't belong to doctor/lab assistant " + doctorId
            );
        }

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

        UUID doctorIdFromAppointment = appointment.getDoctor() != null ? appointment.getDoctor().getId() : null;
        UUID labAssistantIdFromAppointment = appointment.getLabAssistant() != null ? appointment.getLabAssistant().getId() : null;

        if (doctorIdFromAppointment != null || labAssistantIdFromAppointment != null) {
            if (!doctorId.equals(doctorIdFromAppointment) &&
                !doctorId.equals(labAssistantIdFromAppointment)) {
                throw new AccessDeniedException(
                        "Appointment " + appointmentId + " doesn't belong to doctor/lab assistant " + doctorId
                );
            }
            if (appointment.getStatus() != AppointmentStatus.OPEN)
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
        Appointment appointment = new Appointment();

        Referral referral = resolveReferral(dto);
        appointment.setReferral(referral);

        if (dto.getDoctorId() != null)
            assignDoctorAppointment(appointment, dto);
        else
            assignHospitalAppointment(appointment, dto, referral);

        appointment.setStatus(AppointmentStatus.SCHEDULED);
        return appointment;
    }

    private Referral resolveReferral(AppointmentCreateUpdateDto dto) {
        if (dto.getReferralId() != null) {
            return referralRepository.findById(dto.getReferralId())
                    .orElseThrow(() -> new EntityNotFoundException("Referral not found"));
        }
        return buildFamilyDoctorReferral(dto);
    }

    private void assignDoctorAppointment(Appointment appointment, AppointmentCreateUpdateDto dto) {
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        Referral referral = appointment.getReferral();

        if (referral.getDoctorType() == null)
            throw new IllegalArgumentException("Referral doctor type is null");

        if (!doctor.getDoctorType().equals(referral.getDoctorType())) {
            throw new AppointmentTargetConflictException(
                    "Cannot assign to doctor of type " + doctor.getDoctorType() +
                    " using referral for " + referral.getDoctorType()
            );
        }

        appointment.setDoctor(doctor);

        validateFutureDateTime(dto.getDate(), dto.getTime());
        appointment.setDate(dto.getDate());
        appointment.setTime(dto.getTime());
    }

    private void assignHospitalAppointment(Appointment appointment, AppointmentCreateUpdateDto dto, Referral referral) {

        if (dto.getHospitalId() == null)
            throw new IllegalArgumentException("Hospital id is null");

        Hospital hospital = hospitalRepository.findById(dto.getHospitalId())
                .orElseThrow(() -> new EntityNotFoundException("Hospital not found"));

        if (!hospitalService.providesExamination(hospital, referral.getExamination())) {
            throw new AppointmentTargetConflictException(
                    "Hospital doesn't provide examination " + referral.getExamination().getExamName()
            );
        }

        appointment.setHospital(hospital);

        validateFutureDateTime(dto.getDate(), EXAMINATION_TIME);

        appointment.setDate(dto.getDate());
        appointment.setTime(EXAMINATION_TIME);
    }

    private Referral buildFamilyDoctorReferral(AppointmentCreateUpdateDto dto) {
        Referral referral = new Referral();
        referral.setDoctorType(doctorTypeRepository.findByTypeName("Family doctor")
                .orElseThrow(() -> new EntityNotFoundException("Doctor type not found")));

        referral.setPatient(patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found")));

        referral.setValidUntil(dto.getDate().plusDays(1));

        return referral;
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

    public void assignLabAssistantToAppointment(UUID appointmentId, UUID labAssistantId) throws AccessDeniedException {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        LabAssistant labAssistant = labAssistantRepository.findById(labAssistantId)
                .orElseThrow(() -> new EntityNotFoundException("Lab assistant not found"));

        if (appointment.getStatus() == AppointmentStatus.FINISHED)
            throw new AccessDeniedException("Appointment is already finished");

        if (appointment.getLabAssistant() != null)
            throw new AccessDeniedException("Lab assistant already assigned");

        if (appointment.getHospital() == null)
            throw new AppointmentTargetConflictException("Appointment's target is doctor");

        if (!appointment.getHospital().equals(labAssistant.getHospital()))
            throw new AccessDeniedException("Appointment is not from this assistant's hospital");

        appointment.setLabAssistant(labAssistant);
        appointmentRepository.save(appointment);
    }
}
