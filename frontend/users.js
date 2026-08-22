const API_BASE = 'http://localhost:8080/api';
let allUsers = [];
let currentFilter = 'ALL';
let currentEditId = null;

// Load all users
async function loadUsers() {
    try {
        const response = await fetch(`${API_BASE}/users`);
        const result = await response.json();

        if (result.success) {
            allUsers = result.data || [];
        } else {
            showToast(result.message || 'Failed to load users', 'danger');
        }
    } catch (error) {
        console.error('Error loading users:', error);
        showToast('Backend not responding. Please start the backend server.', 'danger');
        allUsers = [];
    }

    // Always apply filter and update stats
    filterUsers(currentFilter);
    updateStatistics();
}

// Filter users by role
function filterUsers(filter) {
    currentFilter = filter;
    
    // Update active filter button
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');
    
    let filtered = allUsers;
    if (filter !== 'ALL') {
        filtered = allUsers.filter(user => user.role === filter);
    }
    
    displayUsers(filtered);
}

// Display users
function displayUsers(users) {
    const grid = document.getElementById('usersGrid');
    
    if (!users || users.length === 0) {
        grid.innerHTML = `
            <div class="col-12 text-center py-5">
                <i class="bi bi-people" style="font-size: 4rem; color: #ccc;"></i>
                <p class="mt-3 text-muted">No users found</p>
            </div>
        `;
        return;
    }
    
    grid.innerHTML = users.map(user => {
        const initials = user.fullName.split(' ').map(n => n[0]).join('');
        const roleClass = `role-${user.role.toLowerCase()}`;
        
        return `
            <div class="user-card animate__animated animate__fadeIn">
                <div class="user-card-header">
                    <div class="user-card-avatar">${initials}</div>
                    <div class="user-card-info">
                        <div class="user-card-name">${user.fullName}</div>
                        <div class="user-card-username">
                            <i class="bi bi-person"></i>
                            ${user.username}
                        </div>
                    </div>
                </div>
                
                <div class="user-card-body">
                    <div class="user-card-role ${roleClass}">
                        ${user.role}
                    </div>
                    <div class="user-card-status">
                        <span class="status-dot ${user.active ? 'active' : 'inactive'}"></span>
                        ${user.active ? 'Active' : 'Inactive'}
                    </div>
                </div>
                
                <div class="user-card-footer">
                    <button class="card-btn btn-card-edit" onclick="editUser(${user.id})">
                        <i class="bi bi-pencil"></i>
                        Edit
                    </button>
                    <button class="card-btn btn-card-delete" onclick="deleteUser(${user.id})">
                        <i class="bi bi-trash"></i>
                        Delete
                    </button>
                </div>
            </div>
        `;
    }).join('');
}

// Update statistics
function updateStatistics() {
    document.getElementById('totalUsers').textContent = allUsers.length;
    document.getElementById('activeUsers').textContent = allUsers.filter(u => u.active).length;
    document.getElementById('adminCount').textContent = allUsers.filter(u => u.role === 'ADMIN').length;
    document.getElementById('managerCount').textContent = allUsers.filter(u => u.role === 'MANAGER').length;
}

// Open add user modal
function openAddUserModal() {
    currentEditId = null;
    document.getElementById('userModalTitle').textContent = 'Add New User';
    document.getElementById('userForm').reset();
    document.getElementById('userId').value = '';
    document.getElementById('password').required = true;
    
    const modal = new bootstrap.Modal(document.getElementById('userModal'));
    modal.show();
}

// Edit user
async function editUser(id) {
    try {
        const response = await fetch(`${API_BASE}/users/${id}`);
        const result = await response.json();
        
        if (result.success) {
            const user = result.data;
            currentEditId = id;
            
            document.getElementById('userModalTitle').textContent = 'Edit User';
            document.getElementById('userId').value = user.id;
            document.getElementById('fullName').value = user.fullName;
            document.getElementById('username').value = user.username;
            document.getElementById('password').value = '';
            document.getElementById('password').required = false;
            document.getElementById('role').value = user.role;
            document.getElementById('active').checked = user.active;
            
            const modal = new bootstrap.Modal(document.getElementById('userModal'));
            modal.show();
        }
    } catch (error) {
        console.error('Error loading user:', error);
        alert('Failed to load user details');
    }
}

// Delete user
async function deleteUser(id) {
    if (!confirm('Are you sure you want to delete this user?')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/users/${id}`, { method: 'DELETE' });
        const result = await response.json();

        if (result.success) {
            loadUsers();
            showToast('User deleted successfully', 'success');
        } else {
            showToast('Failed to delete user: ' + result.message, 'danger');
        }
    } catch (error) {
        console.error('Error deleting user:', error);
        showToast('Backend not responding. Please start the backend server.', 'danger');
    }
}

// Handle form submission
document.getElementById('userForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const userData = {
        fullName: document.getElementById('fullName').value,
        username: document.getElementById('username').value,
        role: document.getElementById('role').value,
        active: document.getElementById('active').checked
    };
    
    const password = document.getElementById('password').value;
    if (password) {
        userData.password = password;
    }
    
    try {
        const url = currentEditId ? `${API_BASE}/users/${currentEditId}` : `${API_BASE}/users`;
        const method = currentEditId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        const result = await response.json();

        if (result.success) {
            bootstrap.Modal.getInstance(document.getElementById('userModal')).hide();
            loadUsers();
            showSuccessMessage(currentEditId ? 'User updated successfully!' : 'User added successfully!');
        } else {
            showToast('Failed to save user: ' + result.message, 'danger');
        }
    } catch (error) {
        console.error('Error saving user:', error);
        showToast('Backend not responding. Please start the backend server.', 'danger');
    }
});

// Toggle sidebar
function toggleSidebar() {
    document.getElementById('sidebar').classList.toggle('active');
}

// Show toast notification
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `alert alert-${type} position-fixed top-0 end-0 m-3`;
    toast.style.zIndex = '9999';
    toast.style.minWidth = '300px';
    toast.innerHTML = `
        <i class="bi bi-${type === 'success' ? 'check-circle' : type === 'danger' ? 'x-circle' : 'info-circle'} me-2"></i>
        <span>${message}</span>
    `;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 4000);
}

function showSuccessMessage(message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-success position-fixed top-0 start-50 translate-middle-x mt-3';
    alertDiv.style.zIndex = '9999';
    alertDiv.style.minWidth = '300px';
    alertDiv.innerHTML = `
        <i class="bi bi-check-circle-fill me-2"></i>
        <strong>${message}</strong>
    `;
    document.body.appendChild(alertDiv);
    setTimeout(() => alertDiv.remove(), 3000);
}

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    // Check if user is admin
    const user = getCurrentUser();
    if (user && user.role !== 'ADMIN' && user.role !== 'MANAGER') {
        window.location.href = 'dashboard.html';
        return;
    }
    loadUsers();
});
