package com.magazine.app.repository;

import com.magazine.app.model.FundingStage;
import com.magazine.app.model.Startup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StartupRepository extends JpaRepository<Startup, Long> {

    Optional<Startup> findBySlug(String slug);

    Page<Startup> findByPublishedTrueOrderByCreatedAtDesc(
        Pageable pageable
    );

    Page<Startup> findByPublishedTrueAndSectorOrderByCreatedAtDesc(
        String sector, Pageable pageable
    );

    Page<Startup> findByPublishedTrueAndFundingStageOrderByCreatedAtDesc(
        FundingStage fundingStage, Pageable pageable
    );

    List<Startup> findByPublishedTrueAndFeaturedTrueOrderByCreatedAtDesc();

    List<Startup> findTop6ByPublishedTrueOrderByCreatedAtDesc();

    @Query("""
       SELECT DISTINCT c.sector
       FROM Company c
       WHERE c.published = true
       ORDER BY c.sector
       """)
    List<String> findDistinctSectorByPublishedTrue();

    long countByPublishedTrue();
}