package kma.health.app.kma_health.controller.api;

import kma.health.app.kma_health.dto.FeedbackCreateUpdateDto;
import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.service.FeedbackService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/hospitals/{hospitalId}/feedback")
public class HospitalFeedbackController {

    FeedbackService feedbackService;

    @GetMapping()
    public List<Feedback> getAllFeedbacks(@PathVariable Long hospitalId)
    {
        return feedbackService.getHospitalFeedbacks(hospitalId);
    }

    @PostMapping()
    @PreAuthorize("hasRole('PATIENT')")
    public void createFeedback(@PathVariable Long hospitalId,
                               @RequestBody FeedbackCreateUpdateDto feedback)
    {
        feedback.setHospital_id(hospitalId);
        feedbackService.createFeedback(feedback);
    }

    @DeleteMapping("/{feedbackId}")
    @PreAuthorize("hasRole('PATIENT')")
    public void deleteFeedback(@PathVariable Long feedbackId)
    {
        feedbackService.deleteFeedback(feedbackId);
    }

}
