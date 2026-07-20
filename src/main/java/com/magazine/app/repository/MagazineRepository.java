package com.magazine.app.repository;

import com.magazine.app.model.Magazine;
import com.magazine.app.model.MagazineRegion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MagazineRepository extends JpaRepository<Magazine, Long> {

    Optional<Magazine> findBySlug(String slug);

    // Magazine listing page — sort direction (asc/desc) comes from the Pageable's Sort
    Page<Magazine> findByPublishedTrue(Pageable pageable);

    // All published issues for a specific region
    Page<Magazine> findByPublishedTrueAndRegion(
        MagazineRegion region, Pageable pageable
    );

    // All published issues for a specific year
    Page<Magazine> findByPublishedTrueAndYear(
        int year, Pageable pageable
    );

    // All published issues for a specific region + year
    Page<Magazine> findByPublishedTrueAndRegionAndYear(
        MagazineRegion region, int year, Pageable pageable
    );

    // Latest 4 issues per region — for homepage cover grid
    List<Magazine> findTop4ByPublishedTrueAndRegionOrderByYearDescIssueNumberDesc(
        MagazineRegion region
    );

    List<Magazine> findTop3ByPublishedTrueOrderByYearDescIssueNumberDesc();

    Optional<Magazine> findTopByPublishedTrueOrderByYearDescIssueNumberDesc();
}