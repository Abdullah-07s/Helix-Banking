// Minimal hash-based router. Each route maps to a view HTML fragment
// (fetched from /views/*.html) and an init function that wires up that
// view's behavior (defined in the corresponding screen's JS file).

const Router = {
    routes: {
        'dashboard': { view: 'views/dashboard.html', init: () => Dashboard.init(), title: 'Dashboard' },
        'accounts': { view: 'views/accounts.html', init: () => Accounts.init(), title: 'Accounts' },
        'transfer': { view: 'views/transfer.html', init: () => Transfer.init(), title: 'Transfer' },
        'transactions': { view: 'views/transactions.html', init: () => Transactions.init(), title: 'Transactions' },
        'alerts': { view: 'views/alerts.html', init: () => Alerts.init(), title: 'Alerts & Fraud' },
        'profile': { view: 'views/profile.html', init: () => Profile.init(), title: 'Profile & Settings' },
    },

    async navigate() {
        const hash = window.location.hash.replace('#/', '') || 'dashboard';
        const route = this.routes[hash];

        if (!route) {
            window.location.hash = '#/dashboard';
            return;
        }

        // Highlight the active sidebar item.
        document.querySelectorAll('.nav-item[data-route]').forEach(el => {
            el.classList.toggle('active', el.dataset.route === hash);
        });

        document.getElementById('page-title').textContent = route.title;

        const viewRoot = document.getElementById('view-root');
        const html = await fetch(route.view).then(r => r.text());
        viewRoot.innerHTML = html;

        route.init();
    },

    start() {
        window.addEventListener('hashchange', () => this.navigate());
        this.navigate();
    },
};