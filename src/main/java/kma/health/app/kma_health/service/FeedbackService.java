package kma.health.app.kma_health.service;

import kma.health.app.kma_health.dto.FeedbackCreateUpdateDto;
import kma.health.app.kma_health.entity.Appointment;
import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.enums.AppointmentStatus;
import kma.health.app.kma_health.exception.FeedbackNotPermitted;
import kma.health.app.kma_health.repository.AppointmentRepository;
import kma.health.app.kma_health.repository.FeedbackRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Component
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final AppointmentRepository appointmentRepository;

    public double calculateDoctorRating(UUID id) {
        List<Feedback> feedbacks = feedbackRepository.findByDoctor_Id(id);
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

    public List<Feedback> getHospitalFeedbacks(Long hospitalId) {
        return feedbackRepository.findByHospital_Id(hospitalId);
    }

    public void createFeedback(FeedbackCreateUpdateDto feedback) {
        if (feedback.getPatient_id() != null && feedback.getDoctor_id() != null) {
            if (!this.patientCanRateDoctor(feedback.getDoctor_id(), feedback.getPatient_id())) {
                throw new FeedbackNotPermitted("You cannot rate this doctor as you do not have history with them");
            }

            // Check if feedback already exists and update it instead
            var existingFeedback = feedbackRepository.findByDoctor_IdAndPatient_Id(
                    feedback.getDoctor_id(),
                    feedback.getPatient_id());

            if (existingFeedback.isPresent()) {
                Feedback feedbackEntity = existingFeedback.get();
                feedbackEntity.setScore(feedback.getScore());
                feedbackEntity.setComment(feedback.getComment());
                feedbackEntity.setDate(feedback.getDate());
                feedbackRepository.save(feedbackEntity);
                return;
            }
        }

        if (feedback.getHospital_id() != null) {
            List<Appointment> result = appointmentRepository
                    .findByReferral_Patient_IdAndHospital_Id(
                            feedback.getPatient_id(),
                            feedback.getHospital_id());

            if (result.isEmpty())
                throw new FeedbackNotPermitted("You cannot rate this hospital as you do not have history with it.");

            // Check if feedback already exists and update it instead
            var existingFeedback = feedbackRepository.findByHospital_IdAndPatient_Id(
                    feedback.getHospital_id(),
                    feedback.getPatient_id());

            if (existingFeedback.isPresent()) {
                Feedback feedbackEntity = existingFeedback.get();
                feedbackEntity.setScore(feedback.getScore());
                feedbackEntity.setComment(feedback.getComment());
                feedbackEntity.setDate(feedback.getDate());
                feedbackRepository.save(feedbackEntity);
                return;
            }
        }

        Feedback feedbackEntity = FeedbackCreateUpdateDto.toEntity(feedback);
        feedbackRepository.save(feedbackEntity);
    }

    public Optional<Feedback> getPatientFeedbackForDoctor(UUID doctorId, UUID patientId) {
        return feedbackRepository.findByDoctor_IdAndPatient_Id(doctorId, patientId);
    }

    public boolean patientCanRateDoctor(UUID doctorId, UUID patientId) {
        List<Appointment> appointments = appointmentRepository
                .findByReferral_Patient_IdAndDoctor_Id(patientId, doctorId);

        LocalDateTime now = LocalDateTime.now();

        return appointments.stream()
                .anyMatch(a -> a.getStatus() == AppointmentStatus.FINISHED &&
                        a.getTime().isBefore(LocalTime.from(now)));
    }

    public List<Feedback> getDoctorFeedbacks(UUID doctorId) {
        return feedbackRepository.findByDoctor_Id(doctorId);
    }

    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }
}
