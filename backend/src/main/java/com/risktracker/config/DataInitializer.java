package com.risktracker.config;

import com.risktracker.model.Project;
import com.risktracker.model.Resource;
import com.risktracker.model.Risk;
import com.risktracker.model.User;
import com.risktracker.repository.ProjectRepository;
import com.risktracker.repository.ResourceRepository;
import com.risktracker.repository.RiskRepository;
import com.risktracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Initializer
 * 
 * Creates sample/demo data for demonstration purposes.
 * This runs automatically when the application starts.
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private RiskRepository riskRepository;
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    @Override
    public void run(String... args) {
        // Only initialize if database is empty
        if (userRepository.count() == 0) {
            initializeSampleData();
        }
    }
    
    private void initializeSampleData() {
        System.out.println("Initializing sample data...");
        
        // Create users with different roles
        User admin = new User("admin", "admin123", "Admin User");
        admin.setRole("ADMIN");
        userRepository.save(admin);
        
        User manager = new User("manager", "manager123", "Project Manager");
        manager.setRole("MANAGER");
        userRepository.save(manager);
        
        User user = new User("user", "user123", "Regular User");
        user.setRole("USER");
        userRepository.save(user);
        
        // Create sample projects
        Project project1 = new Project();
        project1.setProjectName("E-Commerce Platform");
        project1.setProjectCode("ECOM-001");
        project1.setDescription("Online shopping platform with payment integration");
        project1.setClientName("TechMart Inc");
        project1.setStartDate(LocalDate.now().minusMonths(3));
        project1.setExpectedEndDate(LocalDate.now().plusMonths(3));
        project1.setStatus(Project.ProjectStatus.ACTIVE);
        project1.setPriority(Project.Priority.HIGH);
        project1.setPlannedBudget(new BigDecimal("150000"));
        project1.setActualCost(new BigDecimal("95000"));
        project1.setCompletionPercentage(60);
        projectRepository.save(project1);
        
        Project project2 = new Project();
        project2.setProjectName("Mobile Banking App");
        project2.setProjectCode("BANK-002");
        project2.setDescription("Secure mobile banking application");
        project2.setClientName("SecureBank");
        project2.setStartDate(LocalDate.now().minusMonths(2));
        project2.setExpectedEndDate(LocalDate.now().plusMonths(4));
        project2.setStatus(Project.ProjectStatus.ACTIVE);
        project2.setPriority(Project.Priority.HIGH);
        project2.setPlannedBudget(new BigDecimal("200000"));
        project2.setActualCost(new BigDecimal("120000"));
        project2.setCompletionPercentage(50);
        projectRepository.save(project2);
        
        Project project3 = new Project();
        project3.setProjectName("Inventory Management System");
        project3.setProjectCode("INV-003");
        project3.setDescription("Warehouse inventory tracking system");
        project3.setClientName("LogiCorp");
        project3.setStartDate(LocalDate.now().minusMonths(1));
        project3.setExpectedEndDate(LocalDate.now().plusMonths(2));
        project3.setStatus(Project.ProjectStatus.PLANNED);
        project3.setPriority(Project.Priority.MEDIUM);
        project3.setPlannedBudget(new BigDecimal("80000"));
        project3.setActualCost(new BigDecimal("10000"));
        project3.setCompletionPercentage(15);
        projectRepository.save(project3);
        
        // Create sample risks for project 1
        Risk risk1 = new Risk();
        risk1.setProject(project1);
        risk1.setTitle("Payment Gateway Integration Delays");
        risk1.setDescription("Third-party payment API is experiencing downtime");
        risk1.setCategory(Risk.RiskCategory.TECHNICAL);
        risk1.setProbability(4);
        risk1.setImpact(4);
        risk1.setStatus(Risk.RiskStatus.OPEN);
        risk1.setMitigation("Switch to backup payment provider");
        riskRepository.save(risk1);
        
        Risk risk2 = new Risk();
        risk2.setProject(project1);
        risk2.setTitle("Database Performance Issues");
        risk2.setDescription("Query response time degrading under load");
        risk2.setCategory(Risk.RiskCategory.TECHNICAL);
        risk2.setProbability(3);
        risk2.setImpact(3);
        risk2.setStatus(Risk.RiskStatus.IN_PROGRESS);
        risk2.setMitigation("Implement caching and optimize queries");
        riskRepository.save(risk2);
        
        // Create sample risks for project 2
        Risk risk3 = new Risk();
        risk3.setProject(project2);
        risk3.setTitle("Security Compliance Requirements");
        risk3.setDescription("Need additional security certifications");
        risk3.setCategory(Risk.RiskCategory.SECURITY);
        risk3.setProbability(4);
        risk3.setImpact(5);
        risk3.setStatus(Risk.RiskStatus.OPEN);
        risk3.setMitigation("Engage security audit team");
        riskRepository.save(risk3);
        
        Risk risk4 = new Risk();
        risk4.setProject(project2);
        risk4.setTitle("Key Developer Resignation");
        risk4.setDescription("Lead mobile developer leaving next month");
        risk4.setCategory(Risk.RiskCategory.RESOURCE);
        risk4.setProbability(5);
        risk4.setImpact(4);
        risk4.setStatus(Risk.RiskStatus.OPEN);
        risk4.setMitigation("Knowledge transfer and hire replacement");
        riskRepository.save(risk4);
        
        Risk risk5 = new Risk();
        risk5.setProject(project2);
        risk5.setTitle("Budget Overrun Risk");
        risk5.setDescription("Current spending rate will exceed budget");
        risk5.setCategory(Risk.RiskCategory.FINANCIAL);
        risk5.setProbability(3);
        risk5.setImpact(4);
        risk5.setStatus(Risk.RiskStatus.IN_PROGRESS);
        risk5.setMitigation("Reduce scope or request additional funding");
        riskRepository.save(risk5);
        
        // Create sample risks for project 3
        Risk risk6 = new Risk();
        risk6.setProject(project3);
        risk6.setTitle("Unclear Requirements");
        risk6.setDescription("Client requirements are changing frequently");
        risk6.setCategory(Risk.RiskCategory.REQUIREMENT);
        risk6.setProbability(3);
        risk6.setImpact(3);
        risk6.setStatus(Risk.RiskStatus.OPEN);
        risk6.setMitigation("Schedule requirement freeze date");
        riskRepository.save(risk6);
        
        // Create sample resources
        Resource res1 = new Resource("John Smith", "Senior Developer", new BigDecimal("160"));
        res1.setAssignedHours(new BigDecimal("145"));
        resourceRepository.save(res1);
        
        Resource res2 = new Resource("Sarah Johnson", "Project Manager", new BigDecimal("160"));
        res2.setAssignedHours(new BigDecimal("120"));
        resourceRepository.save(res2);
        
        Resource res3 = new Resource("Mike Chen", "QA Engineer", new BigDecimal("160"));
        res3.setAssignedHours(new BigDecimal("95"));
        resourceRepository.save(res3);
        
        Resource res4 = new Resource("Emily Davis", "UI/UX Designer", new BigDecimal("160"));
        res4.setAssignedHours(new BigDecimal("75"));
        resourceRepository.save(res4);
        
        Resource res5 = new Resource("David Wilson", "Backend Developer", new BigDecimal("160"));
        res5.setAssignedHours(new BigDecimal("170"));
        resourceRepository.save(res5);
        
        System.out.println("Sample data initialized successfully!");
        System.out.println("Login credentials:");
        System.out.println("  Admin: username=admin, password=admin123");
        System.out.println("  Manager: username=manager, password=manager123");
        System.out.println("  User: username=user, password=user123");
    }
}
