package com.magazine.app.repository;

import com.magazine.app.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findBySlug(String slug);

    Page<Company> findByPublishedTrueOrderByNameAsc(Pageable pageable);

    Page<Company> findByPublishedTrueAndSectorOrderByNameAsc(
        String sector, Pageable pageable
    );

    List<Company> findByPublishedTrueAndFeaturedTrueOrderByNameAsc();

    List<Company> findTop6ByPublishedTrueOrderByCreatedAtDesc();

    @Query("""
       SELECT DISTINCT c.sector
       FROM Company c
       WHERE c.published = true
       ORDER BY c.sector
       """)
List<String> findDistinctSectorByPublishedTrue();

    long countByPublishedTrue();
}