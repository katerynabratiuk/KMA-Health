(function() {
    const profileGrid = document.querySelector('.profile-info-grid');

    if (profileGrid) {
        profileGrid.addEventListener('click', (event) => {
            const target = event.target;

            if (target.classList.contains('edit-icon')) {
                const itemDiv = target.closest('.info-item');
                const valueSpan = itemDiv.querySelector('.value');
                const inputField = itemDiv.querySelector('.edit-input');
                const saveButton = itemDiv.querySelector('.save-icon');
                const editButton = target;

                inputField.value = valueSpan.textContent.trim();

                valueSpan.classList.add('hidden');
                inputField.classList.remove('hidden');
                editButton.classList.add('hidden');
                saveButton.classList.remove('hidden');

                inputField.focus();

            } else if (target.classList.contains('save-icon')) {
                const itemDiv = target.closest('.info-item');
                const inputField = itemDiv.querySelector('.edit-input');

                handleSave(itemDiv, inputField);
            }
        });
    }

    function handleSave(itemDiv, inputField) {
        const fieldName = itemDiv.dataset.fieldName;
        const newValue = inputField.value.trim();

        if (!newValue) {
            alert(`Поле ${fieldName} не може бути порожнім!`);
            return;
        }

        if (fieldName === 'email' && !isValidEmail(newValue)) {
            alert('Будь ласка, введіть коректний Email.');
            return;
        }

        const valueSpan = itemDiv.querySelector('.value');
        const editIcon = itemDiv.querySelector('.edit-icon');
        const saveButton = itemDiv.querySelector('.save-icon');

        valueSpan.textContent = newValue;

        valueSpan.classList.remove('hidden');
        inputField.classList.add('hidden');
        editIcon.classList.remove('hidden');
        saveButton.classList.add('hidden');

        console.log(`Successfully updated ${fieldName}: ${newValue}`);
    }

    function isValidEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(String(email).toLowerCase());
    }

    const reminderButtons = document.querySelectorAll('.reminder-button');
    const reminderModal = document.getElementById('reminder-modal');
    const closeModalBtn = document.getElementById('close-modal');
    const reminderForm = reminderModal ? reminderModal.querySelector('form') : null;
    let currentAppointmentId = null;

    if ('Notification' in window && Notification.permission !== 'granted') {
        Notification.requestPermission();
    }

    reminderButtons.forEach(button => {
        button.addEventListener('click', () => {
            currentAppointmentId = button.dataset.appointmentId;
            if (reminderModal) {
                reminderModal.classList.remove('hidden');
            }
        });
    });

    if (closeModalBtn) {
        closeModalBtn.addEventListener('click', () => {
            if (reminderModal) {
                reminderModal.classList.add('hidden');
            }
        });
    }

    if (reminderForm) {
        reminderForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const timeValue = document.getElementById('reminderTime').value;

            if (Notification.permission === 'granted') {
                new Notification(`Нагадування встановлено!`, {
                    body: `Нагадування для запису #${currentAppointmentId} надійде за ${timeValue} хв до початку.`,
                    icon: '/images/bell-icon.png'
                });
            }

            reminderModal.classList.add('hidden');
            alert(`Для запису ${currentAppointmentId} встановлено нагадування за ${timeValue} хвилин.`);
        });
    }
})();