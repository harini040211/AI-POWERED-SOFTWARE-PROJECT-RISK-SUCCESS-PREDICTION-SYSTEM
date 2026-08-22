const API_BASE = 'http://localhost:8080/api';

async function loadRisks() {
    try {
        const response = await fetch(`${API_BASE}/risks`);
        const result = await response.json();
        if (result.success) {
            displayRisks(result.data);
        }
    } catch (error) {
        console.error('Error loading risks:', error);
    }
}

function displayRisks(risks) {
    const container = document.getElementById('risksContainer');
    if (!risks || risks.length === 0) {
        container.innerHTML = `<div class="text-center py-5"><i class="bi bi-exclamation-triangle" style="font-size: 4rem; color: #cbd5e1;"></i><p class="mt-3 text-muted">No risks found. Create your first risk assessment!</p></div>`;
        return;
    }
    container.innerHTML = risks.map((risk, index) => {
        const severityClass = (risk.riskLevel || risk.severity || 'MEDIUM').toLowerCase();
        const score = risk.riskScore || (risk.probability * risk.impact) || 0;
        const riskTitle = risk.title || risk.riskTitle || 'Untitled Risk';
        const riskId = risk.id || risk.riskId;
        return `
            <div class="risk-card severity-${severityClass} animate__animated animate__fadeInUp" style="animation-delay: ${index * 0.1}s;">
                <div class="d-flex justify-content-between align-items-start mb-3">
                    <div class="flex-grow-1">
                        <div class="d-flex align-items-center gap-3 mb-2">
                            <div class="risk-score ${severityClass}">${score}</div>
                            <div>
                                <h4 class="mb-1">${riskTitle}</h4>
                                <span class="severity-badge severity-${severityClass}">${risk.riskLevel || risk.severity}</span>
                                <span class="ms-2 text-muted"><i class="bi bi-folder"></i> ${risk.project?.projectName || 'No Project'}</span>
                            </div>
                        </div>
                    </div>
                    <div class="d-flex gap-2">
                        <button class="btn-action btn-edit" onclick="editRisk(${riskId})"><i class="bi bi-pencil"></i> Edit</button>
                        <button class="btn-action btn-delete" onclick="deleteRisk(${riskId})"><i class="bi bi-trash"></i> Delete</button>
                    </div>
                </div>
                <p class="text-muted mb-3">${risk.description || 'No description provided'}</p>
                <div class="row">
                    <div class="col-md-3"><strong>Category:</strong> ${risk.category}</div>
                    <div class="col-md-3"><strong>Status:</strong> ${risk.status}</div>
                    <div class="col-md-3"><strong>Probability:</strong> ${risk.probability}/5</div>
                    <div class="col-md-3"><strong>Impact:</strong> ${risk.impact}/5</div>
                </div>
                ${(risk.mitigation || risk.mitigationStrategy) ? `<div class="mt-3 p-3 bg-light rounded"><strong>Mitigation:</strong> ${risk.mitigation || risk.mitigationStrategy}</div>` : ''}
                ${risk.aiRecommendation ? `<div class="mt-2 p-3 bg-white border rounded"><strong><i class="bi bi-robot text-primary me-1"></i>AI Recommendation:</strong> ${risk.aiRecommendation}</div>` : ''}
            </div>
        `;
    }).join('');
}

async function loadProjects() {
    try {
        const response = await fetch(`${API_BASE}/projects`);
        const result = await response.json();
        if (result.success) {
            const select = document.getElementById('projectId');
            select.innerHTML = '<option value="">Select Project</option>' + 
                result.data.map(p => `<option value="${p.id || p.projectId}">${p.projectName}</option>`).join('');
        }
    } catch (error) {
        console.error('Error loading projects:', error);
    }
}

function showAddModal() {
    document.getElementById('modalTitle').textContent = 'Add New Risk';
    document.getElementById('riskForm').reset();
    document.getElementById('riskId').value = '';
    loadProjects();
    new bootstrap.Modal(document.getElementById('riskModal')).show();
}

async function editRisk(id) {
    try {
        const response = await fetch(`${API_BASE}/risks/${id}`);
        const result = await response.json();
        if (result.success) {
            const risk = result.data;
            document.getElementById('modalTitle').textContent = 'Edit Risk';
            document.getElementById('riskId').value = risk.id || risk.riskId;
            document.getElementById('riskTitle').value = risk.title || risk.riskTitle;
            document.getElementById('description').value = risk.description || '';
            document.getElementById('category').value = risk.category;
            document.getElementById('severity').value = risk.riskLevel || risk.severity;
            document.getElementById('status').value = risk.status;
            document.getElementById('probability').value = risk.probability;
            document.getElementById('impact').value = risk.impact;
            document.getElementById('mitigationStrategy').value = risk.mitigation || risk.mitigationStrategy || '';
            await loadProjects();
            document.getElementById('projectId').value = risk.project?.id || risk.project?.projectId || '';
            new bootstrap.Modal(document.getElementById('riskModal')).show();
        } else {
            alert('Error loading risk: ' + result.message);
        }
    } catch (error) {
        console.error('Error loading risk:', error);
        alert('Error loading risk details. Is backend running?');
    }
}

async function deleteRisk(id) {
    if (!confirm('Are you sure you want to delete this risk?')) return;
    try {
        const response = await fetch(`${API_BASE}/risks/${id}`, { method: 'DELETE' });
        const result = await response.json();
        if (result.success) {
            alert('Risk deleted successfully!');
            loadRisks();
        } else {
            alert('Error deleting risk: ' + result.message);
        }
    } catch (error) {
        console.error('Error deleting risk:', error);
        alert('Error deleting risk');
    }
}

document.getElementById('riskForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const riskId = document.getElementById('riskId').value;
    const projectId = document.getElementById('projectId').value;
    
    // Validation
    if (!projectId) {
        alert('Please select a project!');
        return;
    }
    
    const riskData = {
        title: document.getElementById('riskTitle').value,
        description: document.getElementById('description').value,
        category: document.getElementById('category').value,
        status: document.getElementById('status').value,
        probability: parseInt(document.getElementById('probability').value),
        impact: parseInt(document.getElementById('impact').value),
        mitigation: document.getElementById('mitigationStrategy').value
    };
    
    console.log('Sending risk data:', riskData);
    
    try {
        const url = riskId ? `${API_BASE}/risks/${riskId}` : `${API_BASE}/risks/project/${projectId}`;
        const method = riskId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: { 
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(riskData)
        });
        
        console.log('Response status:', response.status);
        
        if (!response.ok) {
            const errorText = await response.text();
            console.error('Error response:', errorText);
            alert('Error saving risk: ' + (errorText || 'Backend error'));
            return;
        }
        
        const result = await response.json();
        console.log('Result:', result);
        
        if (result.success) {
            showSuccessMessage(riskId ? 'Risk updated successfully!' : 'Risk added successfully!');
            bootstrap.Modal.getInstance(document.getElementById('riskModal')).hide();
            loadRisks();
        } else {
            alert('Error saving risk: ' + (result.message || 'Unknown error'));
        }
    } catch (error) {
        console.error('Error saving risk:', error);
        alert('Error saving risk: Backend not responding. Is it running on port 8080?');
    }
});

function toggleSidebar() {
    document.getElementById('sidebar').classList.toggle('active');
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

document.addEventListener('DOMContentLoaded', function() {
    loadRisks();
});
