// Doctor referral popup functionality
export function initializeReferralPopup(canGetAppointment) {
    console.log('Initializing referral popup with canGetAppointment:', canGetAppointment);
    
    window.scrollToAppointment = function() {
        console.log('scrollToAppointment called');
        console.log('canGetAppointment:', canGetAppointment);
        
        if (!canGetAppointment) {
            console.log('Showing referral required popup');
            showReferralRequiredPopup();
            return;
        }
        
        const appointmentSection = document.getElementById('appointment-section');
        if (appointmentSection) {
            appointmentSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    };
    
    function showReferralRequiredPopup() {
        console.log('showReferralRequiredPopup called');
        // Create overlay
        const overlay = document.createElement('div');
        overlay.style.cssText = 'position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; z-index: 9999;';
        
        // Create popup
        const popup = document.createElement('div');
        popup.style.cssText = 'background: white; padding: 24px; border-radius: 12px; max-width: 400px; text-align: center; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);';
        
        popup.innerHTML = '<div style="font-size: 18px; font-weight: 600; margin-bottom: 12px; color: #333;">Направлення відсутнє</div>' +
            '<div style="font-size: 14px; color: #666; margin-bottom: 20px;">Для запису на прийом до цього лікаря вам потрібне направлення</div>' +
            '<button style="background: #15479D; color: white; border: none; padding: 10px 24px; border-radius: 8px; font-size: 14px; cursor: pointer; font-weight: 500;">Зрозуміло</button>';
        
        const closeButton = popup.querySelector('button');
        closeButton.addEventListener('click', function() {
            document.body.removeChild(overlay);
        });
        
        overlay.addEventListener('click', function(e) {
            if (e.target === overlay) {
                document.body.removeChild(overlay);
            }
        });
        
        overlay.appendChild(popup);
        document.body.appendChild(overlay);
    }
    
    // Attach event listener to button
    document.addEventListener('DOMContentLoaded', function() {
        const appointmentButton = document.getElementById('appointmentButton');
        if (appointmentButton) {
            console.log('Appointment button found, attaching click handler');
            appointmentButton.addEventListener('click', function(e) {
                e.preventDefault();
                window.scrollToAppointment();
            });
        } else {
            console.log('Appointment button not found');
        }
    });
}
