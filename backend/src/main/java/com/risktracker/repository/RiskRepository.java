package com.risktracker.repository;

import com.risktracker.model.Risk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Risk Repository
 */
@Repository
public interface RiskRepository extends JpaRepository<Risk, Long> {
    List<Risk> findByProjectId(Long projectId);
    List<Risk> findByRiskLevel(Risk.RiskLevel riskLevel);
    
    @Query("SELECT COUNT(r) FROM Risk r WHERE r.riskLevel = 'HIGH' AND r.status <> 'CLOSED'")
    Long countHighRisks();
    
    @Query("SELECT AVG(r.riskScore) FROM Risk r WHERE r.status <> 'CLOSED'")
    Double getAverageRiskScore();
    
    @Query("SELECT r.riskLevel, COUNT(r) FROM Risk r WHERE r.status <> 'CLOSED' GROUP BY r.riskLevel")
    List<Object[]> countByRiskLevel();
    
    @Query("SELECT r.category, COUNT(r) FROM Risk r WHERE r.status <> 'CLOSED' GROUP BY r.category")
    List<Object[]> countByCategory();
}
