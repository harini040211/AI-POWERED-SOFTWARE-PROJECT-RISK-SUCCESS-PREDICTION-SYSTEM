package com.risktracker.repository;

import com.risktracker.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

/**
 * Resource Repository
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByUtilizationStatus(Resource.UtilizationStatus status);
    
    @Query("SELECT AVG(r.utilizationPercentage) FROM Resource r")
    BigDecimal getAverageUtilization();
}
