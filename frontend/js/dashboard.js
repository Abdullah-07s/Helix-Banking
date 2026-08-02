// Backs the Dashboard / Home screen: total balance (summed across all
// accounts), recent transactions list, and quick action links.
// Spending Overview donut chart is intentionally omitted (would need a
// category-tagging feature on transactions that doesn't exist in the
// backend - out of scope per the original spec's endpoint list).

const Dashboard = {

    async init() {
        try {
            const [accounts, transactions] = await Promise.all([
                HelixAPI.get('/accounts'),
                HelixAPI.get('/transactions'),
            ]);

            this.renderBalance(accounts);
            this.renderRecentTransactions(transactions.slice(0, 4));
        } catch (err) {
            Toast.error(err.message);
        }
    },

    renderBalance(accounts) {
        const total = accounts.reduce((sum, a) => sum + a.balance, 0);
        document.getElementById('total-balance').textContent = formatCurrency(total);
    },

    renderRecentTransactions(transactions) {
        const container = document.getElementById('recent-transactions-list');

        if (transactions.length === 0) {
            container.innerHTML = '<div class="empty-state">No transactions yet</div>';
            return;
        }

        container.innerHTML = transactions.map(t => `
            <div class="row-between" style="padding:0.6rem 0; border-bottom:1px solid var(--border-color);">
                <div>
                    <div style="font-weight:600; font-size:0.9rem;">${escapeHtml(t.description)}</div>
                    <div style="color:var(--text-muted); font-size:0.8rem;">${formatDate(t.date)}</div>
                </div>
                <div style="font-weight:700; color:${t.type === 'Debit' ? 'var(--danger)' : 'var(--success)'};">
                    ${t.type === 'Debit' ? '-' : '+'}${formatCurrency(t.amount)}
                </div>
            </div>
        `).join('');
    },
};