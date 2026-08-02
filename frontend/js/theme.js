// Applies the saved theme on load (defaulting to light) and wires the
// toggle button. Preference is persisted in memory only for this SPA
// session per artifact storage rules - browser localStorage IS fine
// here since this is a plain HTML/JS app served as static files, not
// a claude.ai artifact (that restriction is specific to the Artifacts
// sandbox, not general web apps).

(function () {
    const STORAGE_KEY = 'helix-theme';

    function getSavedTheme() {
        return localStorage.getItem(STORAGE_KEY) || 'light';
    }

    function applyTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
        const toggleBtn = document.getElementById('theme-toggle');
        if (toggleBtn) {
            toggleBtn.textContent = theme === 'dark' ? '☀️' : '🌙';
        }
        localStorage.setItem(STORAGE_KEY, theme);
    }

    function toggleTheme() {
        const current = document.documentElement.getAttribute('data-theme') || 'light';
        applyTheme(current === 'light' ? 'dark' : 'light');
    }

    // Apply immediately (before DOMContentLoaded) to avoid a flash of
    // the wrong theme on page load.
    applyTheme(getSavedTheme());

    document.addEventListener('DOMContentLoaded', () => {
        const toggleBtn = document.getElementById('theme-toggle');
        if (toggleBtn) {
            toggleBtn.addEventListener('click', toggleTheme);
        }
    });
})();