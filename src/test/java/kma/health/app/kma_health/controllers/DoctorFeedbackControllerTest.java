package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.controller.api.DoctorFeedbackController;
import kma.health.app.kma_health.dto.FeedbackCreateUpdateDto;
import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.service.FeedbackService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorFeedbackControllerTest {

    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private DoctorFeedbackController controller;

    @Test
    void testGetAllFeedbacks() {
        UUID doctorId = UUID.randomUUID();
        List<Feedback> feedbacks = Collections.emptyList();
        when(feedbackService.getDoctorFeedbacks(doctorId)).thenReturn(feedbacks);

        List<Feedback> result = controller.getAllFeedbacks(doctorId);

        assertEquals(feedbacks, result);
    }

    @Test
    void testCreateFeedback() {
        UUID doctorId = UUID.randomUUID();
        FeedbackCreateUpdateDto dto = new FeedbackCreateUpdateDto();

        controller.createFeedback(doctorId, dto);

        assertEquals(doctorId, dto.getDoctor_id());
        verify(feedbackService).createFeedback(dto);
    }

    @Test
    void testDeleteFeedback() {
        controller.deleteFeedback(1L);

        verify(feedbackService).deleteFeedback(1L);
    }
}
