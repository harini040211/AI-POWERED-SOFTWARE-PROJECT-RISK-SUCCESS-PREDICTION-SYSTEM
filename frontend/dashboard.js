// API Base URL
const API_BASE = 'http://localhost:8080/api';

// Load dashboard data
async function loadDashboard() {
    try {
        const response = await fetch(`${API_BASE}/analytics/dashboard`);
        const result = await response.json();
        
        if (result.success) {
            const data = result.data;
            
            // Update KPIs
            document.getElementById('totalProjects').textContent = data.totalProjects || 0;
            document.getElementById('activeProjects').textContent = data.activeProjects || 0;
            document.getElementById('highRisks').textContent = data.highRisks || 0;
            document.getElementById('avgRiskScore').textContent = 
                data.averageRiskScore ? data.averageRiskScore.toFixed(1) : '0.0';
            document.getElementById('projectHealth').textContent = 
                `${data.projectHealthScore || 0}/100`;
            document.getElementById('resourceUtil').textContent = 
                data.resourceUtilization ? `${data.resourceUtilization}%` : '0%';
            
            // Load charts
            loadRiskDistributionChart(data.riskDistribution);
            loadRiskCategoryChart(data.riskByCategory);
            loadTimelineChart();
            
            // Load alerts
            loadAlerts();
            
            // Load activity
            loadRecentActivity();
        }
    } catch (error) {
        console.error('Error loading dashboard:', error);
    }
}

// Risk Distribution Chart
function loadRiskDistributionChart(data) {
    const ctx = document.getElementById('riskDistChart').getContext('2d');
    
    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Low Risk', 'Medium Risk', 'High Risk'],
            datasets: [{
                data: [
                    data?.LOW || 0,
                    data?.MEDIUM || 0,
                    data?.HIGH || 0
                ],
                backgroundColor: [
                    'rgba(16, 185, 129, 0.8)',
                    'rgba(245, 158, 11, 0.8)',
                    'rgba(239, 68, 68, 0.8)'
                ],
                borderWidth: 0,
                hoverOffset: 10
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 20,
                        font: {
                            size: 12,
                            weight: '500'
                        }
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    padding: 12,
                    titleFont: {
                        size: 14,
                        weight: 'bold'
                    },
                    bodyFont: {
                        size: 13
                    },
                    borderColor: 'rgba(255, 255, 255, 0.1)',
                    borderWidth: 1
                }
            }
        }
    });
}

// Risk Category Chart
function loadRiskCategoryChart(data) {
    const ctx = document.getElementById('riskCategoryChart').getContext('2d');
    
    const labels = Object.keys(data || {});
    const values = Object.values(data || {});
    
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Number of Risks',
                data: values,
                backgroundColor: 'rgba(102, 126, 234, 0.8)',
                borderRadius: 10,
                hoverBackgroundColor: 'rgba(118, 75, 162, 0.9)'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    padding: 12,
                    borderColor: 'rgba(255, 255, 255, 0.1)',
                    borderWidth: 1
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1,
                        font: {
                            size: 12
                        }
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    }
                },
                x: {
                    grid: {
                        display: false
                    },
                    ticks: {
                        font: {
                            size: 12
                        }
                    }
                }
            }
        }
    });
}

// Timeline Chart
async function loadTimelineChart() {
    try {
        const response = await fetch(`${API_BASE}/projects`);
        const result = await response.json();
        
        if (result.success && result.data.length > 0) {
            const projects = result.data.slice(0, 5); // Top 5 projects
            
            const ctx = document.getElementById('timelineChart').getContext('2d');
            
            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: projects.map(p => p.projectName),
                    datasets: [{
                        label: 'Completion %',
                        data: projects.map(p => p.completionPercentage),
                        borderColor: 'rgba(102, 126, 234, 1)',
                        backgroundColor: 'rgba(102, 126, 234, 0.1)',
                        borderWidth: 3,
                        fill: true,
                        tension: 0.4,
                        pointBackgroundColor: 'rgba(102, 126, 234, 1)',
                        pointBorderColor: '#fff',
                        pointBorderWidth: 2,
                        pointRadius: 6,
                        pointHoverRadius: 8
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            display: true,
                            position: 'top',
                            labels: {
                                font: {
                                    size: 12,
                                    weight: '500'
                                }
                            }
                        },
                        tooltip: {
                            backgroundColor: 'rgba(0, 0, 0, 0.8)',
                            padding: 12,
                            borderColor: 'rgba(255, 255, 255, 0.1)',
                            borderWidth: 1
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            max: 100,
                            ticks: {
                                callback: function(value) {
                                    return value + '%';
                                },
                                font: {
                                    size: 12
                                }
                            },
                            grid: {
                                color: 'rgba(0, 0, 0, 0.05)'
                            }
                        },
                        x: {
                            grid: {
                                display: false
                            },
                            ticks: {
                                font: {
                                    size: 12
                                }
                            }
                        }
                    }
                }
            });
        }
    } catch (error) {
        console.error('Error loading timeline chart:', error);
    }
}

