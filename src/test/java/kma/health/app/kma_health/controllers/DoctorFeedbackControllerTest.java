package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.controller.api.DoctorFeedbackController;
import kma.health.app.kma_health.dto.FeedbackCreateUpdateDto;
import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.service.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorFeedbackControllerTest {

    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private DoctorFeedbackController controller;

    private UUID doctorId;
    private UUID patientId;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
        patientId = UUID.randomUUID();
    }

    // GET /api/doctor/{doctorId}/feedback tests
    @Test
    void testGetAllFeedbacks_Success() {
        Feedback feedback = new Feedback();
        feedback.setId(1L);
        feedback.setScore((short) 5);
        feedback.setComment("Great doctor");

        when(feedbackService.getDoctorFeedbacks(doctorId)).thenReturn(List.of(feedback));

        List<Feedback> result = controller.getAllFeedbacks(doctorId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals((short) 5, result.get(0).getScore());
        assertEquals("Great doctor", result.get(0).getComment());
    }

    @Test
    void testGetAllFeedbacks_EmptyList() {
        when(feedbackService.getDoctorFeedbacks(doctorId)).thenReturn(Collections.emptyList());

        List<Feedback> result = controller.getAllFeedbacks(doctorId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // GET /api/doctor/{doctorId}/feedback/my tests
    @Test
    void testGetMyFeedback_Found_Success() {
        Feedback feedback = new Feedback();
        feedback.setId(1L);
        feedback.setScore((short) 4);
        feedback.setComment("Good experience");

        when(feedbackService.getPatientFeedbackForDoctor(eq(doctorId), eq(patientId)))
                .thenReturn(Optional.of(feedback));

        ResponseEntity<Feedback> result = controller.getMyFeedback(doctorId, patientId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals((short) 4, result.getBody().getScore());
    }

    @Test
    void testGetMyFeedback_NotFound() {
        when(feedbackService.getPatientFeedbackForDoctor(eq(doctorId), eq(patientId)))
                .thenReturn(Optional.empty());

        ResponseEntity<Feedback> result = controller.getMyFeedback(doctorId, patientId);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
    }

    // POST /api/doctor/{doctorId}/feedback tests
    @Test
    void testCreateFeedback_Success() {
        FeedbackCreateUpdateDto feedbackDto = new FeedbackCreateUpdateDto();
        feedbackDto.setScore((short) 5);
        feedbackDto.setComment("Excellent doctor");

        doNothing().when(feedbackService).createFeedback(any(FeedbackCreateUpdateDto.class));

        controller.createFeedback(doctorId, patientId, feedbackDto);

        assertEquals(doctorId, feedbackDto.getDoctor_id());
        assertEquals(patientId, feedbackDto.getPatient_id());
        verify(feedbackService).createFeedback(feedbackDto);
    }

    @Test
    void testCreateFeedback_SetsIdsCorrectly() {
        FeedbackCreateUpdateDto feedbackDto = new FeedbackCreateUpdateDto();
        feedbackDto.setScore((short) 3);

        controller.createFeedback(doctorId, patientId, feedbackDto);

        assertEquals(doctorId, feedbackDto.getDoctor_id());
        assertEquals(patientId, feedbackDto.getPatient_id());
    }

    // DELETE /api/doctor/{doctorId}/feedback/{feedbackId} tests
    @Test
    void testDeleteFeedback_Success() {
        doNothing().when(feedbackService).deleteFeedback(1L);

        controller.deleteFeedback(1L);

        verify(feedbackService).deleteFeedback(1L);
    }

    @Test
    void testDeleteFeedback_DifferentId() {
        doNothing().when(feedbackService).deleteFeedback(42L);

        controller.deleteFeedback(42L);

        verify(feedbackService).deleteFeedback(42L);
    }
}
