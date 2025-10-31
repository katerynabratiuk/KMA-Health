package kma.health.app.kma_health.controller;

import kma.health.app.kma_health.dto.FeedbackDto;
import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.service.FeedbackService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/hospitals/{hospitalId}/feedback")
public class HospitalFeedbackController {

    FeedbackService feedbackService;

    @GetMapping()
    public List<Feedback> getAllFeedbacks(@PathVariable Long hospitalId)
    {
        return feedbackService.getHospitalFeedbacks(hospitalId);
    }

    @PostMapping()
    public void createFeedback(@PathVariable Long hospitalId,
                               @RequestBody FeedbackDto feedback)
    {
        feedback.setHospital_id(hospitalId);
        feedbackService.createFeedback(feedback);
    }

}