// Load alerts
async function loadAlerts() {
    try {
        const [projects, resources, risks] = await Promise.all([
            fetch(`${API_BASE}/projects`).then(r => r.json()),
            fetch(`${API_BASE}/resources/overloaded`).then(r => r.json()),
            fetch(`${API_BASE}/risks/high-severity`).then(r => r.json())
        ]);
        
        const alertsContainer = document.getElementById('alertsContainer');
        alertsContainer.innerHTML = '';
        
        let hasAlerts = false;
        
        // High risk alerts
        if (risks.success && risks.data.length > 0) {
            hasAlerts = true;
            alertsContainer.innerHTML += `
                <div class="alert-item alert-danger animate__animated animate__fadeInLeft">
                    <div class="alert-icon">
                        <i class="bi bi-exclamation-triangle-fill"></i>
                    </div>
                    <div class="alert-content">
                        <div class="alert-title">Critical Risk Detected</div>
                        <div class="alert-description">
                            ${risks.data.length} high-severity risk(s) require immediate attention
                        </div>
                    </div>
                    <a href="risks.html" class="alert-action">View Details →</a>
                </div>
            `;
        }
        
        // Overloaded resources
        if (resources.success && resources.data.length > 0) {
            hasAlerts = true;
            alertsContainer.innerHTML += `
                <div class="alert-item alert-warning animate__animated animate__fadeInLeft" style="animation-delay: 0.1s;">
                    <div class="alert-icon">
                        <i class="bi bi-people-fill"></i>
                    </div>
                    <div class="alert-content">
                        <div class="alert-title">Resource Overload Warning</div>
                        <div class="alert-description">
                            ${resources.data.length} team member(s) are overallocated and need rebalancing
                        </div>
                    </div>
                    <a href="resources.html" class="alert-action">View Details →</a>
                </div>
            `;
        }
        
        // Budget warnings
        if (projects.success) {
            const overBudget = projects.data.filter(p => {
                const util = (p.actualCost / p.plannedBudget) * 100;
                return util > 90;
            });
            
            if (overBudget.length > 0) {
                hasAlerts = true;
                alertsContainer.innerHTML += `
                    <div class="alert-item alert-warning animate__animated animate__fadeInLeft" style="animation-delay: 0.2s;">
                        <div class="alert-icon">
                            <i class="bi bi-cash-stack"></i>
                        </div>
                        <div class="alert-content">
                            <div class="alert-title">Budget Alert</div>
                            <div class="alert-description">
                                ${overBudget.length} project(s) approaching or exceeding budget limits
                            </div>
                        </div>
                        <a href="projects.html" class="alert-action">View Details →</a>
                    </div>
                `;
            }
        }
        
        if (!hasAlerts) {
            alertsContainer.innerHTML = `
                <div class="alert-item alert-success animate__animated animate__fadeInLeft">
                    <div class="alert-icon">
                        <i class="bi bi-check-circle-fill"></i>
                    </div>
                    <div class="alert-content">
                        <div class="alert-title">All Systems Normal</div>
                        <div class="alert-description">
                            No critical alerts at this time. All projects are on track.
                        </div>
                    </div>
                </div>
            `;
        }
    } catch (error) {
        console.error('Error loading alerts:', error);
    }
}

// Load recent activity
async function loadRecentActivity() {
    const activities = [
        {
            time: '2 hours ago',
            title: 'New risk identified',
            description: 'High-priority risk added to E-Commerce Platform project'
        },
        {
            time: '5 hours ago',
            title: 'Project milestone completed',
            description: 'Mobile Banking App reached 60% completion'
        },
        {
            time: '1 day ago',
            title: 'Resource allocation updated',
            description: 'David Wilson assigned to new development task'
        },
        {
            time: '2 days ago',
            title: 'Budget review completed',
            description: 'Q3 budget analysis report generated'
        },
        {
            time: '3 days ago',
            title: 'Team member added',
            description: 'New developer joined the E-Commerce Platform team'
        }
    ];
    
    const container = document.getElementById('activityTimeline');
    container.innerHTML = activities.map(activity => `
        <div class="activity-item">
            <div class="activity-time">${activity.time}</div>
            <div class="activity-content">
                <div class="activity-title">${activity.title}</div>
                <div class="activity-description">${activity.description}</div>
            </div>
        </div>
    `).join('');
}

// Toggle sidebar
function toggleSidebar() {
    document.getElementById('sidebar').classList.toggle('active');
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    loadDashboard();
});
