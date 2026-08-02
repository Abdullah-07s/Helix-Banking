// Backs the Profile & Settings screen's "Profile Information" and
// "Change Password" cards. Notification Preferences / Two-Factor /
// Active Sessions cards from the reference screen are omitted since
// no backend endpoints exist for them (would need new scope - stop
// and ask territory, not addressed here per original spec).

const Profile = {

    async init() {
        try {
            const profile = await HelixAPI.get('/profile');
            document.getElementById('profile-fullname').value = profile.fullName;
            document.getElementById('profile-email').value = profile.email;
            document.getElementById('profile-phone').value = profile.phoneNumber;
        } catch (err) {
            Toast.error(err.message);
        }

        document.getElementById('profile-form').addEventListener('submit', (e) => this.updateProfile(e));
        document.getElementById('password-form').addEventListener('submit', (e) => this.changePassword(e));
    },

    async updateProfile(e) {
        e.preventDefault();
        try {
            const updated = await HelixAPI.put('/profile', {
                fullName: document.getElementById('profile-fullname').value.trim(),
                phoneNumber: document.getElementById('profile-phone').value.trim(),
            });

            // Keep the sidebar/topbar greeting in sync with the new name.
            const user = HelixAPI.getUser();
            HelixAPI.setUser({ ...user, fullName: updated.fullName });
            document.getElementById('user-greeting').textContent = `Hello, ${updated.fullName}`;

            Toast.success('Profile updated successfully');
        } catch (err) {
            Toast.error(err.message);
        }
    },

    async changePassword(e) {
        e.preventDefault();
        const currentPassword = document.getElementById('current-password').value;
        const newPassword = document.getElementById('new-password').value;
        const confirmNewPassword = document.getElementById('confirm-new-password').value;

        if (newPassword !== confirmNewPassword) {
            Toast.error('New passwords do not match.');
            return;
        }

        try {
            await HelixAPI.put('/profile/password', { currentPassword, newPassword, confirmNewPassword });
            Toast.success('Password changed successfully');
            document.getElementById('password-form').reset();
        } catch (err) {
            Toast.error(err.message);
        }
    },
};