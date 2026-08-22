// API Base URL
const API_BASE = 'http://localhost:8080/api';

// Sample Projects Data
const SAMPLE_PROJECTS = [
    {
        id: 1,
        projectName: 'Mobile Banking App',
        projectCode: 'MBA-2024',
        description: 'Next-generation mobile banking application with AI features',
        clientName: 'National Bank Corp',
        startDate: '2024-01-15',
        expectedEndDate: '2024-12-31',
        status: 'ACTIVE',
        priority: 'HIGH',
        plannedBudget: 500000,
        actualCost: 320000,
        completionPercentage: 65,
        healthScore: 75,
        healthStatus: 'HEALTHY'
    },
    {
        id: 2,
        projectName: 'E-Commerce Platform',
        projectCode: 'ECP-2024',
        description: 'Scalable e-commerce platform with microservices architecture',
        clientName: 'Retail Solutions Inc',
        startDate: '2024-02-01',
        expectedEndDate: '2024-11-30',
        status: 'ACTIVE',
        priority: 'HIGH',
        plannedBudget: 750000,
        actualCost: 480000,
        completionPercentage: 55,
        healthScore: 68,
        healthStatus: 'WARNING'
    },
    {
        id: 3,
        projectName: 'Healthcare Management System',
        projectCode: 'HMS-2024',
        description: 'Comprehensive hospital management and patient care system',
        clientName: 'HealthCare Plus',
        startDate: '2024-03-10',
        expectedEndDate: '2025-02-28',
        status: 'ACTIVE',
        priority: 'MEDIUM',
        plannedBudget: 450000,
        actualCost: 180000,
        completionPercentage: 40,
        healthScore: 82,
        healthStatus: 'HEALTHY'
    },
    {
        id: 4,
        projectName: 'Inventory Management System',
        projectCode: 'IMS-2023',
        description: 'Cloud-based inventory tracking and management solution',
        clientName: 'Logistics Global',
        startDate: '2023-09-01',
        expectedEndDate: '2024-08-31',
        status: 'COMPLETED',
        priority: 'MEDIUM',
        plannedBudget: 300000,
        actualCost: 295000,
        completionPercentage: 100,
        healthScore: 95,
        healthStatus: 'HEALTHY'
    },
    {
        id: 5,
        projectName: 'AI Chatbot Integration',
        projectCode: 'AIC-2024',
        description: 'Customer service chatbot with natural language processing',
        clientName: 'TechCorp Solutions',
        startDate: '2024-04-15',
        expectedEndDate: '2024-10-15',
        status: 'PLANNED',
        priority: 'LOW',
        plannedBudget: 200000,
        actualCost: 50000,
        completionPercentage: 15,
        healthScore: 88,
        healthStatus: 'HEALTHY'
    }
];

// Load all projects
async function loadProjects() {
    try {
        const response = await fetch(`${API_BASE}/projects`);
        const result = await response.json();
        
        if (result.success) {
            displayProjects(result.data);
        }
    } catch (error) {
        console.error('Backend not available, using sample data:', error);
        // Use sample data if backend is not available
        displayProjects(SAMPLE_PROJECTS);
    }
}

