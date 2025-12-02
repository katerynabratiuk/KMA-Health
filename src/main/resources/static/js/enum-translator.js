// Enum translation utility
const EnumTranslator = {
    appointmentStatus: {
        'SCHEDULED': 'Заплановано',
        'OPEN': 'Відкрито',
        'FINISHED': 'Завершено',
        'MISSED': 'Пропущено'
    },

    userRole: {
        'DOCTOR': 'Лікар',
        'PATIENT': 'Пацієнт',
        'LAB_ASSISTANT': 'Лаборант'
    },

    hospitalType: {
        'PUBLIC': 'Державна',
        'PRIVATE': 'Приватна'
    },

    feedbackTargetType: {
        'HOSPITAL': 'Лікарня',
        'DOCTOR': 'Лікар'
    },

    translate(enumType, value) {
        if (!value) return '';
        
        const map = this[enumType];
        return map && map[value] ? map[value] : value;
    },

    translateAppointmentStatus(value) {
        return this.translate('appointmentStatus', value);
    },

    translateUserRole(value) {
        return this.translate('userRole', value);
    },

    translateHospitalType(value) {
        return this.translate('hospitalType', value);
    },

    translateFeedbackTargetType(value) {
        return this.translate('feedbackTargetType', value);
    }
};
