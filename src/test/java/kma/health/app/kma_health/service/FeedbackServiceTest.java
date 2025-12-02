package kma.health.app.kma_health.service;

import kma.health.app.kma_health.dto.FeedbackCreateUpdateDto;
import kma.health.app.kma_health.entity.Appointment;
import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.enums.AppointmentStatus;
import kma.health.app.kma_health.exception.FeedbackNotPermitted;
import kma.health.app.kma_health.repository.AppointmentRepository;
import kma.health.app.kma_health.repository.FeedbackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    @Test
     void testCalculateDoctorRating_ShouldReturnCorrectAverageRating() {
        UUID doctorId = UUID.randomUUID();

        Feedback feedback1 = new Feedback();
        feedback1.setScore((short) 5);

        Feedback feedback2 = new Feedback();
        feedback2.setScore((short) 4);

        Feedback feedback3 = new Feedback();
        feedback3.setScore((short) 3);

        when(feedbackRepository.findByDoctor_Id(doctorId))
                .thenReturn(Arrays.asList(feedback1, feedback2, feedback3));

        double rating = feedbackService.calculateDoctorRating(doctorId);

        assertEquals(4.0, rating);
    }

    @Test
     void testCalculateDoctorRating_ShouldReturnZeroWhenNoFeedbacks() {
        UUID doctorId = UUID.randomUUID();

        when(feedbackRepository.findByDoctor_Id(doctorId)).thenReturn(Collections.emptyList());

        double rating = feedbackService.calculateDoctorRating(doctorId);

        assertEquals(0.0, rating);
    }

    @Test
     void testCalculateHospitalRating_ShouldReturnCorrectAverageRating() {
        Long hospitalId = 1L;

        Feedback feedback1 = new Feedback();
        feedback1.setScore((short) 5);

        Feedback feedback2 = new Feedback();
        feedback2.setScore((short) 3);

        when(feedbackRepository.findByHospital_Id(hospitalId))
                .thenReturn(Arrays.asList(feedback1, feedback2));

        double rating = feedbackService.calculateHospitalRating(hospitalId);

        assertEquals(4.0, rating);
    }

    @Test
     void testCalculateHospitalRating_ShouldReturnZeroWhenNoFeedbacks() {
        Long hospitalId = 1L;

        when(feedbackRepository.findByHospital_Id(hospitalId)).thenReturn(Collections.emptyList());

        double rating = feedbackService.calculateHospitalRating(hospitalId);

        assertEquals(0.0, rating);
    }

    @Test
     void testGetHospitalFeedbacks_ShouldReturnFeedbacksList() {
        Long hospitalId = 1L;
        Feedback feedback1 = new Feedback();
        Feedback feedback2 = new Feedback();

        when(feedbackRepository.findByHospital_Id(hospitalId))
                .thenReturn(Arrays.asList(feedback1, feedback2));

        List<Feedback> result = feedbackService.getHospitalFeedbacks(hospitalId);

        assertEquals(2, result.size());
    }

    @Test
     void testGetDoctorFeedbacks_ShouldReturnFeedbacksList() {
        UUID doctorId = UUID.randomUUID();
        Feedback feedback1 = new Feedback();
        Feedback feedback2 = new Feedback();

        when(feedbackRepository.findByDoctor_Id(doctorId))
                .thenReturn(Arrays.asList(feedback1, feedback2));

        List<Feedback> result = feedbackService.getDoctorFeedbacks(doctorId);

        assertEquals(2, result.size());
    }

    @Test
     void testCreateFeedback_ForDoctor_ShouldSaveFeedback() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        FeedbackCreateUpdateDto dto = new FeedbackCreateUpdateDto();
        dto.setPatient_id(patientId);
        dto.setDoctor_id(doctorId);
        dto.setScore((short) 5);

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.FINISHED);
        appointment.setTime(LocalTime.now().minusHours(1));
        when(appointmentRepository.findByReferral_Patient_IdAndDoctor_Id(patientId, doctorId))
                .thenReturn(Collections.singletonList(appointment));

        feedbackService.createFeedback(dto);

        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
     void testCreateFeedback_ForDoctor_ShouldThrowExceptionWhenNoHistory() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        FeedbackCreateUpdateDto dto = new FeedbackCreateUpdateDto();
        dto.setPatient_id(patientId);
        dto.setDoctor_id(doctorId);

        when(appointmentRepository.findByReferral_Patient_IdAndDoctor_Id(patientId, doctorId))
                .thenReturn(Collections.emptyList());

        assertThrows(FeedbackNotPermitted.class, () -> {
            feedbackService.createFeedback(dto);
        });

        verify(feedbackRepository, never()).save(any(Feedback.class));
    }

    @Test
     void testCreateFeedback_ForHospital_ShouldSaveFeedback() {
        UUID patientId = UUID.randomUUID();
        Long hospitalId = 1L;

        FeedbackCreateUpdateDto dto = new FeedbackCreateUpdateDto();
        dto.setPatient_id(patientId);
        dto.setHospital_id(hospitalId);
        dto.setScore((short) 4);

        Appointment appointment = new Appointment();
        when(appointmentRepository.findByReferral_Patient_IdAndHospital_Id(patientId, hospitalId))
                .thenReturn(Collections.singletonList(appointment));

        feedbackService.createFeedback(dto);

        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
     void testCreateFeedback_ForHospital_ShouldThrowExceptionWhenNoHistory() {
        UUID patientId = UUID.randomUUID();
        Long hospitalId = 1L;

        FeedbackCreateUpdateDto dto = new FeedbackCreateUpdateDto();
        dto.setPatient_id(patientId);
        dto.setHospital_id(hospitalId);

        when(appointmentRepository.findByReferral_Patient_IdAndHospital_Id(patientId, hospitalId))
                .thenReturn(Collections.emptyList());

        assertThrows(FeedbackNotPermitted.class, () -> {
            feedbackService.createFeedback(dto);
        });
    }

    @Test
     void testDeleteFeedback_ShouldDeleteById() {
        Long feedbackId = 1L;

        feedbackService.deleteFeedback(feedbackId);

        verify(feedbackRepository, times(1)).deleteById(feedbackId);
    }

    @Test
     void testCalculateDoctorRating_ShouldRoundToTwoDecimalPlaces() {
        UUID doctorId = UUID.randomUUID();

        Feedback feedback1 = new Feedback();
        feedback1.setScore((short) 5);

        Feedback feedback2 = new Feedback();
        feedback2.setScore((short) 4);

        Feedback feedback3 = new Feedback();
        feedback3.setScore((short) 4);

        when(feedbackRepository.findByDoctor_Id(doctorId))
                .thenReturn(Arrays.asList(feedback1, feedback2, feedback3));

        double rating = feedbackService.calculateDoctorRating(doctorId);

        assertEquals(4.33, rating);
    }

    @Test
     void testCalculateAverage_ShouldReturnZeroForNullFeedbacks() {
        UUID doctorId = UUID.randomUUID();

        when(feedbackRepository.findByDoctor_Id(doctorId)).thenReturn(null);

        double rating = feedbackService.calculateDoctorRating(doctorId);

        assertEquals(0.0, rating);
    }

    @Test
     void testGetPatientFeedbackForDoctor_ShouldReturnFeedback() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Feedback feedback = new Feedback();
        feedback.setScore((short) 5);

        when(feedbackRepository.findByDoctor_IdAndPatient_Id(doctorId, patientId))
                .thenReturn(Optional.of(feedback));

        Optional<Feedback> result = feedbackService.getPatientFeedbackForDoctor(doctorId, patientId);

        assertTrue(result.isPresent());
        assertEquals((short) 5, result.get().getScore());
    }

    @Test
     void testGetPatientFeedbackForDoctor_ShouldReturnEmptyWhenNotFound() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        when(feedbackRepository.findByDoctor_IdAndPatient_Id(doctorId, patientId))
                .thenReturn(Optional.empty());

        Optional<Feedback> result = feedbackService.getPatientFeedbackForDoctor(doctorId, patientId);

        assertTrue(result.isEmpty());
    }

    @Test
     void testPatientCanRateDoctor_ShouldReturnTrue_WhenFinishedAppointmentExists() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.FINISHED);
        appointment.setTime(LocalTime.of(8, 0));

        when(appointmentRepository.findByReferral_Patient_IdAndDoctor_Id(patientId, doctorId))
                .thenReturn(Collections.singletonList(appointment));

        boolean result = feedbackService.patientCanRateDoctor(doctorId, patientId);

        assertTrue(result);
    }

    @Test
     void testPatientCanRateDoctor_ShouldReturnFalse_WhenNoFinishedAppointment() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setTime(LocalTime.of(10, 0));

        when(appointmentRepository.findByReferral_Patient_IdAndDoctor_Id(patientId, doctorId))
                .thenReturn(Collections.singletonList(appointment));

        boolean result = feedbackService.patientCanRateDoctor(doctorId, patientId);

        assertFalse(result);
    }

    @Test
     void testPatientCanRateDoctor_ShouldReturnFalse_WhenNoAppointments() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        when(appointmentRepository.findByReferral_Patient_IdAndDoctor_Id(patientId, doctorId))
                .thenReturn(Collections.emptyList());

        boolean result = feedbackService.patientCanRateDoctor(doctorId, patientId);

        assertFalse(result);
    }

    @Test
     void testPatientCanRateDoctor_ShouldReturnFalse_WhenAppointmentInFuture() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.FINISHED);
        appointment.setTime(LocalTime.of(23, 59));

        when(appointmentRepository.findByReferral_Patient_IdAndDoctor_Id(patientId, doctorId))
                .thenReturn(Collections.singletonList(appointment));

        boolean result = feedbackService.patientCanRateDoctor(doctorId, patientId);

        assertFalse(result);
    }

    @Test
     void testCreateFeedback_ForDoctor_ShouldUpdateExistingFeedback() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        FeedbackCreateUpdateDto dto = new FeedbackCreateUpdateDto();
        dto.setPatient_id(patientId);
        dto.setDoctor_id(doctorId);
        dto.setScore((short) 4);
        dto.setComment("Updated comment");
        dto.setDate(LocalDate.now());

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.FINISHED);
        appointment.setTime(LocalTime.now().minusHours(1));
        when(appointmentRepository.findByReferral_Patient_IdAndDoctor_Id(patientId, doctorId))
                .thenReturn(Collections.singletonList(appointment));

        Feedback existingFeedback = new Feedback();
        existingFeedback.setScore((short) 3);
        when(feedbackRepository.findByDoctor_IdAndPatient_Id(doctorId, patientId))
                .thenReturn(Optional.of(existingFeedback));

        feedbackService.createFeedback(dto);

        verify(feedbackRepository, times(1)).save(existingFeedback);
        assertEquals((short) 4, existingFeedback.getScore());
        assertEquals("Updated comment", existingFeedback.getComment());
    }

    @Test
     void testCreateFeedback_ForHospital_ShouldUpdateExistingFeedback() {
        UUID patientId = UUID.randomUUID();
        Long hospitalId = 1L;

        FeedbackCreateUpdateDto dto = new FeedbackCreateUpdateDto();
        dto.setPatient_id(patientId);
        dto.setHospital_id(hospitalId);
        dto.setScore((short) 5);
        dto.setComment("Great hospital");
        dto.setDate(LocalDate.now());

        Appointment appointment = new Appointment();
        when(appointmentRepository.findByReferral_Patient_IdAndHospital_Id(patientId, hospitalId))
                .thenReturn(Collections.singletonList(appointment));

        Feedback existingFeedback = new Feedback();
        existingFeedback.setScore((short) 3);
        when(feedbackRepository.findByHospital_IdAndPatient_Id(hospitalId, patientId))
                .thenReturn(Optional.of(existingFeedback));

        feedbackService.createFeedback(dto);

        verify(feedbackRepository, times(1)).save(existingFeedback);
        assertEquals((short) 5, existingFeedback.getScore());
    }

    @Test
     void testCreateFeedback_ForDoctor_NoExistingFeedback() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        FeedbackCreateUpdateDto dto = new FeedbackCreateUpdateDto();
        dto.setPatient_id(patientId);
        dto.setDoctor_id(doctorId);
        dto.setScore((short) 5);

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.FINISHED);
        appointment.setTime(LocalTime.now().minusHours(1));
        when(appointmentRepository.findByReferral_Patient_IdAndDoctor_Id(patientId, doctorId))
                .thenReturn(Collections.singletonList(appointment));

        when(feedbackRepository.findByDoctor_IdAndPatient_Id(doctorId, patientId))
                .thenReturn(Optional.empty());

        feedbackService.createFeedback(dto);

        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
     void testCreateFeedback_ForHospital_NoExistingFeedback() {
        UUID patientId = UUID.randomUUID();
        Long hospitalId = 1L;

        FeedbackCreateUpdateDto dto = new FeedbackCreateUpdateDto();
        dto.setPatient_id(patientId);
        dto.setHospital_id(hospitalId);
        dto.setScore((short) 4);

        Appointment appointment = new Appointment();
        when(appointmentRepository.findByReferral_Patient_IdAndHospital_Id(patientId, hospitalId))
                .thenReturn(Collections.singletonList(appointment));

        when(feedbackRepository.findByHospital_IdAndPatient_Id(hospitalId, patientId))
                .thenReturn(Optional.empty());

        feedbackService.createFeedback(dto);

        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
     void testCreateFeedback_WithNullDoctorId() {
        UUID patientId = UUID.randomUUID();
        Long hospitalId = 1L;

        FeedbackCreateUpdateDto dto = new FeedbackCreateUpdateDto();
        dto.setPatient_id(patientId);
        dto.setDoctor_id(null);
        dto.setHospital_id(hospitalId);
        dto.setScore((short) 4);

        Appointment appointment = new Appointment();
        when(appointmentRepository.findByReferral_Patient_IdAndHospital_Id(patientId, hospitalId))
                .thenReturn(Collections.singletonList(appointment));

        when(feedbackRepository.findByHospital_IdAndPatient_Id(hospitalId, patientId))
                .thenReturn(Optional.empty());

        feedbackService.createFeedback(dto);

        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
     void testCreateFeedback_WithNullPatientId() {
        UUID doctorId = UUID.randomUUID();

        FeedbackCreateUpdateDto dto = new FeedbackCreateUpdateDto();
        dto.setPatient_id(null);
        dto.setDoctor_id(doctorId);
        dto.setScore((short) 5);

        feedbackService.createFeedback(dto);

        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }
}
