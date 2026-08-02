// Handles the Login/Register screens: loading the right view HTML into
// #auth-root, form submission, error display, and transitioning to the
// main app shell on success.

const AuthScreens = {

    async init() {
        await this.showLogin();
    },

    async showLogin() {
        const root = document.getElementById('auth-root');
        root.innerHTML = await fetch('views/login.html').then(r => r.text());
        this.wireLoginForm();
        document.getElementById('go-to-register').addEventListener('click', (e) => {
            e.preventDefault();
            this.showRegister();
        });
    },

    async showRegister() {
        const root = document.getElementById('auth-root');
        root.innerHTML = await fetch('views/register.html').then(r => r.text());
        this.wireRegisterForm();
        document.getElementById('go-to-login').addEventListener('click', (e) => {
            e.preventDefault();
            this.showLogin();
        });
    },

    wireLoginForm() {
        const form = document.getElementById('login-form');
        const errorBox = document.getElementById('login-error');

        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            errorBox.classList.add('hidden');

            const email = document.getElementById('login-email').value.trim();
            const password = document.getElementById('login-password').value;

            try {
                const result = await HelixAPI.post('/auth/login', { email, password });
                HelixAPI.setToken(result.token);
                HelixAPI.setUser({ fullName: result.fullName, email: result.email });
                window.HelixApp.showAppShell();
            } catch (err) {
                errorBox.textContent = err.message;
                errorBox.classList.remove('hidden');
            }
        });
    },

    wireRegisterForm() {
        const form = document.getElementById('register-form');
        const errorBox = document.getElementById('register-error');

        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            errorBox.classList.add('hidden');

            const payload = {
                fullName: document.getElementById('reg-fullname').value.trim(),
                email: document.getElementById('reg-email').value.trim(),
                phoneNumber: document.getElementById('reg-phone').value.trim(),
                password: document.getElementById('reg-password').value,
                confirmPassword: document.getElementById('reg-confirm').value,
            };

            if (payload.password !== payload.confirmPassword) {
                errorBox.textContent = 'Passwords do not match.';
                errorBox.classList.remove('hidden');
                return;
            }

            try {
                const result = await HelixAPI.post('/auth/register', payload);
                HelixAPI.setToken(result.token);
                HelixAPI.setUser({ fullName: result.fullName, email: result.email });
                window.HelixApp.showAppShell();
            } catch (err) {
                errorBox.textContent = err.message;
                errorBox.classList.remove('hidden');
            }
        });
    },
};