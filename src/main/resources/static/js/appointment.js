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

            fetch('/api/appointments/finish', {
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

            // Create custom confirmation modal
            const modal = document.createElement('div');
            modal.style.cssText = `
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0, 0, 0, 0.5);
                display: flex;
                justify-content: center;
                align-items: center;
                z-index: 10000;
                backdrop-filter: blur(4px);
            `;

            const modalContent = document.createElement('div');
            modalContent.style.cssText = `
                background: white;
                padding: 30px;
                border-radius: 12px;
                box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
                max-width: 400px;
                width: 90%;
                text-align: center;
                animation: slideIn 0.3s ease-out;
            `;

            modalContent.innerHTML = `
                <style>
                    @keyframes slideIn {
                        from {
                            transform: translateY(-50px);
                            opacity: 0;
                        }
                        to {
                            transform: translateY(0);
                            opacity: 1;
                        }
                    }
                    .cancel-modal-icon {
                        font-size: 48px;
                        margin-bottom: 20px;
                    }
                    .cancel-modal-title {
                        font-size: 24px;
                        font-weight: 600;
                        margin-bottom: 10px;
                        color: #333;
                    }
                    .cancel-modal-text {
                        font-size: 16px;
                        color: #666;
                        margin-bottom: 25px;
                        line-height: 1.5;
                    }
                    .cancel-modal-buttons {
                        display: flex;
                        gap: 12px;
                        justify-content: center;
                    }
                    .cancel-modal-btn {
                        padding: 12px 30px;
                        border: none;
                        border-radius: 8px;
                        font-size: 16px;
                        font-weight: 500;
                        cursor: pointer;
                        transition: all 0.2s;
                    }
                    .cancel-modal-btn-confirm {
                        background: #dc3545;
                        color: white;
                    }
                    .cancel-modal-btn-confirm:hover {
                        background: #c82333;
                        transform: translateY(-2px);
                        box-shadow: 0 4px 12px rgba(220, 53, 69, 0.3);
                    }
                    .cancel-modal-btn-cancel {
                        background: #6c757d;
                        color: white;
                    }
                    .cancel-modal-btn-cancel:hover {
                        background: #5a6268;
                        transform: translateY(-2px);
                        box-shadow: 0 4px 12px rgba(108, 117, 125, 0.3);
                    }
                </style>
                <div class="cancel-modal-title">Скасувати запис?</div>
                <div class="cancel-modal-text">Ви впевнені, що хочете скасувати цей запис? Цю дію не можна буде відмінити.</div>
                <div class="cancel-modal-buttons">
                    <button class="cancel-modal-btn cancel-modal-btn-cancel" id="modalCancelBtn">Ні, залишити</button>
                    <button class="cancel-modal-btn cancel-modal-btn-confirm" id="modalConfirmBtn">Так, скасувати</button>
                </div>
            `;

            modal.appendChild(modalContent);
            document.body.appendChild(modal);

            // Handle modal buttons
            document.getElementById('modalCancelBtn').addEventListener('click', () => {
                modal.remove();
            });

            document.getElementById('modalConfirmBtn').addEventListener('click', () => {
                modal.remove();
                
                // Show loading state
                const loadingModal = document.createElement('div');
                loadingModal.style.cssText = modal.style.cssText;
                loadingModal.innerHTML = `
                    <div style="${modalContent.style.cssText}">
                        <div style="font-size: 48px; margin-bottom: 20px;">⏳</div>
                        <div style="font-size: 20px; font-weight: 600; color: #333;">Скасування запису...</div>
                    </div>
                `;
                document.body.appendChild(loadingModal);

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
                        loadingModal.remove();
                        
                        if (response.ok) {
                            // Show success modal
                            const successModal = document.createElement('div');
                            successModal.style.cssText = modal.style.cssText;
                            successModal.innerHTML = `
                                <div style="${modalContent.style.cssText}">
                                    <div style="font-size: 24px; font-weight: 600; margin-bottom: 10px; color: #28a745;">Успішно!</div>
                                    <div style="font-size: 16px; color: #666; margin-bottom: 20px;">Запис успішно скасовано</div>
                                </div>
                            `;
                            document.body.appendChild(successModal);
                            
                            setTimeout(() => {
                                window.location.href = '/ui/profile';
                            }, 1500);
                        } else {
                            response.json().then(data => {
                                // Show error modal
                                const errorModal = document.createElement('div');
                                errorModal.style.cssText = modal.style.cssText;
                                errorModal.innerHTML = `
                                    <div style="${modalContent.style.cssText}">
                                        <div style="font-size: 24px; font-weight: 600; margin-bottom: 10px; color: #dc3545;">Помилка</div>
                                        <div style="font-size: 16px; color: #666; margin-bottom: 25px;">${data.message || 'Невідома помилка'}</div>
                                        <button class="cancel-modal-btn cancel-modal-btn-cancel" onclick="this.closest('div').parentElement.remove()">Закрити</button>
                                    </div>
                                `;
                                document.body.appendChild(errorModal);
                            }).catch(() => {
                                alert('Помилка сервера');
                            });
                        }
                    })
                    .catch(error => {
                        loadingModal.remove();
                        console.error('Fetch error:', error);
                        
                        // Show error modal
                        const errorModal = document.createElement('div');
                        errorModal.style.cssText = modal.style.cssText;
                        errorModal.innerHTML = `
                            <div style="${modalContent.style.cssText}">
                                <div style="font-size: 48px; margin-bottom: 20px;">❌</div>
                                <div style="font-size: 24px; font-weight: 600; margin-bottom: 10px; color: #dc3545;">Помилка мережі</div>
                                <div style="font-size: 16px; color: #666; margin-bottom: 25px;">Не вдалося підключитися до сервера</div>
                                <button class="cancel-modal-btn cancel-modal-btn-cancel" onclick="this.closest('div').parentElement.remove()">Закрити</button>
                            </div>
                        `;
                        document.body.appendChild(errorModal);
                    });
            });

            // Close modal on outside click
            modal.addEventListener('click', (e) => {
                if (e.target === modal) {
                    modal.remove();
                }
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
        patientCard.addEventListener('click', async function() {
            const patientId = patientCard.getAttribute('data-patient-id');
            const patientRole = patientCard.getAttribute('data-patient-role');
            const userRole = /*[[${userRole}]]*/ 'DOCTOR';

            if (userRole === 'DOCTOR') {
                console.log(userRole)
                const url = `/ui/profile/view?profileId=${patientId}&profileRole=${patientRole}`;
                window.location.href = url;
            }
        });
    }
});