// Backs the Transaction History screen: full list with client-side
// search filtering by description. Date-range filtering was on the
// reference screen too but is skipped here since the backend's
// GET /api/transactions endpoint doesn't accept date params (would be
// scope creep to add without discussion) - search covers the core need.

const Transactions = {

    allTransactions: [],

    async init() {
        try {
            this.allTransactions = await HelixAPI.get('/transactions');
            this.render(this.allTransactions);
        } catch (err) {
            Toast.error(err.message);
        }

        document.getElementById('tx-search').addEventListener('input', (e) => {
            const term = e.target.value.toLowerCase();
            const filtered = this.allTransactions.filter(t =>
                t.description.toLowerCase().includes(term)
            );
            this.render(filtered);
        });
    },

    render(transactions) {
        const tbody = document.getElementById('tx-table-body');
        const emptyState = document.getElementById('tx-empty');

        if (transactions.length === 0) {
            tbody.innerHTML = '';
            emptyState.classList.remove('hidden');
            return;
        }
        emptyState.classList.add('hidden');

        const statusBadge = { SUCCESSFUL: 'badge-success', PENDING: 'badge-warning', FAILED: 'badge-danger' };

        tbody.innerHTML = transactions.map(t => `
            <tr style="border-bottom:1px solid var(--border-color);">
                <td style="padding:0.75rem 0.6rem; font-size:0.85rem; color:var(--text-secondary);">${formatDate(t.date)}</td>
                <td style="padding:0.75rem 0.6rem; font-weight:600;">${escapeHtml(t.description)}</td>
                <td style="padding:0.75rem 0.6rem;">${escapeHtml(t.type)}</td>
                <td style="padding:0.75rem 0.6rem; font-weight:700; color:${t.type === 'Debit' ? 'var(--danger)' : 'var(--success)'};">
                    ${t.type === 'Debit' ? '-' : '+'}${formatCurrency(t.amount)}
                </td>
                <td style="padding:0.75rem 0.6rem;">
                    <span class="badge ${statusBadge[t.status] || 'badge-warning'}">${escapeHtml(t.status)}</span>
                </td>
            </tr>
        `).join('');
    },
};