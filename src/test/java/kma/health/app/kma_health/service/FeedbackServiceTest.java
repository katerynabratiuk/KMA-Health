package kma.health.app.kma_health.service;

import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.repository.FeedbackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

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
}
