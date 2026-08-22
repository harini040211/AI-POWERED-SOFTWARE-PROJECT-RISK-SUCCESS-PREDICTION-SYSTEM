package com.risktracker.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.risktracker.model.Project;
import com.risktracker.model.Risk;
import com.risktracker.model.Resource;
import com.risktracker.repository.ProjectRepository;
import com.risktracker.repository.RiskRepository;
import com.risktracker.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Report Service
 * 
 * Generates PDF reports using OpenPDF library
 */
@Service
public class ReportService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private RiskRepository riskRepository;
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    /**
     * Generate comprehensive project risk report
     */
    public byte[] generateProjectReport(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        List<Risk> risks = project.getRisks();
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            
            document.open();
            
            // Title
            addTitle(document, "Project Risk Report");
            addEmptyLine(document, 1);
            
            // Generation info
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
            Paragraph generated = new Paragraph("Generated: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), smallFont);
            generated.setAlignment(Element.ALIGN_RIGHT);
            document.add(generated);
            addEmptyLine(document, 1);
            
            // Project Information
            addSectionHeader(document, "Project Information");
            addProjectInfo(document, project);
            addEmptyLine(document, 1);
            
            // Project Health
            addSectionHeader(document, "Project Health Summary");
            addHealthSummary(document, project);
            addEmptyLine(document, 1);
            
            // Budget Summary
            addSectionHeader(document, "Budget Summary");
            addBudgetSummary(document, project);
            addEmptyLine(document, 1);
            
            // Risk Summary
            addSectionHeader(document, "Risk Summary");
            addRiskSummary(document, risks);
            addEmptyLine(document, 1);
            
            // High Priority Risks
            List<Risk> highRisks = risks.stream()
                    .filter(r -> r.getRiskLevel() == Risk.RiskLevel.HIGH)
                    .toList();
            
            if (!highRisks.isEmpty()) {
                addSectionHeader(document, "High Priority Risks");
                addRiskDetails(document, highRisks);
            }
            
            document.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }
    
    private void addTitle(Document document, String title) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLACK);
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        document.add(titlePara);
    }
    
    private void addSectionHeader(Document document, String header) throws DocumentException {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY);
        Paragraph headerPara = new Paragraph(header, headerFont);
        headerPara.setSpacingBefore(10);
        document.add(headerPara);
        document.add(new Paragraph(" "));
    }
    
    private void addProjectInfo(Document document, Project project) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        
        addTableRow(table, "Project Name:", project.getProjectName());
        addTableRow(table, "Project Code:", project.getProjectCode() != null ? project.getProjectCode() : "N/A");
        addTableRow(table, "Client:", project.getClientName() != null ? project.getClientName() : "N/A");
        addTableRow(table, "Status:", project.getStatus() != null ? project.getStatus().toString() : "PLANNED");
        addTableRow(table, "Priority:", project.getPriority() != null ? project.getPriority().toString() : "MEDIUM");
        addTableRow(table, "Completion:", (project.getCompletionPercentage() != null ? project.getCompletionPercentage() : 0) + "%");
        
        document.add(table);
    }
    
    private void addHealthSummary(Document document, Project project) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        
        addTableRow(table, "Health Status:", project.getHealthStatus() != null ? project.getHealthStatus().toString() : "HEALTHY");
        addTableRow(table, "Health Score:", (project.getHealthScore() != null ? project.getHealthScore() : 100) + "/100");
        
        if (project.getPredictedRiskLevel() != null) {
            addTableRow(table, "ML Prediction:", project.getPredictedRiskLevel());
            addTableRow(table, "Prediction Probability:", 
                    String.format("%.1f%%", project.getPredictionProbability() != null ? project.getPredictionProbability() : 0.0));
        }
        
        document.add(table);
    }
    
    private void addBudgetSummary(Document document, Project project) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        
        BigDecimal planned = project.getPlannedBudget() != null ? project.getPlannedBudget() : BigDecimal.ZERO;
        BigDecimal actual = project.getActualCost() != null ? project.getActualCost() : BigDecimal.ZERO;
        
        addTableRow(table, "Planned Budget:", "$" + planned);
        addTableRow(table, "Actual Cost:", "$" + actual);
        addTableRow(table, "Remaining:", "$" + planned.subtract(actual));
        
        document.add(table);
    }
    
    private void addRiskSummary(Document document, List<Risk> risks) throws DocumentException {
        long highRisks = risks.stream().filter(r -> r.getRiskLevel() == Risk.RiskLevel.HIGH).count();
        long mediumRisks = risks.stream().filter(r -> r.getRiskLevel() == Risk.RiskLevel.MEDIUM).count();
        long lowRisks = risks.stream().filter(r -> r.getRiskLevel() == Risk.RiskLevel.LOW).count();
        
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        
        addTableRow(table, "Total Risks:", String.valueOf(risks.size()));
        addTableRow(table, "High Severity:", String.valueOf(highRisks));
        addTableRow(table, "Medium Severity:", String.valueOf(mediumRisks));
        addTableRow(table, "Low Severity:", String.valueOf(lowRisks));
        
        document.add(table);
    }
    
    private void addRiskDetails(Document document, List<Risk> risks) throws DocumentException {
        for (Risk risk : risks) {
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            
            Paragraph riskTitle = new Paragraph(risk.getTitle(), boldFont);
            document.add(riskTitle);
            
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5);
            table.setSpacingAfter(10);
            
            addTableRow(table, "Category:", risk.getCategory() != null ? risk.getCategory().toString() : "GENERAL");
            addTableRow(table, "Risk Score:", risk.getRiskScore() + " (" + risk.getRiskLevel() + ")");
            addTableRow(table, "Status:", risk.getStatus() != null ? risk.getStatus().toString() : "OPEN");
            
            document.add(table);
            
            if (risk.getAiRecommendation() != null) {
                Paragraph aiRec = new Paragraph("AI Recommendation: " + risk.getAiRecommendation(), normalFont);
                aiRec.setSpacingAfter(10);
                document.add(aiRec);
            }
        }
    }
    
    private void addTableRow(PdfPTable table, String key, String value) {
        Font keyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        
        PdfPCell keyCell = new PdfPCell(new Phrase(key, keyFont));
        keyCell.setBorder(Rectangle.NO_BORDER);
        keyCell.setPadding(5);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        
        table.addCell(keyCell);
        table.addCell(valueCell);
    }
    
    private void addEmptyLine(Document document, int number) throws DocumentException {
        for (int i = 0; i < number; i++) {
            document.add(new Paragraph(" "));
        }
    }
    
    /**
     * Generate Executive Summary Report - Overview of all projects
     */
    public byte[] generateExecutiveSummary() {
        List<Project> allProjects = projectRepository.findAll();
        List<Risk> allRisks = riskRepository.findAll();
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();
            
            // Title
            addTitle(document, "Executive Summary Report");
            addEmptyLine(document, 1);
            
            // Generation info
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
            Paragraph generated = new Paragraph("Generated: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), smallFont);
            generated.setAlignment(Element.ALIGN_RIGHT);
            document.add(generated);
            addEmptyLine(document, 1);
            
            // Portfolio Overview
            addSectionHeader(document, "Portfolio Overview");
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            addTableRow(summaryTable, "Total Projects:", String.valueOf(allProjects.size()));
            addTableRow(summaryTable, "Active Projects:", 
                String.valueOf(allProjects.stream().filter(p -> p.getStatus() == Project.ProjectStatus.ACTIVE).count()));
            addTableRow(summaryTable, "Completed Projects:", 
                String.valueOf(allProjects.stream().filter(p -> p.getStatus() == Project.ProjectStatus.COMPLETED).count()));
            addTableRow(summaryTable, "Total Risks:", String.valueOf(allRisks.size()));
            document.add(summaryTable);
            addEmptyLine(document, 1);
            
            // Financial Summary
            addSectionHeader(document, "Financial Summary");
            BigDecimal totalBudget = allProjects.stream()
                .map(p -> p.getPlannedBudget() != null ? p.getPlannedBudget() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalCost = allProjects.stream()
                .map(p -> p.getActualCost() != null ? p.getActualCost() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            PdfPTable finTable = new PdfPTable(2);
            finTable.setWidthPercentage(100);
            addTableRow(finTable, "Total Planned Budget:", "$" + totalBudget);
            addTableRow(finTable, "Total Actual Cost:", "$" + totalCost);
            addTableRow(finTable, "Total Remaining:", "$" + totalBudget.subtract(totalCost));
            document.add(finTable);
            addEmptyLine(document, 1);
            
            // Project Health Summary
            addSectionHeader(document, "Project Health Distribution");
            Map<Project.HealthStatus, Long> healthMap = allProjects.stream()
                .collect(Collectors.groupingBy(Project::getHealthStatus, Collectors.counting()));
            
            PdfPTable healthTable = new PdfPTable(2);
            healthTable.setWidthPercentage(100);
            addTableRow(healthTable, "Healthy Projects:", String.valueOf(healthMap.getOrDefault(Project.HealthStatus.HEALTHY, 0L)));
            addTableRow(healthTable, "Warning Projects:", String.valueOf(healthMap.getOrDefault(Project.HealthStatus.WARNING, 0L)));
            addTableRow(healthTable, "Critical Projects:", String.valueOf(healthMap.getOrDefault(Project.HealthStatus.CRITICAL, 0L)));
            document.add(healthTable);
            addEmptyLine(document, 1);
            
            // Top Projects by Priority
            addSectionHeader(document, "High Priority Projects");
            List<Project> highPriorityProjects = allProjects.stream()
                .filter(p -> p.getPriority() == Project.Priority.HIGH)
                .limit(5)
                .toList();
            
            for (Project project : highPriorityProjects) {
                Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
                Paragraph projectName = new Paragraph(project.getProjectName(), boldFont);
                document.add(projectName);
                
                PdfPTable projTable = new PdfPTable(2);
                projTable.setWidthPercentage(100);
                projTable.setSpacingBefore(5);
                projTable.setSpacingAfter(10);
                addTableRow(projTable, "Status:", project.getStatus().toString());
                addTableRow(projTable, "Health:", project.getHealthStatus().toString());
                addTableRow(projTable, "Completion:", project.getCompletionPercentage() + "%");
                document.add(projTable);
            }
            
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating executive summary", e);
        }
    }
    
    /**
     * Generate Risk Assessment Report
     */
    public byte[] generateRiskAssessment() {
        List<Risk> allRisks = riskRepository.findAll();
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();
            
            // Title
            addTitle(document, "Risk Assessment Report");
            addEmptyLine(document, 1);
            
            // Generation info
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
            Paragraph generated = new Paragraph("Generated: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), smallFont);
            generated.setAlignment(Element.ALIGN_RIGHT);
            document.add(generated);
            addEmptyLine(document, 1);
            
            // Risk Overview
            addSectionHeader(document, "Risk Overview");
            PdfPTable overviewTable = new PdfPTable(2);
            overviewTable.setWidthPercentage(100);
            
            long highRisks = allRisks.stream().filter(r -> r.getRiskLevel() == Risk.RiskLevel.HIGH).count();
            long mediumRisks = allRisks.stream().filter(r -> r.getRiskLevel() == Risk.RiskLevel.MEDIUM).count();
            long lowRisks = allRisks.stream().filter(r -> r.getRiskLevel() == Risk.RiskLevel.LOW).count();
            
            addTableRow(overviewTable, "Total Risks Identified:", String.valueOf(allRisks.size()));
            addTableRow(overviewTable, "High Severity Risks:", String.valueOf(highRisks));
            addTableRow(overviewTable, "Medium Severity Risks:", String.valueOf(mediumRisks));
            addTableRow(overviewTable, "Low Severity Risks:", String.valueOf(lowRisks));
            document.add(overviewTable);
            addEmptyLine(document, 1);
            
            // Risk by Category
            addSectionHeader(document, "Risk Distribution by Category");
            Map<Risk.RiskCategory, Long> categoryMap = allRisks.stream()
                .collect(Collectors.groupingBy(Risk::getCategory, Collectors.counting()));
            
            PdfPTable categoryTable = new PdfPTable(2);
            categoryTable.setWidthPercentage(100);
            categoryMap.forEach((category, count) -> {
                try {
                    addTableRow(categoryTable, category.toString() + ":", String.valueOf(count));
                } catch (Exception e) {
                    // Skip
                }
            });
            document.add(categoryTable);
            addEmptyLine(document, 1);
            
            // High Priority Risks - Detailed
            List<Risk> criticalRisks = allRisks.stream()
                .filter(r -> r.getRiskLevel() == Risk.RiskLevel.HIGH)
                .toList();
            
            if (!criticalRisks.isEmpty()) {
                addSectionHeader(document, "Critical Risks - Immediate Action Required");
                addRiskDetails(document, criticalRisks);
            }
            
            // Medium Priority Risks
            List<Risk> mediumRiskList = allRisks.stream()
                .filter(r -> r.getRiskLevel() == Risk.RiskLevel.MEDIUM)
                .limit(5)
                .toList();
            
            if (!mediumRiskList.isEmpty()) {
                addSectionHeader(document, "Medium Priority Risks");
                addRiskDetails(document, mediumRiskList);
            }
            
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating risk assessment", e);
        }
    }
    
    /**
     * Generate Resource Utilization Report
     */
    public byte[] generateResourceUtilization() {
        List<Resource> allResources = resourceRepository.findAll();
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();
            
            // Title
            addTitle(document, "Resource Utilization Report");
            addEmptyLine(document, 1);
            
            // Generation info
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
            Paragraph generated = new Paragraph("Generated: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), smallFont);
            generated.setAlignment(Element.ALIGN_RIGHT);
            document.add(generated);
            addEmptyLine(document, 1);
            
            // Resource Summary
            addSectionHeader(document, "Resource Summary");
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            
            addTableRow(summaryTable, "Total Resources:", String.valueOf(allResources.size()));
            addTableRow(summaryTable, "Optimal Utilization:", 
                String.valueOf(allResources.stream().filter(r -> r.getUtilizationStatus() == Resource.UtilizationStatus.OPTIMAL).count()));
            addTableRow(summaryTable, "High Utilization:", 
                String.valueOf(allResources.stream().filter(r -> r.getUtilizationStatus() == Resource.UtilizationStatus.HIGH).count()));
            addTableRow(summaryTable, "Overloaded Resources:", 
                String.valueOf(allResources.stream().filter(r -> r.getUtilizationStatus() == Resource.UtilizationStatus.OVERLOADED).count()));
            
            document.add(summaryTable);
            addEmptyLine(document, 1);
            
            // Workload Analysis
            addSectionHeader(document, "Workload Distribution");
            PdfPTable workloadTable = new PdfPTable(2);
            workloadTable.setWidthPercentage(100);
            
            long overloaded = allResources.stream().filter(r -> r.getUtilizationStatus() == Resource.UtilizationStatus.OVERLOADED).count();
            long optimal = allResources.stream().filter(r -> r.getUtilizationStatus() == Resource.UtilizationStatus.OPTIMAL).count();
            long underutilized = allResources.stream().filter(r -> r.getUtilizationStatus() == Resource.UtilizationStatus.UNDERUTILIZED).count();
            
            addTableRow(workloadTable, "Overloaded (>100%):", String.valueOf(overloaded));
            addTableRow(workloadTable, "Optimal Workload (71-90%):", String.valueOf(optimal));
            addTableRow(workloadTable, "Underutilized (<70%):", String.valueOf(underutilized));
            
            document.add(workloadTable);
            addEmptyLine(document, 1);
            
            // Resource Details
            addSectionHeader(document, "Resource Details");
            
            for (Resource resource : allResources) {
                Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
                Paragraph resourceName = new Paragraph(resource.getEmployeeName(), boldFont);
                document.add(resourceName);
                
                PdfPTable resTable = new PdfPTable(2);
                resTable.setWidthPercentage(100);
                resTable.setSpacingBefore(5);
                resTable.setSpacingAfter(10);
                
                addTableRow(resTable, "Role:", resource.getRole());
                addTableRow(resTable, "Available Hours:", resource.getAvailableHours() + " hrs");
                addTableRow(resTable, "Assigned Hours:", resource.getAssignedHours() + " hrs");
                addTableRow(resTable, "Utilization:", resource.getUtilizationPercentage() + "%");
                addTableRow(resTable, "Status:", resource.getUtilizationStatus().toString());
                
                document.add(resTable);
            }
            
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating resource utilization report", e);
        }
    }
    
    /**
     * Generate Financial Analysis Report
     */
    public byte[] generateFinancialAnalysis() {
        List<Project> allProjects = projectRepository.findAll();
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();
            
            // Title
            addTitle(document, "Financial Analysis Report");
            addEmptyLine(document, 1);
            
            // Generation info
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
            Paragraph generated = new Paragraph("Generated: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), smallFont);
            generated.setAlignment(Element.ALIGN_RIGHT);
            document.add(generated);
            addEmptyLine(document, 1);
            
            // Overall Financial Summary
            addSectionHeader(document, "Portfolio Financial Summary");
            
            BigDecimal totalBudget = allProjects.stream()
                .map(p -> p.getPlannedBudget() != null ? p.getPlannedBudget() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalCost = allProjects.stream()
                .map(p -> p.getActualCost() != null ? p.getActualCost() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalRemaining = totalBudget.subtract(totalCost);
            
            double totalUtilPercent = totalBudget.compareTo(BigDecimal.ZERO) > 0 ? 
                totalCost.divide(totalBudget, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).doubleValue() : 0.0;
            
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            addTableRow(summaryTable, "Total Portfolio Budget:", "$" + totalBudget);
            addTableRow(summaryTable, "Total Actual Cost:", "$" + totalCost);
            addTableRow(summaryTable, "Total Remaining Budget:", "$" + totalRemaining);
            addTableRow(summaryTable, "Budget Utilization:", 
                String.format("%.1f%%", totalUtilPercent));
            
            document.add(summaryTable);
            addEmptyLine(document, 1);
            
            // Budget Status
            addSectionHeader(document, "Budget Status by Project");
            
            long onBudget = allProjects.stream()
                .filter(p -> {
                    BigDecimal pBudget = p.getPlannedBudget() != null ? p.getPlannedBudget() : BigDecimal.ZERO;
                    BigDecimal pCost = p.getActualCost() != null ? p.getActualCost() : BigDecimal.ZERO;
                    return pCost.compareTo(pBudget) <= 0;
                })
                .count();
            long overBudget = allProjects.stream()
                .filter(p -> {
                    BigDecimal pBudget = p.getPlannedBudget() != null ? p.getPlannedBudget() : BigDecimal.ZERO;
                    BigDecimal pCost = p.getActualCost() != null ? p.getActualCost() : BigDecimal.ZERO;
                    return pCost.compareTo(pBudget) > 0;
                })
                .count();
            
            PdfPTable statusTable = new PdfPTable(2);
            statusTable.setWidthPercentage(100);
            addTableRow(statusTable, "Projects On/Under Budget:", String.valueOf(onBudget));
            addTableRow(statusTable, "Projects Over Budget:", String.valueOf(overBudget));
            document.add(statusTable);
            addEmptyLine(document, 1);
            
            // Project Financial Details
            addSectionHeader(document, "Project Financial Breakdown");
            
            for (Project project : allProjects) {
                Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
                Paragraph projectName = new Paragraph(project.getProjectName(), boldFont);
                document.add(projectName);
                
                PdfPTable projTable = new PdfPTable(2);
                projTable.setWidthPercentage(100);
                projTable.setSpacingBefore(5);
                projTable.setSpacingAfter(10);
                
                BigDecimal pBudget = project.getPlannedBudget() != null ? project.getPlannedBudget() : BigDecimal.ZERO;
                BigDecimal pCost = project.getActualCost() != null ? project.getActualCost() : BigDecimal.ZERO;
                BigDecimal remaining = pBudget.subtract(pCost);
                
                double utilizationPercent = pBudget.compareTo(BigDecimal.ZERO) > 0 ?
                    pCost.divide(pBudget, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).doubleValue() : 0.0;
                
                addTableRow(projTable, "Planned Budget:", "$" + pBudget);
                addTableRow(projTable, "Actual Cost:", "$" + pCost);
                addTableRow(projTable, "Remaining Budget:", "$" + remaining);
                addTableRow(projTable, "Budget Utilization:", String.format("%.1f%%", utilizationPercent));
                addTableRow(projTable, "Project Completion:", (project.getCompletionPercentage() != null ? project.getCompletionPercentage() : 0) + "%");
                
                // Budget status indicator
                String budgetStatus = remaining.compareTo(BigDecimal.ZERO) >= 0 ? "On Budget" : "Over Budget";
                addTableRow(projTable, "Status:", budgetStatus);
                
                document.add(projTable);
            }
            
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating financial analysis", e);
        }
    }
}
