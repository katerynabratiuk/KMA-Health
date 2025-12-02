/**
 * Module for handling doctor feedback form functionality
 */

export function initializeFeedbackForm(doctorId) {
    console.log('Initializing feedback form for doctor:', doctorId);
    
    // Scroll to feedback button handler
    const scrollToFeedbackBtn = document.getElementById('scrollToFeedback');
    if (scrollToFeedbackBtn) {
        scrollToFeedbackBtn.addEventListener('click', async function() {
            const feedbackSection = document.getElementById('feedback-section');
            if (feedbackSection) {
                feedbackSection.style.display = 'block';
                feedbackSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
                
                // Get doctor ID from data attribute on the section
                const doctorIdFromSection = feedbackSection.getAttribute('data-doctor-id');
                console.log('Doctor ID from section:', doctorIdFromSection);
                
                // Fallback to hidden input
                const doctorIdFromInput = document.getElementById('feedbackDoctorId')?.value;
                console.log('Doctor ID from input:', doctorIdFromInput);
                
                const actualDoctorId = doctorIdFromSection || doctorIdFromInput;
                console.log('Using doctor ID:', actualDoctorId);
                
                // Load existing feedback when form is opened
                if (actualDoctorId) {
                    await loadExistingFeedback(actualDoctorId);
                } else {
                    console.error('Doctor ID not found in form or section!');
                }
            }
        });
    }
    
    const feedbackForm = document.getElementById('feedbackForm');
    if (!feedbackForm) {
        console.log('Feedback form not found on this page');
        return;
    }
    
    // Load existing feedback if any
    loadExistingFeedback(doctorId);
    
    // Star rating interaction
    const stars = document.querySelectorAll('.star-rating input[type="radio"]');
    const labels = document.querySelectorAll('.star-rating label');
    
    labels.forEach((label, index) => {
        label.addEventListener('mouseenter', function() {
            highlightStars(labels.length - index);
        });
    });
    
    document.querySelector('.star-rating').addEventListener('mouseleave', function() {
        const checked = document.querySelector('.star-rating input:checked');
        if (checked) {
            highlightStars(parseInt(checked.value));
        } else {
            highlightStars(0);
        }
    });
    
    stars.forEach(star => {
        star.addEventListener('change', function() {
            highlightStars(parseInt(this.value));
            document.getElementById('ratingError').textContent = '';
        });
    });
    
    // Character counter for comment
    const commentTextarea = document.getElementById('feedbackComment');
    const charCount = document.getElementById('charCount');
    
    if (commentTextarea) {
        commentTextarea.addEventListener('input', function() {
            const length = this.value.length;
            charCount.textContent = `${length}/500`;
        });
    }
    
    // Cancel button
    const cancelBtn = document.getElementById('cancelFeedback');
    if (cancelBtn) {
        cancelBtn.addEventListener('click', function() {
            resetFeedbackForm();
        });
    }
    
    // Delete button
    const deleteBtn = document.getElementById('deleteFeedback');
    if (deleteBtn) {
        deleteBtn.addEventListener('click', handleFeedbackDelete);
    }
    
    // Form submission
    feedbackForm.addEventListener('submit', handleFeedbackSubmit);
}

async function loadExistingFeedback(doctorId) {
    console.log('=== Loading existing feedback for doctor:', doctorId);
    try {
        const response = await fetch(`/api/doctor/${doctorId}/feedback/my`, {
            method: 'GET',
            credentials: 'include'
        });
        
        console.log('Feedback response status:', response.status);
        
        if (response.ok) {
            const feedback = await response.json();
            console.log('Found existing feedback:', feedback);
            
            // Store feedback ID
            const feedbackIdInput = document.getElementById('feedbackId');
            if (feedbackIdInput) {
                feedbackIdInput.value = feedback.id;
                console.log('Set feedback ID:', feedback.id);
            }
            
            // Set rating
            const ratingInput = document.getElementById(`star${feedback.score}`);
            if (ratingInput) {
                ratingInput.checked = true;
                highlightStars(feedback.score);
                console.log('Set rating:', feedback.score);
            }
            
            // Set comment
            const commentTextarea = document.getElementById('feedbackComment');
            if (commentTextarea && feedback.comment) {
                commentTextarea.value = feedback.comment;
                document.getElementById('charCount').textContent = `${feedback.comment.length}/500`;
                console.log('Set comment:', feedback.comment);
            }
            
            // Show warning message
            const warningDiv = document.getElementById('editWarning');
            if (warningDiv) {
                warningDiv.style.display = 'flex';
                console.log('Warning message shown');
            } else {
                console.error('Warning div not found!');
            }
            
            // Update button text to indicate editing
            const submitBtn = document.querySelector('#feedbackForm .btn-submit');
            if (submitBtn) {
                submitBtn.textContent = 'Оновити відгук';
                console.log('Button text updated to: Оновити відгук');
            }
            
            // Show delete button
            const deleteBtn = document.getElementById('deleteFeedback');
            if (deleteBtn) {
                deleteBtn.style.display = 'block';
                console.log('Delete button shown');
            } else {
                console.error('Delete button not found!');
            }
            
            console.log('=== Finished loading existing feedback');
        } else {
            console.log('No existing feedback (status:', response.status, ')');
        }
    } catch (error) {
        console.log('No existing feedback found or error loading:', error);
    }
}

function highlightStars(count) {
    const labels = document.querySelectorAll('.star-rating label');
    labels.forEach((label, index) => {
        if (labels.length - index <= count) {
            label.classList.add('active');
        } else {
            label.classList.remove('active');
        }
    });
}

