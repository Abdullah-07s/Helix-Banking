// Backs the Accounts screen: shows a quick-reference panel (account ID +
// masked number, needed for the Transfer screen), the full accounts list,
// cards list, an "Add Money" (deposit) modal, and a simple "add card"
// action tied to the first account found (kept minimal since the
// reference screen doesn't show a detailed account-picker modal for
// card creation).

const Accounts = {

    accountsCache: [],

    async init() {
        await Promise.all([this.loadAccounts(), this.loadCards()]);

        document.getElementById('add-card-btn').addEventListener('click', () => this.addCard());
        document.getElementById('deposit-btn').addEventListener('click', () => this.openDepositModal());
        document.getElementById('deposit-cancel').addEventListener('click', () => this.closeDepositModal());
        document.getElementById('deposit-form').addEventListener('submit', (e) => this.submitDeposit(e));
    },

    async loadAccounts() {
        try {
            const accounts = await HelixAPI.get('/accounts');
            this.accountsCache = accounts;
            this.renderReference(accounts);
            this.renderAccounts(accounts);
        } catch (err) {
            Toast.error(err.message);
        }
    },

    renderReference(accounts) {
        const container = document.getElementById('account-reference-list');

        if (accounts.length === 0) {
            container.innerHTML = '<div class="empty-state">No accounts yet</div>';
            return;
        }

        container.innerHTML = accounts.map(a => `
            <div class="row-between" style="padding:0.5rem 0; border-bottom:1px solid var(--border-color);">
                <span style="font-weight:600;">${escapeHtml(a.label)}</span>
                <span style="color:var(--text-secondary); font-size:0.85rem;">
                    Account ID: <strong style="color:var(--text-primary);">${a.id}</strong>
                    &nbsp;·&nbsp;
                    Number: <strong style="color:var(--text-primary);">${a.accountNumberMasked}</strong>
                </span>
            </div>
        `).join('');
    },

    renderAccounts(accounts) {
        const container = document.getElementById('accounts-list');

        if (accounts.length === 0) {
            container.innerHTML = '<div class="empty-state">No accounts yet</div>';
            return;
        }

        const icons = { CHECKING: '🏦', SAVINGS: '💰', CREDIT: '💳' };

        container.innerHTML = accounts.map(a => `
            <div class="card row-between">
                <div style="display:flex; align-items:center; gap:1rem;">
                    <div style="font-size:1.5rem;">${icons[a.type] || '🏦'}</div>
                    <div>
                        <div style="font-weight:700;">
                            ${escapeHtml(a.label)}
                            <span style="color:var(--text-muted); font-weight:400; font-size:0.8rem;">(ID: ${a.id})</span>
                        </div>
                        <div style="color:var(--text-muted); font-size:0.85rem;">${a.accountNumberMasked} · ${escapeHtml(a.type)}</div>
                    </div>
                </div>
                <div style="text-align:right;">
                    <div style="font-weight:700; font-size:1.1rem;">${formatCurrency(a.balance)}</div>
                    <div style="color:var(--text-muted); font-size:0.8rem;">Available Balance</div>
                </div>
            </div>
        `).join('');
    },

    async loadCards() {
        try {
            const cards = await HelixAPI.get('/cards');
            this.renderCards(cards);
        } catch (err) {
            Toast.error(err.message);
        }
    },

    renderCards(cards) {
        const container = document.getElementById('cards-list');

        if (cards.length === 0) {
            container.innerHTML = '<div class="empty-state">No cards yet</div>';
            return;
        }

        const statusBadge = { ACTIVE: 'badge-success', FROZEN: 'badge-warning', BLOCKED: 'badge-danger' };

        container.innerHTML = cards.map(c => `
            <div class="card row-between">
                <div style="display:flex; align-items:center; gap:1rem;">
                    <div style="font-size:1.5rem;">💳</div>
                    <div>
                        <div style="font-weight:700;">${escapeHtml(c.network)} ${escapeHtml(c.type)}</div>
                        <div style="color:var(--text-muted); font-size:0.85rem;">${c.cardNumberMasked} · Exp ${c.expiry}</div>
                    </div>
                </div>
                <span class="badge ${statusBadge[c.status] || 'badge-warning'}">${escapeHtml(c.status)}</span>
            </div>
        `).join('');
    },

    async addCard() {
        if (this.accountsCache.length === 0) {
            Toast.error('You need an account before adding a card.');
            return;
        }

        try {
            await HelixAPI.post('/cards', {
                accountId: this.accountsCache[0].id,
                type: 'CREDIT',
                network: 'VISA',
            });
            Toast.success('Card created successfully');
            await this.loadCards();
        } catch (err) {
            Toast.error(err.message);
        }
    },

    // ---------- Deposit (Add Money) ----------

    openDepositModal() {
        if (this.accountsCache.length === 0) {
            Toast.error('You need an account first.');
            return;
        }

        const select = document.getElementById('deposit-account');
        select.innerHTML = this.accountsCache.map(a =>
            `<option value="${a.id}">${escapeHtml(a.label)} (${a.accountNumberMasked})</option>`
        ).join('');

        document.getElementById('deposit-error').classList.add('hidden');
        document.getElementById('deposit-form').reset();
        document.getElementById('deposit-modal').classList.remove('hidden');
    },

    closeDepositModal() {
        document.getElementById('deposit-modal').classList.add('hidden');
    },

    async submitDeposit(e) {
        e.preventDefault();
        const errorBox = document.getElementById('deposit-error');
        errorBox.classList.add('hidden');

        const accountId = Number(document.getElementById('deposit-account').value);
        const amount = parseFloat(document.getElementById('deposit-amount').value);
        const note = document.getElementById('deposit-note').value.trim();

        try {
            await HelixAPI.post('/transactions/deposit', { accountId, amount, note: note || undefined });
            Toast.success(`${formatCurrency(amount)} added successfully`);
            this.closeDepositModal();
            await this.loadAccounts();
        } catch (err) {
            errorBox.textContent = err.message;
            errorBox.classList.remove('hidden');
        }
    },
};