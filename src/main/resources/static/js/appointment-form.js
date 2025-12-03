export class AppointmentForm {
    constructor(doctorId) {
        this.doctorId = doctorId;
        this.currentDate = null;
        this.allSlots = [];
        
        this.initializeElements();
        this.setupEventListeners();
        this.setDefaultDate();
    }
    
    initializeElements() {
        this.form = document.getElementById('appointmentForm');
        this.dateInput = document.getElementById('appointmentDate');
        this.timeSlotsContainer = document.getElementById('timeSlotsContainer');
        this.selectedTimeSlotInput = document.getElementById('selectedTimeSlot');
        this.messageDiv = document.getElementById('appointmentMessage');
    }
    
    setupEventListeners() {
        // Date input
        this.dateInput.addEventListener('change', () => this.handleDateChange());
        
        // Form submission
        this.form.addEventListener('submit', (e) => this.handleSubmit(e));
    }
    
    setDefaultDate() {
        const today = new Date().toISOString().split('T')[0];
        this.dateInput.setAttribute('min', today);
        this.dateInput.value = today;
        this.currentDate = today;
        this.loadTimeSlots(today);
    }
    
    async handleDateChange() {
        const selectedDate = this.dateInput.value;
        if (!selectedDate) return;
        
        this.currentDate = selectedDate;
        await this.loadTimeSlots(selectedDate);
    }
    
    async loadTimeSlots(date) {
        this.showLoadingMessage();
        
        try {
            const response = await fetch(`/api/appointments/doctor/${this.doctorId}/slots?date=${date}`, {
                method: 'GET',
                credentials: 'include'
            });
            
            if (response.ok) {
                const busyAppointments = await response.json();
                // Transform busy appointments into full schedule with availability
                const slots = this.generateSlotsFromAppointments(date, busyAppointments);
                this.allSlots = slots;
                this.renderTimeSlots(slots);
            } else {
                console.error(response);
            }
        } catch (error) {
            console.error('Error loading time slots:', error);
        }
    }
    
    generateSlotsFromAppointments(date, busyAppointments) {
        const slots = [];
        const selectedDate = new Date(date + 'T00:00:00');
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        
        // Don't generate slots for past dates
        if (selectedDate < today) {
            return slots;
        }
        
        // Create a set of busy times for quick lookup
        const busyTimes = new Set();
        busyAppointments.forEach(apt => {
            if (apt.time) {
                // Store time in HH:MM format
                const timeStr = apt.time.length > 5 ? apt.time.substring(0, 5) : apt.time;
                busyTimes.add(timeStr);
            }
        });
        
        const now = new Date();
        const isToday = selectedDate.getTime() === today.getTime();
        
        // Generate 20-minute slots from 9:00 to 17:00, skipping 13:00-14:00
        for (let hour = 9; hour < 17; hour++) {
            // Skip lunch break from 13:00 to 14:00
            if (hour === 13) continue;
            
            for (let minute = 0; minute < 60; minute += 20) {
                const time = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
                
                // If it's today, skip past times entirely
                if (isToday) {
                    const slotDateTime = new Date();
                    slotDateTime.setHours(hour, minute, 0, 0);
                    
                    if (slotDateTime <= now) {
                        continue; // Skip past times
                    }
                }
                
                // Check if this time is busy
                const isBusy = busyTimes.has(time);
                
                slots.push({
                    time: time,
                    duration: '20 хв',
                    available: !isBusy
                });
            }
        }
        
        return slots;
    }
    
    showLoadingMessage() {
        this.timeSlotsContainer.innerHTML = `
            <div class="loading-message">
                <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <polyline points="12 6 12 12 16 14"></polyline>
                </svg>
                <p>Завантаження доступних часів...</p>
            </div>
        `;
    }

    renderTimeSlots(slots) {
        // Якщо слотів немає – показуємо повідомлення
        if (!slots || slots.length === 0) {
            this.timeSlotsContainer.innerHTML = `
            <div class="no-slots-message">
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="8" x2="12" y2="12"></line>
                    <line x1="12" y1="16" x2="12.01" y2="16"></line>
                </svg>
                <p>Немає доступних слотів на цю дату</p>
            </div>`;
            return;
        }

        // Якщо слоти є – очищаємо контейнер і рендеримо грід
        this.timeSlotsContainer.innerHTML = '';

        const slotsGrid = document.createElement('div');
        slotsGrid.className = 'time-slots-grid';

        slots.forEach(slot => {
            slotsGrid.appendChild(this.createSlotButton(slot));
        });

        this.timeSlotsContainer.appendChild(slotsGrid);
    }
    
    createSlotButton(slot) {
        const slotButton = document.createElement('button');
        slotButton.type = 'button';
        slotButton.className = 'time-slot';
        slotButton.innerHTML = `
            <span class="time-slot-time">${slot.time}</span>
        `;
        
        if (!slot.available) {
            slotButton.classList.add('unavailable');
            slotButton.disabled = true;
            slotButton.title = 'Цей час вже зайнятий';
        } else {
            slotButton.title = `Натисніть, щоб обрати цей час`;
            slotButton.addEventListener('click', () => this.selectSlot(slotButton, slot));
        }
        
        return slotButton;
    }
    
    selectSlot(button, slot) {
        document.querySelectorAll('.time-slot').forEach(btn => {
            btn.classList.remove('selected');
        });
        
        button.classList.add('selected');
        this.selectedTimeSlotInput.value = slot.time;
        
        document.getElementById('timeError').textContent = '';
        
        const selectedInfo = document.createElement('div');
        selectedInfo.className = 'selected-time-info';
        selectedInfo.innerHTML = `
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="20 6 9 17 4 12"></polyline>
            </svg>
            Обрано: ${slot.time} (${slot.duration})
        `;
        
        const existingInfo = document.querySelector('.selected-time-info');
        if (existingInfo) {
            existingInfo.remove();
        }
        
        this.timeSlotsContainer.parentNode.insertBefore(selectedInfo, document.getElementById('timeError'));
    }
    
    async handleSubmit(e) {
        e.preventDefault();
        console.log('Form submitted');
        
        this.messageDiv.style.display = 'none';
        document.querySelectorAll('.error-message').forEach(el => el.textContent = '');
        
        if (!this.validateForm()) {
            console.log('Form validation failed');
            return;
        }
        
        console.log('Form validation passed');
        
        const appointmentData = {
            doctorId: this.doctorId,
            date: this.dateInput.value,
            time: this.selectedTimeSlotInput.value
        };
        
        console.log('Sending appointment data:', appointmentData);
        
        try {
            const response = await fetch(`/api/appointments`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(appointmentData),
                credentials: 'include'
            });
            
            if (response.ok) {
                this.showMessage('Ваш запис успішно створено! Очікуйте підтвердження.', 'success');
                this.resetForm();
            } else {
                const errorData = await response.json();
                this.showMessage(errorData.message || 'Помилка при створенні запису. Спробуйте пізніше.', 'error');
            }
        } catch (error) {
            console.error('Appointment creation error:', error);
            this.showMessage('Помилка з\'єднання. Спробуйте пізніше.', 'error');
        }
    }
    
    validateForm() {
        let isValid = true;
        
        const selectedDate = new Date(this.dateInput.value);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        
        if (!this.dateInput.value) {
            this.showError('dateError', 'Оберіть дату приймання');
            isValid = false;
        } else if (selectedDate < today) {
            this.showError('dateError', 'Дата не може бути в минулому');
            isValid = false;
        }
        if (!this.selectedTimeSlotInput.value) {
            this.showError('timeError', 'Оберіть часовий слот');
            isValid = false;
        }
        
        return isValid;
    }
    
    showError(elementId, message) {
        const errorElement = document.getElementById(elementId);
        if (errorElement) {
            errorElement.textContent = message;
        }
    }
    
    showMessage(message, type) {
        this.messageDiv.textContent = message;
        this.messageDiv.className = `appointment-message ${type}`;
        this.messageDiv.style.display = 'block';
        
        if (type === 'success') {
            setTimeout(() => {
                this.messageDiv.style.display = 'none';
            }, 5000);
        }
    }
    
    resetForm() {
        this.form.reset();
        
        const today = new Date().toISOString().split('T')[0];
        this.dateInput.setAttribute('min', today);
        
        this.timeSlotsContainer.innerHTML = `
            <div class="no-slots-message">
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"></circle>
                    <polyline points="12 6 12 12 16 14"></polyline>
                </svg>
                <p>Спочатку оберіть дату приймання</p>
            </div>
        `;
        
        this.selectedTimeSlotInput.value = '';
        
        const selectedInfo = document.querySelector('.selected-time-info');
        if (selectedInfo) {
            selectedInfo.remove();
        }
        
        document.querySelectorAll('.error-message').forEach(el => el.textContent = '');
        this.messageDiv.style.display = 'none';
    }
}

// Global helper functions
// Only override if not already defined (for cases when appointment form doesn't exist)
if (!window.scrollToAppointment) {
    window.scrollToAppointment = function() {
        const appointmentSection = document.getElementById('appointment-section');
        if (appointmentSection) {
            appointmentSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    }
}

window.resetForm = function() {
    const form = document.getElementById('appointmentForm');
    if (form && window.appointmentFormInstance) {
        window.appointmentFormInstance.resetForm();
    }
}
