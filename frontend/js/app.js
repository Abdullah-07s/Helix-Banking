// Bootstraps the SPA: decides whether to show the auth screens (login/
// register) or the main app shell based on token presence, and wires
// up global chrome (logout button + modal, user greeting).

(async function () {

    function showAuthScreens() {
        document.getElementById('auth-root').classList.remove('hidden');
        document.getElementById('app-shell').classList.add('hidden');
        AuthScreens.init();
    }

    async function showAppShell() {
        document.getElementById('auth-root').classList.add('hidden');
        document.getElementById('app-shell').classList.remove('hidden');

        const user = HelixAPI.getUser();
        document.getElementById('user-greeting').textContent = user ? `Hello, ${user.fullName}` : '';

        Router.start();
    }

    function setupLogoutModal() {
        const modal = document.getElementById('logout-modal');
        document.getElementById('logout-trigger').addEventListener('click', () => {
            modal.classList.remove('hidden');
        });
        document.getElementById('logout-cancel').addEventListener('click', () => {
            modal.classList.add('hidden');
        });
        document.getElementById('logout-confirm').addEventListener('click', () => {
            HelixAPI.clearToken();
            modal.classList.add('hidden');
            window.location.hash = '';
            init();
        });
    }

    function init() {
        setupLogoutModal();
        if (HelixAPI.isAuthenticated()) {
            showAppShell();
        } else {
            showAuthScreens();
        }
    }

    document.addEventListener('DOMContentLoaded', init);

    // Expose a global so auth.js can trigger the transition to the app
    // shell right after a successful login/register without a full reload.
    window.HelixApp = { showAppShell };
})();