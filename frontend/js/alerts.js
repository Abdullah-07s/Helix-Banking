// Backs the Alerts & Fraud screen: All/Pending/Reviewed/Blocked tabs
// and status updates. Clicking an alert row cycles it forward
// (Pending -> Reviewed -> Blocked) as a simple interaction, since the
// reference screen implies tapping an alert lets you act on it but
// doesn't specify an exact modal/detail flow (kept minimal per scope).

const Alerts = {

    currentFilter: 'ALL',

    async init() {
        document.querySelectorAll('.alert-tab').forEach(tab => {
            tab.addEventListener('click', () => {
                document.querySelectorAll('.alert-tab').forEach(t => t.classList.remove('active'));
                tab.classList.add('active');
                this.currentFilter = tab.dataset.status;
                this.load();
            });
        });

        await this.load();
    },

    async load() {
        try {
            const query = this.currentFilter === 'ALL' ? '' : `?status=${this.currentFilter}`;
            const alerts = await HelixAPI.get(`/alerts${query}`);
            this.render(alerts);
        } catch (err) {
            Toast.error(err.message);
        }
    },

    render(alerts) {
        const container = document.getElementById('alerts-list');
        const emptyState = document.getElementById('alerts-empty');

        if (alerts.length === 0) {
            container.innerHTML = '';
            emptyState.classList.remove('hidden');
            return;
        }
        emptyState.classList.add('hidden');

        const statusBadge = { PENDING: 'badge-warning', REVIEWED: 'badge-success', BLOCKED: 'badge-danger' };
        const typeIcon = { HIGH_VALUE_TRANSACTION: '💰', MULTIPLE_FAILED_LOGINS: '🔑', SUSPICIOUS_LOCATION: '📍' };

        container.innerHTML = alerts.map(a => `
            <div class="card row-between alert-row" data-alert-id="${a.id}" data-status="${a.status}" style="cursor:pointer;">
                <div style="display:flex; align-items:center; gap:1rem;">
                    <div style="font-size:1.4rem;">${typeIcon[a.type] || '⚠'}</div>
                    <div>
                        <div style="font-weight:700;">${escapeHtml(a.title)}</div>
                        <div style="color:var(--text-muted); font-size:0.85rem;">${escapeHtml(a.description || '')}</div>
                        <div style="color:var(--text-muted); font-size:0.75rem; margin-top:0.2rem;">${formatDate(a.date)}</div>
                    </div>
                </div>
                <span class="badge ${statusBadge[a.status] || 'badge-warning'}">${escapeHtml(a.status)}</span>
            </div>
        `).join('');

        container.querySelectorAll('.alert-row').forEach(row => {
            row.addEventListener('click', () => this.advanceStatus(row.dataset.alertId, row.dataset.status));
        });
    },

    async advanceStatus(alertId, currentStatus) {
        const next = { PENDING: 'REVIEWED', REVIEWED: 'BLOCKED', BLOCKED: 'BLOCKED' };
        const nextStatus = next[currentStatus];

        if (nextStatus === currentStatus) {
            Toast.error('This alert is already blocked.');
            return;
        }

        try {
            await HelixAPI.put(`/alerts/${alertId}/status`, { status: nextStatus });
            Toast.success(`Alert marked as ${nextStatus.toLowerCase()}`);
            await this.load();
        } catch (err) {
            Toast.error(err.message);
        }
    },
};