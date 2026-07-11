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

    Page<Magazine> findByPublishedTrueOrderByYearDescIssueNumberDesc(
        Pageable pageable
    );

    // All published issues for a specific region
    Page<Magazine> findByPublishedTrueAndRegionOrderByYearDescIssueNumberDesc(
        MagazineRegion region, Pageable pageable
    );

    // Latest 4 issues per region — for homepage cover grid
    List<Magazine> findTop4ByPublishedTrueAndRegionOrderByYearDescIssueNumberDesc(
        MagazineRegion region
    );

    List<Magazine> findTop3ByPublishedTrueOrderByYearDescIssueNumberDesc();

    Optional<Magazine> findTopByPublishedTrueOrderByYearDescIssueNumberDesc();
}