function resetFeedbackForm() {
    const form = document.getElementById('feedbackForm');
    if (form) {
        form.reset();
        highlightStars(0);
        document.getElementById('charCount').textContent = '0/500';
        document.getElementById('ratingError').textContent = '';
        document.getElementById('feedbackMessage').style.display = 'none';
        document.getElementById('feedbackId').value = '';
        document.getElementById('editWarning').style.display = 'none';
        document.getElementById('deleteFeedback').style.display = 'none';
        
        const submitBtn = document.querySelector('#feedbackForm .btn-submit');
        if (submitBtn) {
            submitBtn.textContent = 'Надіслати відгук';
        }
    }
}

async function handleFeedbackSubmit(event) {
    event.preventDefault();
    
    const form = event.target;
    const doctorId = document.getElementById('feedbackDoctorId').value;
    const rating = document.querySelector('.star-rating input:checked');
    const comment = document.getElementById('feedbackComment').value.trim();
    const messageDiv = document.getElementById('feedbackMessage');
    const ratingError = document.getElementById('ratingError');
    
    // Validation
    if (!rating) {
        ratingError.textContent = 'Будь ласка, оберіть оцінку';
        return;
    }
    
    const feedbackData = {
        score: parseInt(rating.value),
        comment: comment || null,
        date: new Date().toISOString().split('T')[0]
    };
    
    console.log('Submitting feedback:', feedbackData);
    
    try {
        const response = await fetch(`/api/doctor/${doctorId}/feedback`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify(feedbackData)
        });
        
        console.log('Response status:', response.status);
        console.log('Response headers:', response.headers);
        
        if (response.ok) {
            const submitBtn = document.querySelector('#feedbackForm .btn-submit');
            const wasUpdate = submitBtn && submitBtn.textContent === 'Оновити відгук';
            
            messageDiv.textContent = wasUpdate 
                ? 'Відгук успішно оновлено!' 
                : 'Відгук успішно надіслано!';
            messageDiv.className = 'feedback-message success';
            messageDiv.style.display = 'block';
            
            // Reset form after 2 seconds and reload page to show new feedback
            setTimeout(() => {
                window.location.reload();
            }, 2000);
        } else {
            const errorText = await response.text();
            let errorMessage = 'Не вдалося надіслати відгук. ';
            
            if (response.status === 403) {
                errorMessage += 'Ви не маєте права залишати відгук для цього лікаря.';
            } else if (response.status === 400) {
                errorMessage += 'Перевірте правильність введених даних.';
            } else {
                errorMessage += errorText || 'Спробуйте пізніше.';
            }
            
            messageDiv.textContent = errorMessage;
            messageDiv.className = 'feedback-message error';
            messageDiv.style.display = 'block';
        }
    } catch (error) {
        console.error('Error submitting feedback:', error);
        messageDiv.textContent = 'Помилка з\'єднання. Перевірте інтернет-з\'єднання та спробуйте ще раз.';
        messageDiv.className = 'feedback-message error';
        messageDiv.style.display = 'block';
    }
}

async function handleFeedbackDelete() {
    const doctorId = document.getElementById('feedbackDoctorId').value;
    const feedbackId = document.getElementById('feedbackId').value;
    const messageDiv = document.getElementById('feedbackMessage');
    
    if (!feedbackId) {
        messageDiv.textContent = 'Помилка: ID відгуку не знайдено';
        messageDiv.className = 'feedback-message error';
        messageDiv.style.display = 'block';
        return;
    }
    
    // Show custom confirmation modal
    showDeleteConfirmModal(() => {
        // On confirm, proceed with deletion
        performDelete(doctorId, feedbackId, messageDiv);
    });
}

function showDeleteConfirmModal(onConfirm) {
    const modal = document.getElementById('deleteConfirmModal');
    const confirmBtn = document.getElementById('confirmDelete');
    const cancelBtn = document.getElementById('cancelDelete');
    
    modal.style.display = 'flex';
    
    const handleConfirm = () => {
        modal.style.display = 'none';
        confirmBtn.removeEventListener('click', handleConfirm);
        cancelBtn.removeEventListener('click', handleCancel);
        onConfirm();
    };
    
    const handleCancel = () => {
        modal.style.display = 'none';
        confirmBtn.removeEventListener('click', handleConfirm);
        cancelBtn.removeEventListener('click', handleCancel);
    };
    
    confirmBtn.addEventListener('click', handleConfirm);
    cancelBtn.addEventListener('click', handleCancel);
    
    // Close on overlay click
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            handleCancel();
        }
    });
}

async function performDelete(doctorId, feedbackId, messageDiv) {
    try {
        const response = await fetch(`/api/doctor/${doctorId}/feedback/${feedbackId}`, {
            method: 'DELETE',
            credentials: 'include'
        });
        
        if (response.ok) {
            messageDiv.textContent = 'Відгук успішно видалено!';
            messageDiv.className = 'feedback-message success';
            messageDiv.style.display = 'block';
            
            // Reload page after 1.5 seconds
            setTimeout(() => {
                window.location.reload();
            }, 1500);
        } else {
            let errorMessage = 'Не вдалося видалити відгук. ';
            
            if (response.status === 403) {
                errorMessage += 'Ви не маєте права видаляти цей відгук.';
            } else if (response.status === 404) {
                errorMessage += 'Відгук не знайдено.';
            } else {
                errorMessage += 'Спробуйте пізніше.';
            }
            
            messageDiv.textContent = errorMessage;
            messageDiv.className = 'feedback-message error';
            messageDiv.style.display = 'block';
        }
    } catch (error) {
        console.error('Error deleting feedback:', error);
        messageDiv.textContent = 'Помилка з\'єднання. Перевірте інтернет-з\'єднання та спробуйте ще раз.';
        messageDiv.className = 'feedback-message error';
        messageDiv.style.display = 'block';
    }
}
