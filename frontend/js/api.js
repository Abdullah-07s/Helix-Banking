// Thin fetch wrapper for talking to the Helix API Gateway (port 8080),
// plus small shared UI helpers (currency/date formatting, HTML escaping,
// toast notifications) used across every screen. Kept in one file since
// these are all "utility layer" concerns that every other screen's JS
// depends on and are loaded before them in index.html.

const API_BASE = 'http://localhost:8080/api';

const HelixAPI = {

    getToken() {
        return sessionStorage.getItem('helix-token');
    },

    setToken(token) {
        sessionStorage.setItem('helix-token', token);
    },

    clearToken() {
        sessionStorage.removeItem('helix-token');
        sessionStorage.removeItem('helix-user');
    },

    getUser() {
        const raw = sessionStorage.getItem('helix-user');
        return raw ? JSON.parse(raw) : null;
    },

    setUser(user) {
        sessionStorage.setItem('helix-user', JSON.stringify(user));
    },

    isAuthenticated() {
        return !!this.getToken();
    },

    /**
     * Core request method. Returns the parsed `data` field of the
     * ApiResponse envelope on success, or throws an Error with the
     * server's message on failure.
     */
    async request(method, path, body) {
        const headers = { 'Content-Type': 'application/json' };
        const token = this.getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const response = await fetch(`${API_BASE}${path}`, {
            method,
            headers,
            body: body ? JSON.stringify(body) : undefined,
        });

        let payload;
        try {
            payload = await response.json();
        } catch {
            payload = null;
        }

        if (!response.ok) {
            const message = payload?.message || `Request failed (${response.status})`;
            throw new Error(message);
        }

        return payload?.data;
    },

    get(path) {
        return this.request('GET', path);
    },

    post(path, body) {
        return this.request('POST', path, body);
    },

    put(path, body) {
        return this.request('PUT', path, body);
    },
};

// ---------- Shared formatting helpers ----------

function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount);
}

function formatDate(isoString) {
    const d = new Date(isoString);
    return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }) +
           ' · ' + d.toLocaleTimeString('en-US', { hour: 'numeric', minute: '2-digit' });
}

function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str ?? '';
    return div.innerHTML;
}

// ---------- Toast notifications ----------

const Toast = {
    show(message, type = 'success') {
        const root = document.getElementById('toast-root');
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.textContent = message;
        root.appendChild(toast);
        setTimeout(() => toast.remove(), 3500);
    },
    success(message) { this.show(message, 'success'); },
    error(message) { this.show(message, 'error'); },
};