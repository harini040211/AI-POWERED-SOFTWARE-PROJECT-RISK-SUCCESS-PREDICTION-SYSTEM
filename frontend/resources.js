const API_BASE = 'http://localhost:8080/api';
let currentEditId = null;

// Load all resources
async function loadResources() {
    try {
        const response = await fetch(`${API_BASE}/resources`);
        const result = await response.json();
        
        if (result.success) {
            displayResources(result.data);
            updateStatistics(result.data);
        }
    } catch (error) {
        console.error('Error loading resources:', error);
    }
}

// Display resources in table
function displayResources(resources) {
    const tbody = document.getElementById('resourcesTableBody');
    
    if (!resources || resources.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center py-5">
                    <i class="bi bi-inbox" style="font-size: 3rem; color: #ccc;"></i>
                    <p class="mt-3 text-muted">No resources found. Add your first resource!</p>
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = resources.map(resource => {
        const utilization = resource.utilizationPercentage || 0;
        const status = resource.utilizationStatus || 'UNDERUTILIZED';
        
        let barClass = 'util-underutilized';
        let badgeClass = 'badge-underutilized';
        
        if (status === 'OPTIMAL') {
            barClass = 'util-optimal';
            badgeClass = 'badge-optimal';
        } else if (status === 'HIGH') {
            barClass = 'util-high';
            badgeClass = 'badge-high';
        } else if (status === 'OVERLOADED') {
            barClass = 'util-overloaded';
            badgeClass = 'badge-overloaded';
        }
        
        const initials = resource.employeeName.split(' ').map(n => n[0]).join('');
        
        return `
            <tr>
                <td>
                    <div class="employee-info">
                        <div class="employee-avatar">${initials}</div>
                        <div class="employee-details">
                            <div class="employee-name">${resource.employeeName}</div>
                            <div class="employee-role">${resource.role}</div>
                        </div>
                    </div>
                </td>
                <td>${resource.availableHours} hrs</td>
                <td>${resource.assignedHours} hrs</td>
                <td>
                    <div class="utilization-bar">
                        <div class="utilization-fill ${barClass}" style="width: ${Math.min(utilization, 100)}%"></div>
                    </div>
                    <small class="text-muted">${utilization.toFixed(1)}%</small>
                </td>
                <td>
                    <span class="status-badge ${badgeClass}">
                        ${status}
                    </span>
                </td>
                <td>
                    <div class="action-btns">
                        <button class="btn-action btn-edit" onclick="editResource(${resource.id})" title="Edit">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn-action btn-delete" onclick="deleteResource(${resource.id})" title="Delete">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

// Update statistics
function updateStatistics(resources) {
    document.getElementById('totalResources').textContent = resources.length;
    
    const avgUtil = resources.reduce((sum, r) => sum + (r.utilizationPercentage || 0), 0) / resources.length;
    document.getElementById('avgUtilization').textContent = avgUtil.toFixed(1) + '%';
    
    const overloaded = resources.filter(r => r.utilizationStatus === 'OVERLOADED').length;
    document.getElementById('overloadedCount').textContent = overloaded;
    
    const underutilized = resources.filter(r => r.utilizationStatus === 'UNDERUTILIZED').length;
    document.getElementById('underutilizedCount').textContent = underutilized;
}

// Open add modal
function openAddModal() {
    currentEditId = null;
    document.getElementById('modalTitle').textContent = 'Add New Resource';
    document.getElementById('resourceForm').reset();
    document.getElementById('resourceId').value = '';
    
    const modal = new bootstrap.Modal(document.getElementById('resourceModal'));
    modal.show();
}

// Edit resource
async function editResource(id) {
    try {
        const response = await fetch(`${API_BASE}/resources/${id}`);
        const result = await response.json();
        
        if (result.success) {
            const resource = result.data;
            currentEditId = id;
            
            document.getElementById('modalTitle').textContent = 'Edit Resource';
            document.getElementById('resourceId').value = resource.id || resource.resourceId;
            document.getElementById('employeeName').value = resource.employeeName || resource.name || resource.resourceName;
            document.getElementById('role').value = resource.role;
            document.getElementById('availableHours').value = resource.availableHours;
            document.getElementById('assignedHours').value = resource.assignedHours;
            
            const modal = new bootstrap.Modal(document.getElementById('resourceModal'));
            modal.show();
        } else {
            alert('Error loading resource: ' + result.message);
        }
    } catch (error) {
        console.error('Error loading resource:', error);
        alert('Error loading resource details. Is backend running?');
    }
}

// Delete resource
async function deleteResource(id) {
    if (!confirm('Are you sure you want to delete this resource?')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/resources/${id}`, {
            method: 'DELETE'
        });
        
        const result = await response.json();
        
        if (result.success) {
            loadResources();
            showToast('Resource deleted successfully', 'success');
        } else {
            alert('Failed to delete resource: ' + result.message);
        }
    } catch (error) {
        console.error('Error deleting resource:', error);
        alert('Failed to delete resource');
    }
}

// Handle form submission
document.getElementById('resourceForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const resourceId = document.getElementById('resourceId').value;
    const resourceData = {
        employeeName: document.getElementById('employeeName').value,
        role: document.getElementById('role').value,
        availableHours: parseFloat(document.getElementById('availableHours').value),
        assignedHours: parseFloat(document.getElementById('assignedHours').value)
    };
    
    console.log('Sending resource data:', resourceData);
    
    try {
        let response;
        
        if (currentEditId || resourceId) {
            // Update existing resource
            const id = currentEditId || resourceId;
            console.log('Updating resource ID:', id);
            response = await fetch(`${API_BASE}/resources/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(resourceData)
            });
        } else {
            // Create new resource
            console.log('Creating new resource');
            response = await fetch(`${API_BASE}/resources`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(resourceData)
            });
        }
        
        console.log('Response status:', response.status);
        
        if (!response.ok) {
            const errorText = await response.text();
            console.error('Error response:', errorText);
            alert('Error saving resource: ' + (errorText || 'Backend error'));
            return;
        }
        
        const result = await response.json();
        console.log('Result:', result);
        
        if (result.success) {
            bootstrap.Modal.getInstance(document.getElementById('resourceModal')).hide();
            loadResources();
            showSuccessMessage(currentEditId || resourceId ? 'Resource updated successfully!' : 'Resource added successfully!');
            currentEditId = null;
        } else {
            alert('Failed to save resource: ' + (result.message || 'Unknown error'));
        }
    } catch (error) {
        console.error('Error saving resource:', error);
        alert('Failed to save resource: Backend not responding. Is it running on port 8080?');
    }
});

// Search functionality
document.getElementById('searchInput').addEventListener('input', function(e) {
    const searchTerm = e.target.value.toLowerCase();
    const rows = document.querySelectorAll('#resourcesTableBody tr');
    
    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(searchTerm) ? '' : 'none';
    });
});

// Toggle sidebar
function toggleSidebar() {
    document.getElementById('sidebar').classList.toggle('active');
}

// Show toast notification
function showToast(message, type = 'info') {
    showSuccessMessage(message);
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
    loadResources();
});
