/**
 * CityFix - Authentication JavaScript
 */

document.addEventListener('DOMContentLoaded', () => {
    // Citizen Login Form
    const loginForm = document.getElementById('citizenLoginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const btn = loginForm.querySelector('button[type="submit"]');
            const alertBox = document.getElementById('loginAlert');
            alertBox.classList.add('d-none');

            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value;

            if (!email || !password) {
                showAlert(alertBox, 'Please fill in both email and password.');
                return;
            }

            btn.disabled = true;
            btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Logging in...';

            try {
                const res = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, password })
                });
                const data = await res.json();

                if (data.success) {
                    showToast('Login successful! Redirecting...', 'success');
                    setTimeout(() => {
                        window.location.href = '/dashboard';
                    }, 500);
                } else {
                    showAlert(alertBox, data.message || 'Login failed. Check credentials.');
                    btn.disabled = false;
                    btn.innerHTML = '<i class="bi bi-box-arrow-in-right"></i> Sign In';
                }
            } catch (err) {
                showAlert(alertBox, 'Server connection error. Please try again.');
                btn.disabled = false;
                btn.innerHTML = '<i class="bi bi-box-arrow-in-right"></i> Sign In';
            }
        });
    }

    // Citizen Registration Form
    const registerForm = document.getElementById('citizenRegisterForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const btn = registerForm.querySelector('button[type="submit"]');
            const alertBox = document.getElementById('registerAlert');
            alertBox.classList.add('d-none');

            const name = document.getElementById('name').value.trim();
            const email = document.getElementById('email').value.trim();
            const phone = document.getElementById('phone').value.trim();
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            if (!name || !email || !phone || !password || !confirmPassword) {
                showAlert(alertBox, 'All fields are required.');
                return;
            }

            if (password !== confirmPassword) {
                showAlert(alertBox, 'Passwords do not match.');
                return;
            }

            if (password.length < 6) {
                showAlert(alertBox, 'Password must be at least 6 characters long.');
                return;
            }

            btn.disabled = true;
            btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Creating Account...';

            try {
                const res = await fetch('/api/auth/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name, email, phone, password, confirmPassword })
                });
                const data = await res.json();

                if (data.success) {
                    showToast('Registration successful! Redirecting to login...', 'success');
                    setTimeout(() => {
                        window.location.href = '/login?registered=true';
                    }, 1000);
                } else {
                    let errMsg = data.message || 'Registration failed.';
                    if (data.errors) {
                        errMsg = Object.values(data.errors).join('<br>');
                    }
                    showAlert(alertBox, errMsg);
                    btn.disabled = false;
                    btn.innerHTML = '<i class="bi bi-person-plus-fill"></i> Create Account';
                }
            } catch (err) {
                showAlert(alertBox, 'Server connection error. Please try again.');
                btn.disabled = false;
                btn.innerHTML = '<i class="bi bi-person-plus-fill"></i> Create Account';
            }
        });
    }

    // Admin Login Form
    const adminLoginForm = document.getElementById('adminLoginForm');
    if (adminLoginForm) {
        adminLoginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const btn = adminLoginForm.querySelector('button[type="submit"]');
            const alertBox = document.getElementById('adminLoginAlert');
            alertBox.classList.add('d-none');

            const username = document.getElementById('username').value.trim();
            const password = document.getElementById('password').value;

            if (!username || !password) {
                showAlert(alertBox, 'Please enter administrator username and password.');
                return;
            }

            btn.disabled = true;
            btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Authenticating...';

            try {
                const res = await fetch('/api/auth/admin-login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email: username, password })
                });
                const data = await res.json();

                if (data.success) {
                    showToast('Admin login successful! Redirecting...', 'success');
                    setTimeout(() => {
                        window.location.href = '/admin/dashboard';
                    }, 500);
                } else {
                    showAlert(alertBox, data.message || 'Invalid admin credentials.');
                    btn.disabled = false;
                    btn.innerHTML = '<i class="bi bi-shield-lock"></i> Admin Sign In';
                }
            } catch (err) {
                showAlert(alertBox, 'Server connection error. Please try again.');
                btn.disabled = false;
                btn.innerHTML = '<i class="bi bi-shield-lock"></i> Admin Sign In';
            }
        });
    }
});

function showAlert(elem, msg) {
    if (!elem) return;
    elem.innerHTML = msg;
    elem.classList.remove('d-none');
}
