import { AppointmentForm } from './appointment-form.js';

document.addEventListener('DOMContentLoaded', function() {
    console.log('Doctor detail page loaded');
    const doctorId = document.getElementById('doctorId')?.value;
    console.log('Doctor ID:', doctorId);
    
    if (doctorId) {
        // Initialize appointment form
        window.appointmentFormInstance = new AppointmentForm(doctorId);
        console.log('AppointmentForm initialized');
    } else {
        console.error('Doctor ID not found');
    }
});
