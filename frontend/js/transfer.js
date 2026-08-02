// Backs the Transfer Money + confirmation screens. The recipient's
// account ID is collected directly from the user (per the design note
// in Phase 4 - the Account service doesn't expose an account-number
// lookup endpoint, so this frontend passes both the human-entered
// account number for display/history AND the numeric account id the
// backend actually needs to move money).

const Transfer = {

    async init() {
        document.getElementById('transfer-confirmation').classList.add('hidden');
        document.getElementById('transfer-form').closest('.card').classList.remove('hidden');

        await this.loadFromAccounts();

        document.getElementById('transfer-form').addEventListener('submit', (e) => this.submit(e));
        document.getElementById('confirmation-done').addEventListener('click', () => {
            window.location.hash = '#/dashboard';
        });
    },

    async loadFromAccounts() {
        try {
            const accounts = await HelixAPI.get('/accounts');
            const select = document.getElementById('transfer-from');
            select.innerHTML = accounts.map(a =>
                `<option value="${a.id}">${escapeHtml(a.label)} (${a.accountNumberMasked}) - ${formatCurrency(a.balance)}</option>`
            ).join('');
        } catch (err) {
            Toast.error(err.message);
        }
    },

    async submit(e) {
        e.preventDefault();
        const errorBox = document.getElementById('transfer-error');
        errorBox.classList.add('hidden');

        const fromAccountId = document.getElementById('transfer-from').value;
        const toAccountId = document.getElementById('transfer-to-account-id').value;
        const recipientAccountNumber = document.getElementById('transfer-recipient-number').value.trim();
        const amount = parseFloat(document.getElementById('transfer-amount').value);
        const note = document.getElementById('transfer-note').value.trim();

        try {
            const result = await HelixAPI.post(`/transactions/transfer?toAccountId=${toAccountId}`, {
                fromAccountId: Number(fromAccountId),
                recipientAccountNumber,
                amount,
                note: note || undefined,
            });

            this.showConfirmation(result);
        } catch (err) {
            errorBox.textContent = err.message;
            errorBox.classList.remove('hidden');
        }
    },

    showConfirmation(result) {
        document.getElementById('transfer-form').closest('.card').classList.add('hidden');
        const confirmation = document.getElementById('transfer-confirmation');
        confirmation.classList.remove('hidden');

        document.getElementById('confirmation-summary').textContent =
            `${formatCurrency(result.amount)} has been sent to ${result.recipientAccountNumberMasked} · ${formatDate(result.timestamp)}`;
    },
};