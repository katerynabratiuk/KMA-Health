import { AppointmentForm } from './appointment-form.js';

document.addEventListener('DOMContentLoaded', function() {
    console.log('Doctor detail page loaded');
    const doctorId = document.getElementById('doctorId')?.value;
    console.log('Doctor ID:', doctorId);
    
    if (doctorId) {
        window.appointmentFormInstance = new AppointmentForm(doctorId);
        console.log('AppointmentForm initialized');
    } else {
        console.error('Doctor ID not found');
    }

    const appointmentForm = document.getElementById("appointmentForm");
    const appointmentMessage = document.getElementById("appointmentMessage");

    appointmentForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        clearErrors();

        const doctorId = document.getElementById("doctorId").value;
        const appointmentDate = document.getElementById("appointmentDate").value;
        const selectedTime = document.getElementById("selectedTimeSlot").value;
        const referralSelect = document.getElementById("referralSelect");
        const referralId = referralSelect ? referralSelect.value : null;

        let hasError = false;
        if (!appointmentDate) {
            showError("dateError", "Будь ласка, оберіть дату");
            hasError = true;
        }
        if (!selectedTime) {
            showError("timeError", "Будь ласка, оберіть час");
            hasError = true;
        }

        if (hasError) return;

        const payload = {
            doctorId: doctorId,
            date: appointmentDate,
            time: selectedTime,
            diagnosis: null,
            labAssistantId: null,
            referralId: isFamilyDoctor() ? null : referralId
        };

        try {
            const response = await fetch("/api/appointments", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                credentials: "include",
                body: JSON.stringify(payload)
            });

            if (response.status === 201) {
                appointmentMessage.style.display = "block";
                appointmentMessage.textContent = "Запис успішно створено!";
                appointmentMessage.className = "appointment-message success";
                appointmentForm.reset();
            } else if (response.status === 403) {
                appointmentMessage.style.display = "block";
                appointmentMessage.textContent = "Ви не маєте прав для створення запису";
                appointmentMessage.className = "appointment-message error";
            } else {
                const errorText = await response.text();
                appointmentMessage.style.display = "block";
                appointmentMessage.textContent = `Помилка: ${errorText}`;
                appointmentMessage.className = "appointment-message error";
            }
        } catch (err) {
            console.error(err);
            appointmentMessage.style.display = "block";
            appointmentMessage.textContent = "Сталася помилка при створенні запису";
            appointmentMessage.className = "appointment-message error";
        }
    });

    function showError(id, message) {
        const el = document.getElementById(id);
        if (el) {
            el.textContent = message;
        }
    }

    function clearErrors() {
        ["dateError", "timeError", "referralError"].forEach(id => {
            const el = document.getElementById(id);
            if (el) el.textContent = "";
        });
    }

    function isFamilyDoctor() {
        const doctorTypeEl = document.querySelector(".doctor-specialty-badge");
        return doctorTypeEl && doctorTypeEl.textContent.trim() === "Family Doctor";
    }
});
