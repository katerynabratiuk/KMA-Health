package kma.health.app.kma_health.controller.api;

import kma.health.app.kma_health.dto.FeedbackCreateUpdateDto;
import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.service.FeedbackService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/doctor/{doctorId}/feedback")
public class DoctorFeedbackController {
    FeedbackService feedbackService;

    @GetMapping()
    public List<Feedback> getAllFeedbacks(@PathVariable UUID doctorId)
    {
        return feedbackService.getDoctorFeedbacks(doctorId);
    }

    @PostMapping()
    @PreAuthorize("hasRole('PATIENT')")
    public void createFeedback(@PathVariable UUID doctorId,
                               @RequestBody FeedbackCreateUpdateDto feedback)
    {
        feedback.setDoctor_id(doctorId);
        feedbackService.createFeedback(feedback);
    }

    @DeleteMapping("/{feedbackId}")
    @PreAuthorize("hasRole('PATIENT')")
    public void deleteFeedback(@PathVariable Long feedbackId)
    {
        feedbackService.deleteFeedback(feedbackId);
    }
}
