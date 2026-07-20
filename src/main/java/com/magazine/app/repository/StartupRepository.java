package com.magazine.app.repository;

import com.magazine.app.model.FundingStage;
import com.magazine.app.model.Startup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StartupRepository extends JpaRepository<Startup, Long> {

    Optional<Startup> findBySlug(String slug);

    Page<Startup> findByPublishedTrueOrderByCreatedAtDesc(
        Pageable pageable
    );

    List<Startup> findByPublishedTrueAndFeaturedTrueOrderByCreatedAtDesc();

    List<Startup> findTop6ByPublishedTrueOrderByCreatedAtDesc();

    // Combinable sector / stage / city filters — each null param is ignored
    @Query("""
       SELECT s FROM Startup s
       WHERE s.published = true
         AND (:sector IS NULL OR s.sector = :sector)
         AND (:stage IS NULL OR s.fundingStage = :stage)
         AND (:city IS NULL OR s.city = :city)
       ORDER BY s.createdAt DESC
       """)
    Page<Startup> findFiltered(
        @Param("sector") String sector,
        @Param("stage") FundingStage stage,
        @Param("city") String city,
        Pageable pageable
    );

    @Query("""
       SELECT DISTINCT s.sector
       FROM Startup s
       WHERE s.published = true AND s.sector IS NOT NULL
       ORDER BY s.sector
       """)
    List<String> findDistinctSectorByPublishedTrue();

    @Query("""
       SELECT DISTINCT s.city
       FROM Startup s
       WHERE s.published = true AND s.city IS NOT NULL
       ORDER BY s.city
       """)
    List<String> findDistinctCityByPublishedTrue();

    long countByPublishedTrue();
}