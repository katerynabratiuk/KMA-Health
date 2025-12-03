document.addEventListener('DOMContentLoaded', function() {
    const toggleBtn = document.getElementById('toggle-form-btn');
    const formContainer = document.getElementById('referral-form');
    const form = document.getElementById('create-referral-form');
    const submitBtn = document.getElementById('submit-referral-btn');

    if (toggleBtn) {
        toggleBtn.addEventListener('click', function() {
            formContainer.classList.toggle('hidden');
            toggleBtn.textContent = formContainer.classList.contains('hidden') ? 'Створити нове направлення' : 'Згорнути форму';
        });
    }

    if (form) {
        form.addEventListener('submit', function() {
            const patientId = document.getElementById('targetPatient').value;
            const targetType = document.getElementById('targetType').value;
            const targetDetails = document.getElementById('targetDetails').value;

            const payload = {
                patientId: patientId,
                doctorTypeName: targetType === 'doctor' ? targetDetails : null,
                examinationName: targetType === 'examination' ? targetDetails : null
            };

            fetch('/api/referral', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: "include",
                body: JSON.stringify(payload)
            })
                .then(response => {
                    if (response.status === 201) {
                        location.reload();
                    } else if (response.status === 404) {
                        alert('Лікаря або пацієнта не знайдено.');
                    } else {
                        alert('Помилка при створенні направлення.');
                    }
                })
                .catch(err => {
                    console.error(err);
                    alert('Помилка при підключенні до сервера.');
                });
        });
    }
});