// Display projects
function displayProjects(projects) {
    const container = document.getElementById('projectsContainer');
    
    if (projects.length === 0) {
        container.innerHTML = `
            <div class="text-center py-5 animate__animated animate__fadeIn">
                <i class="bi bi-folder" style="font-size: 4rem; color: #cbd5e1;"></i>
                <p class="mt-3 text-muted">No projects found. Create your first project!</p>
            </div>
        `;
        return;
    }
    
    container.innerHTML = projects.map((project, index) => {
        const projId = project.id || project.projectId;
        const statusClass = (project.status || 'PLANNED').toLowerCase().replace('_', '-');
        const budgetUtil = (project.plannedBudget && project.plannedBudget > 0) ? 
            (((project.actualCost || 0) / project.plannedBudget) * 100).toFixed(1) : '0.0';
        const budgetColor = budgetUtil > 100 ? '#ef4444' : budgetUtil > 80 ? '#f59e0b' : '#10b981';
        
        return `
            <div class="project-card status-${statusClass} animate__animated animate__fadeInUp" style="animation-delay: ${index * 0.1}s;">
                <div class="project-header">
                    <div>
                        <div class="project-title">${project.projectName} <small class="text-muted">(${project.projectCode || 'NO-CODE'})</small></div>
                        <span class="status-badge status-${statusClass}">${formatStatus(project.status || 'PLANNED')}</span>
                    </div>
                    <div class="project-actions">
                        <button class="btn-action btn-edit" onclick="editProject(${projId})">
                            <i class="bi bi-pencil"></i> Edit
                        </button>
                        <button class="btn-action btn-delete" onclick="deleteProject(${projId})">
                            <i class="bi bi-trash"></i> Delete
                        </button>
                    </div>
                </div>
                
                <p class="project-description">${project.description || 'No description provided'}</p>
                
                <div class="project-meta">
                    <div class="meta-item">
                        <i class="bi bi-calendar"></i>
                        <span>${formatDate(project.startDate)} - ${formatDate(project.expectedEndDate || project.endDate)}</span>
                    </div>
                    <div class="meta-item">
                        <i class="bi bi-cash"></i>
                        <span>Budget: $${(project.plannedBudget || 0).toLocaleString()} (${budgetUtil}% used)</span>
                    </div>
                    <div class="meta-item">
                        <i class="bi bi-exclamation-triangle"></i>
                        <span>${project.risks ? project.risks.length : 0} Risks</span>
                    </div>
                </div>
                
                <div class="progress-section">
                    <div class="progress-label">
                        <span>Project Progress</span>
                        <span>${project.completionPercentage || 0}%</span>
                    </div>
                    <div class="progress">
                        <div class="progress-bar" style="width: ${project.completionPercentage || 0}%"></div>
                    </div>
                </div>
                
                <div class="progress-section mt-3">
                    <div class="progress-label">
                        <span>Budget Utilization</span>
                        <span style="color: ${budgetColor}">${budgetUtil}%</span>
                    </div>
                    <div class="progress">
                        <div class="progress-bar" style="width: ${Math.min(budgetUtil, 100)}%; background: ${budgetColor};"></div>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

// Format status
function formatStatus(status) {
    if (!status) return 'Planned';
    return status.replace('_', ' ').split(' ').map(word => 
        word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()
    ).join(' ');
}

// Format date
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
}

// Show add modal
function showAddModal() {
    document.getElementById('modalTitle').textContent = 'Add New Project';
    document.getElementById('projectForm').reset();
    document.getElementById('projectId').value = '';
    new bootstrap.Modal(document.getElementById('projectModal')).show();
}

// Edit project
async function editProject(id) {
    try {
        const response = await fetch(`${API_BASE}/projects/${id}`);
        const result = await response.json();
        
        if (result.success) {
            const project = result.data;
            document.getElementById('modalTitle').textContent = 'Edit Project';
            document.getElementById('projectId').value = project.id || project.projectId;
            document.getElementById('projectName').value = project.projectName;
            document.getElementById('projectCode').value = project.projectCode || '';
            document.getElementById('description').value = project.description || '';
            document.getElementById('status').value = project.status || 'PLANNED';
            document.getElementById('startDate').value = project.startDate || '';
            document.getElementById('endDate').value = project.expectedEndDate || project.endDate || '';
            document.getElementById('completionPercentage').value = project.completionPercentage || 0;
            document.getElementById('plannedBudget').value = project.plannedBudget || 0;
            document.getElementById('actualCost').value = project.actualCost || 0;
            
            new bootstrap.Modal(document.getElementById('projectModal')).show();
        } else {
            alert('Error loading project: ' + result.message);
        }
    } catch (error) {
        console.error('Error loading project:', error);
        alert('Error loading project details. Is backend running?');
    }
}

// Delete project
async function deleteProject(id) {
    if (!confirm('Are you sure you want to delete this project? This action cannot be undone.')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/projects/${id}`, {
            method: 'DELETE'
        });
        
        const result = await response.json();
        
        if (result.success) {
            alert('Project deleted successfully!');
            loadProjects();
        } else {
            alert('Error deleting project: ' + result.message);
        }
    } catch (error) {
        console.error('Error deleting project:', error);
        alert('Error deleting project');
    }
}

// Handle form submission
document.getElementById('projectForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const projectId = document.getElementById('projectId').value;
    const projectData = {
        projectName: document.getElementById('projectName').value,
        projectCode: document.getElementById('projectCode').value || ('PROJ-' + Date.now()),
        description: document.getElementById('description').value,
        status: document.getElementById('status').value,
        startDate: document.getElementById('startDate').value,
        expectedEndDate: document.getElementById('endDate').value,
        completionPercentage: parseInt(document.getElementById('completionPercentage').value) || 0,
        plannedBudget: parseFloat(document.getElementById('plannedBudget').value) || 0,
        actualCost: parseFloat(document.getElementById('actualCost').value) || 0
    };
    
    console.log('Sending project data:', projectData);
    
    try {
        const url = projectId ? `${API_BASE}/projects/${projectId}` : `${API_BASE}/projects`;
        const method = projectId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(projectData)
        });
        
        console.log('Response status:', response.status);
        
        if (!response.ok) {
            const errorText = await response.text();
            console.error('Error response:', errorText);
            alert('Error saving project: ' + (errorText || 'Backend error'));
            return;
        }
        
        const result = await response.json();
        console.log('Result:', result);
        
        if (result.success) {
            // Show success message
            showSuccessMessage(projectId ? 'Project updated successfully!' : 'Project added successfully!');
            bootstrap.Modal.getInstance(document.getElementById('projectModal')).hide();
            loadProjects();
        } else {
            alert('Error saving project: ' + (result.message || 'Unknown error'));
        }
    } catch (error) {
        console.error('Error saving project:', error);
        alert('Error saving project: Backend not responding. Is it running on port 8080?');
    }
});

// Toggle sidebar
function toggleSidebar() {
    document.getElementById('sidebar').classList.toggle('active');
}

// Show success message
function showSuccessMessage(message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-success position-fixed top-0 start-50 translate-middle-x mt-3';
    alertDiv.style.zIndex = '9999';
    alertDiv.style.minWidth = '300px';
    alertDiv.style.animation = 'slideDown 0.3s ease-out';
    alertDiv.innerHTML = `
        <i class="bi bi-check-circle-fill me-2"></i>
        <strong>${message}</strong>
    `;
    
    document.body.appendChild(alertDiv);
    
    setTimeout(() => {
        alertDiv.style.animation = 'slideUp 0.3s ease-out';
        setTimeout(() => alertDiv.remove(), 300);
    }, 3000);
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    loadProjects();
});
