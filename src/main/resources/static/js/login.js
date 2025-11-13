document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('login-form');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const roleSelect = document.getElementById('role');
    const usernameError = document.getElementById('username-error');
    const passwordError = document.getElementById('password-error');
    const generalError = document.getElementById('general-error');
    const successMessage = document.getElementById('success-message');
    const loginButton = document.querySelector('.login-button');
    
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('registered')) {
        successMessage.style.display = 'block';
        successMessage.textContent = 'Реєстрація успішна! Тепер ви можете увійти.';
    }
    
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        usernameError.style.display = 'none';
        passwordError.style.display = 'none';
        generalError.style.display = 'none';
        successMessage.style.display = 'none';
        
        let isValid = true;
        
        if (!usernameInput.value.trim()) {
            usernameError.textContent = 'Будь ласка, введіть email';
            usernameError.style.display = 'block';
            isValid = false;
        }
        
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (usernameInput.value && !emailRegex.test(usernameInput.value)) {
            usernameError.textContent = 'Будь ласка, введіть коректний email';
            usernameError.style.display = 'block';
            isValid = false;
        }
        
        if (!passwordInput.value) {
            passwordError.textContent = 'Будь ласка, введіть пароль';
            passwordError.style.display = 'block';
            isValid = false;
        }
        
        if (!isValid) {
            return;
        }
        
        loginButton.disabled = true;
        loginButton.textContent = 'Вхід...';
        
        try {
            const loginData = {
                identifier: usernameInput.value,
                password: passwordInput.value,
                method: 'EMAIL',
                role: roleSelect.value
            };
            
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(loginData)
            });
            
            if (response.ok) {
                const token = await response.text();
                localStorage.setItem('authToken', token);
                localStorage.setItem('userRole', roleSelect.value);
                
                successMessage.textContent = 'Успішний вхід! Перенаправлення...';
                successMessage.style.display = 'block';
                
                setTimeout(() => {
                    window.location.href = '/ui/public/';
                }, 1000);
            } else {
                let errorMessage = 'Невірний email або пароль';
                try {
                    const errorData = await response.json();
                    if (errorData.message) {
                        errorMessage = errorData.message;
                    }
                } catch (e) {}
                
                generalError.textContent = errorMessage;
                generalError.style.display = 'block';
            }
        } catch (error) {
            generalError.textContent = 'Помилка з\'єднання. Спробуйте пізніше.';
            generalError.style.display = 'block';
            console.error('Login error:', error);
        } finally {
            loginButton.disabled = false;
            loginButton.textContent = 'Увійти';
        }
    });
    
    const rememberCheckbox = document.getElementById('remember');
    if (rememberCheckbox && localStorage.getItem('rememberLogin')) {
        const savedUsername = localStorage.getItem('savedUsername');
        if (savedUsername) {
            usernameInput.value = savedUsername;
            rememberCheckbox.checked = true;
        }
    }
    
    if (rememberCheckbox) {
        rememberCheckbox.addEventListener('change', function() {
            if (this.checked) {
                localStorage.setItem('rememberLogin', 'true');
                localStorage.setItem('savedUsername', usernameInput.value);
            } else {
                localStorage.removeItem('rememberLogin');
                localStorage.removeItem('savedUsername');
            }
        });
    }
});