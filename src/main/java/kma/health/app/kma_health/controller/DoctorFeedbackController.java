package kma.health.app.kma_health.controller;

import kma.health.app.kma_health.dto.FeedbackDto;
import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.service.FeedbackService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/doctor/{doctorId}/feedback")
public class DoctorFeedbackController {
    FeedbackService feedbackService;

    @GetMapping()
    public List<Feedback> getAllFeedbacks(@PathVariable UUID doctorId)
    {
        return feedbackService.getDoctorFeedbacks(doctorId);
    }

    @PostMapping()
    public void createFeedback(@PathVariable UUID doctorId,
                               @RequestBody FeedbackDto feedback)
    {
        feedback.setDoctor_id(doctorId);
        feedbackService.createFeedback(feedback);
    }

    @DeleteMapping("/{feedbackId}")
    public void deleteFeedback(@PathVariable Long feedbackId)
    {
        feedbackService.deleteFeedback(feedbackId);
    }
}
