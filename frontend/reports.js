const API_BASE = 'http://localhost:8080/api';

async function generateReport(type) {
    try {
        let endpoint = '';
        let filename = '';
        
        switch(type) {
            case 'executive':
                endpoint = '/reports/executive-summary';
                filename = 'executive-summary-' + Date.now() + '.pdf';
                break;
            case 'risk':
                endpoint = '/reports/risk-assessment';
                filename = 'risk-assessment-' + Date.now() + '.pdf';
                break;
            case 'resource':
                endpoint = '/reports/resource-utilization';
                filename = 'resource-utilization-' + Date.now() + '.pdf';
                break;
            case 'financial':
                endpoint = '/reports/financial-analysis';
                filename = 'financial-analysis-' + Date.now() + '.pdf';
                break;
        }
        
        // Show loading
        const btn = event.target.closest('button');
        const originalHTML = btn.innerHTML;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Generating...';
        btn.disabled = true;
        
        try {
            const response = await fetch(`${API_BASE}${endpoint}`, {
                method: 'GET',
                headers: { 
                    'Accept': 'application/pdf',
                    'Content-Type': 'application/json'
                }
            });
            
            if (response.ok) {
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = filename;
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);
                
                // Add to recent reports
                addToRecentReports(filename, type);
                
                // Show success message
                showToast('Report generated successfully!', 'success');
            } else {
                const errorText = await response.text();
                console.error('Report generation failed:', errorText);
                showToast('Error generating report. Please ensure backend is running.', 'danger');
            }
        } catch (fetchError) {
            console.error('Fetch error:', fetchError);
            showToast('Backend not responding. Please start the backend server.', 'danger');
        }
        
        // Restore button
        btn.innerHTML = originalHTML;
        btn.disabled = false;
        
    } catch (error) {
        console.error('Error generating report:', error);
        showToast('Error generating report. Please try again.', 'danger');
        if (event && event.target) {
            event.target.closest('button').innerHTML = '<i class="bi bi-download"></i> Generate PDF';
            event.target.closest('button').disabled = false;
        }
    }
}

function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `alert alert-${type} position-fixed top-0 end-0 m-3`;
    toast.style.zIndex = '9999';
    toast.style.minWidth = '300px';
    toast.innerHTML = `
        <div class="d-flex align-items-center">
            <i class="bi bi-${type === 'success' ? 'check-circle' : type === 'danger' ? 'x-circle' : 'info-circle'} me-2"></i>
            <span>${message}</span>
        </div>
    `;
    document.body.appendChild(toast);
    
    setTimeout(() => {
        toast.remove();
    }, 4000);
}

function addToRecentReports(filename, type) {
    const reports = JSON.parse(localStorage.getItem('recentReports') || '[]');
    reports.unshift({
        filename: filename,
        type: type,
        date: new Date().toISOString()
    });
    
    // Keep only last 10
    if (reports.length > 10) {
        reports.pop();
    }
    
    localStorage.setItem('recentReports', JSON.stringify(reports));
    displayRecentReports();
}

function displayRecentReports() {
    const reports = JSON.parse(localStorage.getItem('recentReports') || '[]');
    const container = document.getElementById('recentReports');
    
    if (reports.length === 0) {
        container.innerHTML = '<p class="text-muted">No reports generated yet. Click the buttons above to create your first report.</p>';
        return;
    }
    
    container.innerHTML = `
        <div class="table-responsive">
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th>Report Name</th>
                        <th>Type</th>
                        <th>Generated On</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    ${reports.map(report => `
                        <tr>
                            <td><i class="bi bi-file-earmark-pdf text-danger me-2"></i>${report.filename}</td>
                            <td><span class="badge bg-primary">${formatReportType(report.type)}</span></td>
                            <td>${new Date(report.date).toLocaleString()}</td>
                            <td>
                                <button class="btn btn-sm btn-outline-primary" onclick="regenerateReport('${report.type}')">
                                    <i class="bi bi-arrow-clockwise"></i> Regenerate
                                </button>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </div>
    `;
}

function formatReportType(type) {
    const types = {
        'executive': 'Executive Summary',
        'risk': 'Risk Assessment',
        'resource': 'Resource Utilization',
        'financial': 'Financial Analysis'
    };
    return types[type] || type;
}

function regenerateReport(type) {
    generateReport(type);
}

function toggleSidebar() {
    document.getElementById('sidebar').classList.toggle('active');
}

document.addEventListener('DOMContentLoaded', function() {
    displayRecentReports();
});
