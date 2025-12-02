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

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    @Test
    public void testCalculateDoctorRating_ShouldReturnCorrectAverageRating() {
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
    public void testCalculateDoctorRating_ShouldReturnZeroWhenNoFeedbacks() {
        UUID doctorId = UUID.randomUUID();

        when(feedbackRepository.findByDoctor_Id(doctorId)).thenReturn(Collections.emptyList());

        double rating = feedbackService.calculateDoctorRating(doctorId);

        assertEquals(0.0, rating);
    }

    @Test
    public void testCalculateHospitalRating_ShouldReturnCorrectAverageRating() {
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
    public void testCalculateHospitalRating_ShouldReturnZeroWhenNoFeedbacks() {
        Long hospitalId = 1L;

        when(feedbackRepository.findByHospital_Id(hospitalId)).thenReturn(Collections.emptyList());

        double rating = feedbackService.calculateHospitalRating(hospitalId);

        assertEquals(0.0, rating);
    }

    @Test
    public void testGetHospitalFeedbacks_ShouldReturnFeedbacksList() {
        Long hospitalId = 1L;
        Feedback feedback1 = new Feedback();
        Feedback feedback2 = new Feedback();

        when(feedbackRepository.findByHospital_Id(hospitalId))
                .thenReturn(Arrays.asList(feedback1, feedback2));

        List<Feedback> result = feedbackService.getHospitalFeedbacks(hospitalId);

        assertEquals(2, result.size());
    }

    @Test
    public void testGetDoctorFeedbacks_ShouldReturnFeedbacksList() {
        UUID doctorId = UUID.randomUUID();
        Feedback feedback1 = new Feedback();
        Feedback feedback2 = new Feedback();

        when(feedbackRepository.findByDoctor_Id(doctorId))
                .thenReturn(Arrays.asList(feedback1, feedback2));

        List<Feedback> result = feedbackService.getDoctorFeedbacks(doctorId);

        assertEquals(2, result.size());
    }

    @Test
    public void testCreateFeedback_ForDoctor_ShouldSaveFeedback() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        FeedbackCreateUpdateDto dto = new FeedbackCreateUpdateDto();
        dto.setPatient_id(patientId);
        dto.setDoctor_id(doctorId);
        dto.setScore((short) 5);

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.FINISHED);
        appointment.setTime(LocalTime.now().minusHours(1)); // Past time
        when(appointmentRepository.findByReferral_Patient_IdAndDoctor_Id(patientId, doctorId))
                .thenReturn(Collections.singletonList(appointment));

        feedbackService.createFeedback(dto);

        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    public void testCreateFeedback_ForDoctor_ShouldThrowExceptionWhenNoHistory() {
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
    public void testCreateFeedback_ForHospital_ShouldSaveFeedback() {
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
    public void testCreateFeedback_ForHospital_ShouldThrowExceptionWhenNoHistory() {
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
    public void testDeleteFeedback_ShouldDeleteById() {
        Long feedbackId = 1L;

        feedbackService.deleteFeedback(feedbackId);

        verify(feedbackRepository, times(1)).deleteById(feedbackId);
    }

    @Test
    public void testCalculateDoctorRating_ShouldRoundToTwoDecimalPlaces() {
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
}
