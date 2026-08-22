// Common Authentication Functions - Used across all pages

// Get current user from localStorage
function getCurrentUser() {
    const userStr = localStorage.getItem('user');
    if (!userStr) {
        // No user logged in, redirect to login
        if (!window.location.href.includes('login.html')) {
            window.location.href = 'login.html';
        }
        return null;
    }
    return JSON.parse(userStr);
}

// Check if user is logged in
function checkAuth() {
    const user = getCurrentUser();
    if (!user && !window.location.href.includes('login.html')) {
        window.location.href = 'login.html';
        return false;
    }
    return true;
}

// Logout function
function logout() {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    window.location.href = 'login.html';
}

// Load user info and apply styling (called on every page)
function loadUserInfo() {
    const user = getCurrentUser();
    if (!user) return;

    // Update sidebar user info
    const userNameEl = document.getElementById('userName');
    const userRoleEl = document.getElementById('userRole');
    
    if (userNameEl) {
        userNameEl.textContent = user.fullName || user.username;
    }
    if (userRoleEl) {
        userRoleEl.textContent = user.role || 'USER';
    }

    // Apply background based on role
    if (user.role === 'MANAGER' || user.role === 'ADMIN') {
        document.body.classList.add('manager-dashboard');
    } else {
        document.body.classList.add('user-dashboard');
    }

    // Hide user management for non-admin/manager users
    if (user.role !== 'ADMIN' && user.role !== 'MANAGER') {
        const usersMenu = document.getElementById('usersMenu');
        if (usersMenu) {
            usersMenu.style.display = 'none';
        }
    }
}

// Initialize auth on page load
document.addEventListener('DOMContentLoaded', function() {
    // Don't check auth on login page
    if (!window.location.href.includes('login.html')) {
        checkAuth();
        loadUserInfo();
    }
});
