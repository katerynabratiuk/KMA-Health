package kma.health.app.kma_health.service;

import kma.health.app.kma_health.dto.FeedbackDto;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.repository.FeedbackRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public double calculateDoctorRating(Doctor doctor) {
        List<Feedback> feedbacks = feedbackRepository.findByDoctor(doctor);
        return calculateAverage(feedbacks);
    }

    public double calculateHospitalRating(Long hospitalId) {
        List<Feedback> feedbacks = feedbackRepository.findByHospital_Id(hospitalId);
        return calculateAverage(feedbacks);
    }

    private double calculateAverage(List<Feedback> feedbacks) {
        if (feedbacks == null || feedbacks.isEmpty())
            return 0.0;

        double average = feedbacks.stream()
                .mapToDouble(Feedback::getScore)
                .average()
                .orElse(0.0);

        return Math.round(average * 100.0) / 100.0;
    }

    public List<Feedback> getHospitalFeedbacks(Long hospitalId)
    {
        return feedbackRepository.findByHospital_Id(hospitalId);
    }

    public void createFeedback(FeedbackDto feedback)
    {
        Feedback feedbackEntity = FeedbackDto.toEntity(feedback);
        feedbackRepository.save(feedbackEntity);
    }
}
