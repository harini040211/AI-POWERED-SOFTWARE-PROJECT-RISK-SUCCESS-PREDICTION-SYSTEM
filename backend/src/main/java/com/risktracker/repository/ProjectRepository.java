package com.risktracker.repository;

import com.risktracker.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Project Repository
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByProjectCode(String projectCode);
    List<Project> findByStatus(Project.ProjectStatus status);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = 'ACTIVE'")
    Long countActiveProjects();
}
