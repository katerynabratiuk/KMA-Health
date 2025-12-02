package kma.health.app.kma_health.controller.api;

import kma.health.app.kma_health.dto.FeedbackCreateUpdateDto;
import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.service.FeedbackService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/doctor/{doctorId}/feedback")
public class DoctorFeedbackController {
    FeedbackService feedbackService;

    @GetMapping()
    public List<Feedback> getAllFeedbacks(@PathVariable UUID doctorId) {
        return feedbackService.getDoctorFeedbacks(doctorId);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Feedback> getMyFeedback(@PathVariable UUID doctorId,
            @AuthenticationPrincipal UUID patientId) {
        Optional<Feedback> feedback = feedbackService.getPatientFeedbackForDoctor(doctorId, patientId);
        return feedback.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping()
    @PreAuthorize("hasRole('PATIENT')")
    public void createFeedback(@PathVariable UUID doctorId,
            @AuthenticationPrincipal UUID patientId,
            @RequestBody FeedbackCreateUpdateDto feedback) {
        feedback.setDoctor_id(doctorId);
        feedback.setPatient_id(patientId);
        feedbackService.createFeedback(feedback);
    }

    @DeleteMapping("/{feedbackId}")
    @PreAuthorize("hasRole('PATIENT')")
    public void deleteFeedback(@PathVariable Long feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
    }
}
