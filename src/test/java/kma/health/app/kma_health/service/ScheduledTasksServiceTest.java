package kma.health.app.kma_health.service;

import kma.health.app.kma_health.entity.*;
import kma.health.app.kma_health.enums.AppointmentStatus;
import kma.health.app.kma_health.repository.AppointmentRepository;
import kma.health.app.kma_health.repository.ReminderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduledTasksServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private ReminderRepository reminderRepository;

    @InjectMocks
    private ScheduledTasksService scheduledTasksService;

    @Test
    void testGenerateAppointmentReminders_WithUpcomingAppointments() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Doctor doctor = new Doctor();
        doctor.setFullName("Dr. Smith");

        Referral referral = new Referral();
        referral.setPatient(patient);

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setDate(LocalDate.now());
        appointment.setTime(LocalTime.of(10, 0));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setReferral(referral);
        appointment.setDoctor(doctor);

        when(appointmentRepository.findByStatus(AppointmentStatus.SCHEDULED))
                .thenReturn(List.of(appointment));
        when(reminderRepository.findByPatientAndReminderDate(any(Patient.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        scheduledTasksService.generateAppointmentReminders();

        verify(reminderRepository).save(any(Reminder.class));
    }

    @Test
    void testGenerateAppointmentReminders_WithTomorrowAppointment() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Hospital hospital = new Hospital();
        hospital.setName("Test Hospital");

        Referral referral = new Referral();
        referral.setPatient(patient);

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setDate(LocalDate.now().plusDays(1));
        appointment.setTime(LocalTime.of(14, 30));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setReferral(referral);
        appointment.setDoctor(null);
        appointment.setHospital(hospital);

        when(appointmentRepository.findByStatus(AppointmentStatus.SCHEDULED))
                .thenReturn(List.of(appointment));
        when(reminderRepository.findByPatientAndReminderDate(any(Patient.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        scheduledTasksService.generateAppointmentReminders();

        verify(reminderRepository).save(any(Reminder.class));
    }

    @Test
    void testGenerateAppointmentReminders_ReminderAlreadyExists() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Referral referral = new Referral();
        referral.setPatient(patient);

        UUID appointmentId = UUID.randomUUID();
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setDate(LocalDate.now());
        appointment.setTime(LocalTime.of(10, 0));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setReferral(referral);

        Reminder existingReminder = new Reminder();
        existingReminder.setText("Нагадування: запис " + appointmentId + " на ...");

        when(appointmentRepository.findByStatus(AppointmentStatus.SCHEDULED))
                .thenReturn(List.of(appointment));
        when(reminderRepository.findByPatientAndReminderDate(any(Patient.class), any(LocalDate.class)))
                .thenReturn(List.of(existingReminder));

        scheduledTasksService.generateAppointmentReminders();

        verify(reminderRepository, never()).save(any(Reminder.class));
    }

    @Test
    void testGenerateAppointmentReminders_NoUpcomingAppointments() {
        Appointment appointment = new Appointment();
        appointment.setDate(LocalDate.now().plusDays(7)); // Not today or tomorrow
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findByStatus(AppointmentStatus.SCHEDULED))
                .thenReturn(List.of(appointment));

        scheduledTasksService.generateAppointmentReminders();

        verify(reminderRepository, never()).save(any(Reminder.class));
    }

    @Test
    void testMarkMissedAppointments_WithMissedAppointments() {
        Appointment missedAppointment = new Appointment();
        missedAppointment.setId(UUID.randomUUID());
        missedAppointment.setDate(LocalDate.now().minusDays(1));
        missedAppointment.setTime(LocalTime.of(10, 0));
        missedAppointment.setStatus(AppointmentStatus.OPEN);

        List<Appointment> openAppointments = new ArrayList<>();
        openAppointments.add(missedAppointment);

        when(appointmentRepository.findByStatus(AppointmentStatus.OPEN))
                .thenReturn(openAppointments);

        scheduledTasksService.markMissedAppointments();

        verify(appointmentRepository).saveAll(openAppointments);
    }

    @Test
    void testMarkMissedAppointments_NoMissedAppointments() {
        Appointment recentAppointment = new Appointment();
        recentAppointment.setId(UUID.randomUUID());
        recentAppointment.setDate(LocalDate.now());
        recentAppointment.setTime(LocalTime.now().plusHours(1));
        recentAppointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findByStatus(AppointmentStatus.OPEN))
                .thenReturn(List.of(recentAppointment));

        scheduledTasksService.markMissedAppointments();

        verify(appointmentRepository, never()).saveAll(any());
    }

    @Test
    void testMarkMissedAppointments_NoOpenAppointments() {
        when(appointmentRepository.findByStatus(AppointmentStatus.OPEN))
                .thenReturn(Collections.emptyList());

        scheduledTasksService.markMissedAppointments();

        verify(appointmentRepository, never()).saveAll(any());
    }
}

