package kma.health.app.kma_health.service;

import kma.health.app.kma_health.dto.AppointmentFullViewDto;
import kma.health.app.kma_health.dto.AppointmentShortViewDto;
import kma.health.app.kma_health.entity.Appointment;
import kma.health.app.kma_health.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<AppointmentShortViewDto> getAppointments(String patientPassportNumber) {

        List<Appointment> queryRes = appointmentRepository.findAllByReferralPatientPassportNumber(patientPassportNumber);
        List<AppointmentShortViewDto> res = new ArrayList<>();
        for(Appointment app : queryRes)
        {
            res.add(new AppointmentShortViewDto(app));
        }
        return res;
    }

    public AppointmentFullViewDto getFullAppointment(UUID id)
    {
        Appointment appointment = appointmentRepository.getReferenceById(id);
        return new AppointmentFullViewDto(appointment);
    }


}
