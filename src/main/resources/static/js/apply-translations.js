// Apply enum translations on page load
document.addEventListener('DOMContentLoaded', function() {
    // Translate all appointment statuses
    document.querySelectorAll('[data-status]').forEach(element => {
        const status = element.getAttribute('data-status');
        if (status) {
            element.textContent = EnumTranslator.translateAppointmentStatus(status);
        }
    });

    // Translate all user roles
    document.querySelectorAll('[data-role]').forEach(element => {
        const role = element.getAttribute('data-role');
        if (role) {
            element.textContent = EnumTranslator.translateUserRole(role);
        }
    });

    // Translate all hospital types
    document.querySelectorAll('[data-hospital-type]').forEach(element => {
        const type = element.getAttribute('data-hospital-type');
        if (type) {
            element.textContent = EnumTranslator.translateHospitalType(type);
        }
    });

    // Translate all feedback target types
    document.querySelectorAll('[data-feedback-target]').forEach(element => {
        const type = element.getAttribute('data-feedback-target');
        if (type) {
            element.textContent = EnumTranslator.translateFeedbackTargetType(type);
        }
    });
});
