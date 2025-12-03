document.addEventListener('DOMContentLoaded', () => {
    const appointmentPageContent = document.querySelector('.appointment-page-content');
    if (!appointmentPageContent) {
        console.error("Appointment page content not found. Script stopped.");
        return;
    }

    const appointmentIdElement = document.querySelector('.meta span');
    const appointmentId = appointmentIdElement ? appointmentIdElement.textContent : null;
    const doctorId = appointmentPageContent.dataset.doctorId || null;
    const patientId = appointmentPageContent.dataset.patientId || null;
    const userRole = appointmentPageContent.dataset.userRole || null;
    
    console.log('Appointment Data:', { appointmentId, doctorId, patientId, userRole });

    const dropzone = document.getElementById('dropzone');
    const fileInput = document.getElementById('fileInput');
    const filesList = document.getElementById('filesList');
    const diagnosisTextarea = document.getElementById('diagnosis');
    const finishBtn = document.getElementById('finishBtn');
    const cancelBtn = document.getElementById('cancelBtn');

    let uploadedFiles = [];

    const patientCard = document.querySelector('.patient-card');

    function handleFiles(files) {
        const newFiles = Array.from(files);

        newFiles.forEach(file => {
            if (!uploadedFiles.some(existingFile => existingFile.name === file.name)) {
                uploadedFiles.push(file);
            }
        });

        updateFilesListDisplay();
    }

    function removeFile(fileName) {
        uploadedFiles = uploadedFiles.filter(file => file.name !== fileName);
        updateFilesListDisplay();
    }

    function updateFilesListDisplay() {
        const existingFilesHtml = filesList.querySelectorAll('.file-item:not(.new-file)');
        const existingFilesList = Array.from(existingFilesHtml).map(item => item.outerHTML).join('');

        filesList.innerHTML = existingFilesList;

        uploadedFiles.forEach(file => {
            const fileItem = document.createElement('div');
            fileItem.className = 'file-item new-file';
            fileItem.setAttribute('data-file-name', file.name);

            const fileNameDiv = document.createElement('div');
            fileNameDiv.textContent = file.name;

            const removeBtn = document.createElement('button');
            removeBtn.className = 'remove-file-btn meta';
            removeBtn.textContent = 'Видалити';
            removeBtn.title = `Видалити файл ${file.name}`;
            removeBtn.onclick = (e) => {
                e.preventDefault();
                removeFile(file.name);
            };

            fileItem.appendChild(fileNameDiv);
            fileItem.appendChild(removeBtn);
            filesList.appendChild(fileItem);
        });
    }

    function preventDefaults(e) {
        e.preventDefault();
        e.stopPropagation();
    }

    function highlight() {
        if (dropzone) dropzone.classList.add('highlight');
    }

    function unhighlight() {
        if (dropzone) dropzone.classList.remove('highlight');
    }

    function handleDrop(e) {
        unhighlight();
        const dt = e.dataTransfer;
        const files = dt.files;
        handleFiles(files);
    }

    if (dropzone) {
        ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
            dropzone.addEventListener(eventName, preventDefaults, false);
        });

        ['dragenter', 'dragover'].forEach(eventName => {
            dropzone.addEventListener(eventName, highlight, false);
        });
        ['dragleave', 'drop'].forEach(eventName => {
            dropzone.addEventListener(eventName, unhighlight, false);
        });

        dropzone.addEventListener('drop', handleDrop, false);

        dropzone.addEventListener('click', () => {
            fileInput.click();
        });
    }

    if (fileInput) {
        fileInput.addEventListener('change', (e) => {
            handleFiles(e.target.files);
            e.target.value = '';
        });
    }

    if (finishBtn && appointmentId) {
        finishBtn.addEventListener('click', (e) => {
            e.preventDefault();

            const diagnosis = diagnosisTextarea ? diagnosisTextarea.value : '';

            const formData = new FormData();

            formData.append('appointmentId', appointmentId);
            formData.append('diagnosis', diagnosis);

            uploadedFiles.forEach(file => {
                formData.append('medicalFiles', file, file.name);
            });

            fetch('/api/appointments/cancel', {
                method: 'POST',
                credentials: "include",
                body: formData
            })
                .then(response => {
                    if (response.ok) {
                        alert('Запис успішно завершено!');
                        window.location.reload();
                    } else {
                        const contentType = response.headers.get("content-type");

                        if (contentType && contentType.includes("application/json") && response.status !== 204) {
                            response.json().then(data => {
                                alert(`Помилка при завершенні запису: ${data.message || 'Невідома помилка'}`);
                            }).catch(() => {
                                alert('Помилка: Сервер повернув невалідне JSON-тіло.');
                            });
                        } else {
                            alert(`Помилка: Сервер повернув статус ${response.status} без деталей.`);
                        }
                    }
                })
                .catch(error => {
                    console.error('Fetch error:', error);
                    alert('Помилка мережі або сервера.');
                });
        });
    }

    if (cancelBtn && appointmentId) {
        cancelBtn.addEventListener('click', (e) => {
            e.preventDefault();

            if (!confirm('Ви впевнені, що хочете скасувати цей запис?')) {
                return;
            }

            let cancelUrl = `/api/appointments/cancel?appointmentId=${appointmentId}`;
            if (userRole === 'PATIENT') {
                cancelUrl += `&patientId=${patientId}`;
            } else if (userRole === 'DOCTOR' || userRole === 'LAB_ASSISTANT') {
                cancelUrl += `&doctorId=${doctorId}`;
            }
            
            fetch(cancelUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    credentials: "include"
                },
                credentials: "include"
            })
                .then(response => {
                    if (response.ok) {
                        alert('✅ Запис успішно скасовано!');
                        window.location.reload();
                    } else {
                        response.json().then(data => {
                            alert(`Помилка при скасуванні запису: ${data.message || 'Невідома помилка'}`);
                        });
                    }
                })
                .catch(error => {
                    console.error('Fetch error:', error);
                    alert('Помилка мережі або сервера.');
                });
        });
    }

    if (typeof globalTranslateEnums === 'function' && typeof applyTranslations === 'function') {
        globalTranslateEnums();
        applyTranslations();
    } else {
        console.warn("Translation functions not found. Check if enum-translator.js and apply-translations.js are loaded.");
    }

    if (dropzone) {
        updateFilesListDisplay();
    }

    if (patientCard) {
        patientCard.addEventListener('click', function() {
            const patientId = patientCard.getAttribute('data-patient-id');
            const patientRole = patientCard.getAttribute('data-patient-role');
            const userRole = /*[[${userRole}]]*/ 'DOCTOR';
            if (userRole === 'DOCTOR') {
                const url = `/ui/profile/view/?profileId=${patientId}&profileRole=${patientRole}`;
                window.location.href = url;
            }
        });
    }
});