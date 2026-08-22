const API_BASE = 'http://localhost:8080/api';

async function loadAnalytics() {
    try {
        const [dashboard, risks, resources, projects] = await Promise.all([
            fetch(`${API_BASE}/analytics/dashboard`).then(r => r.json()),
            fetch(`${API_BASE}/risks`).then(r => r.json()),
            fetch(`${API_BASE}/resources`).then(r => r.json()),
            fetch(`${API_BASE}/projects`).then(r => r.json())
        ]);

        if (dashboard.success) {
            loadSeverityChart(dashboard.data.riskDistribution);
            loadCategoryChart(dashboard.data.riskByCategory);
        }

        if (risks.success) {
            loadTrendsChart(risks.data);
        }

        if (resources.success) {
            loadResourceChart(resources.data);
        }

        if (projects.success) {
            loadBudgetChart(projects.data);
        }

        loadPredictions();
    } catch (error) {
        console.error('Error loading analytics:', error);
    }
}

function loadSeverityChart(data) {
    const ctx = document.getElementById('severityChart').getContext('2d');
    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Low Risk', 'Medium Risk', 'High Risk'],
            datasets: [{
                data: [data?.LOW || 0, data?.MEDIUM || 0, data?.HIGH || 0],
                backgroundColor: ['rgba(16, 185, 129, 0.8)', 'rgba(245, 158, 11, 0.8)', 'rgba(239, 68, 68, 0.8)'],
                borderWidth: 0,
                hoverOffset: 10
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { position: 'bottom', labels: { padding: 20, font: { size: 12, weight: '500' } } }
            }
        }
    });
}

function loadCategoryChart(data) {
    const ctx = document.getElementById('categoryChart').getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: Object.keys(data || {}),
            datasets: [{
                label: 'Number of Risks',
                data: Object.values(data || {}),
                backgroundColor: 'rgba(102, 126, 234, 0.8)',
                borderRadius: 10
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                y: { beginAtZero: true, ticks: { stepSize: 1 } }
            }
        }
    });
}

function loadTrendsChart(risks) {
    const ctx = document.getElementById('trendsChart').getContext('2d');
    const monthlyData = {};
    risks.forEach(risk => {
        const date = new Date(risk.identifiedDate || Date.now());
        const month = date.toLocaleDateString('en-US', { year: 'numeric', month: 'short' });
        monthlyData[month] = (monthlyData[month] || 0) + 1;
    });
    
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: Object.keys(monthlyData),
            datasets: [{
                label: 'Risks Identified',
                data: Object.values(monthlyData),
                borderColor: 'rgba(102, 126, 234, 1)',
                backgroundColor: 'rgba(102, 126, 234, 0.1)',
                borderWidth: 3,
                fill: true,
                tension: 0.4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: true } },
            scales: { y: { beginAtZero: true } }
        }
    });
}

function loadResourceChart(resources) {
    const ctx = document.getElementById('resourceChart').getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: resources.slice(0, 10).map(r => r.employeeName || 'Resource'),
            datasets: [{
                label: 'Workload %',
                data: resources.slice(0, 10).map(r => r.utilizationPercentage || 0),
                backgroundColor: resources.slice(0, 10).map(r => 
                    (r.utilizationPercentage || 0) > 90 ? 'rgba(239, 68, 68, 0.8)' : 
                    (r.utilizationPercentage || 0) > 70 ? 'rgba(245, 158, 11, 0.8)' : 
                    'rgba(16, 185, 129, 0.8)'
                ),
                borderRadius: 10
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                y: { beginAtZero: true }
            }
        }
    });
}

function loadBudgetChart(projects) {
    const ctx = document.getElementById('budgetChart').getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: projects.slice(0, 5).map(p => p.projectName),
            datasets: [
                {
                    label: 'Planned Budget',
                    data: projects.slice(0, 5).map(p => p.plannedBudget || 0),
                    backgroundColor: 'rgba(102, 126, 234, 0.6)',
                    borderRadius: 10
                },
                {
                    label: 'Actual Cost',
                    data: projects.slice(0, 5).map(p => p.actualCost || 0),
                    backgroundColor: 'rgba(239, 68, 68, 0.6)',
                    borderRadius: 10
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { position: 'top' } },
            scales: { y: { beginAtZero: true } }
        }
    });
}

async function loadPredictions() {
    try {
        const projResp = await fetch(`${API_BASE}/projects`);
        const projResult = await projResp.json();
        
        const container = document.getElementById('predictionsContainer');
        if (projResult.success && projResult.data.length > 0) {
            const predictions = [];
            for (const proj of projResult.data) {
                try {
                    const predResp = await fetch(`${API_BASE}/projects/${proj.id || proj.projectId}/predict-risk`);
                    const predResult = await predResp.json();
                    if (predResult.success) {
                        predictions.push({
                            projectName: proj.projectName,
                            riskLevel: predResult.data.predictedRiskLevel,
                            probability: predResult.data.predictionProbability,
                            explanation: predResult.data.explanation
                        });
                    }
                } catch (e) {
                    console.error('Error predicting for project:', proj.id, e);
                }
            }
            
            const elevatedRisks = predictions.filter(p => p.riskLevel === 'HIGH' || p.riskLevel === 'MEDIUM');
            if (elevatedRisks.length > 0) {
                container.innerHTML = `
                    <div class="alert alert-warning">
                        <h5><i class="bi bi-robot"></i> Early Warning Risk Predictions</h5>
                        <p>ML Heuristic Model identified ${elevatedRisks.length} project(s) with elevated risk indicators:</p>
                        <ul class="mb-0">
                            ${elevatedRisks.map(pred => `
                                <li><strong>${pred.projectName}</strong> - Predicted Level: <span class="badge ${pred.riskLevel === 'HIGH' ? 'bg-danger' : 'bg-warning'}">${pred.riskLevel}</span> (${pred.probability.toFixed(1)}% probability)<br><small class="text-muted">${pred.explanation}</small></li>
                            `).join('')}
                        </ul>
                    </div>
                `;
            } else {
                container.innerHTML = `
                    <div class="alert alert-success">
                        <h5><i class="bi bi-check-circle"></i> All Systems Healthy</h5>
                        <p class="mb-0">ML Risk Prediction Model shows low risk across all active projects.</p>
                    </div>
                `;
            }
        } else {
            container.innerHTML = `
                <div class="alert alert-info">
                    <p class="mb-0">No projects available for risk prediction.</p>
                </div>
            `;
        }
    } catch (error) {
        console.error('Error loading predictions:', error);
        document.getElementById('predictionsContainer').innerHTML = `
            <div class="alert alert-info">
                <p class="mb-0">Risk predictions unavailable. Ensure backend server is running.</p>
            </div>
        `;
    }
}

function toggleSidebar() {
    document.getElementById('sidebar').classList.toggle('active');
}

document.addEventListener('DOMContentLoaded', function() {
    loadAnalytics();
});
