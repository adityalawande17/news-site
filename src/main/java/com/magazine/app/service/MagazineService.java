package com.magazine.app.service;

import com.magazine.app.model.Article;
import com.magazine.app.model.Magazine;
import com.magazine.app.repository.ArticleRepository;
import com.magazine.app.repository.MagazineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.magazine.app.model.MagazineRegion;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MagazineService {

    @Autowired private MagazineRepository magazineRepository;
    @Autowired private ArticleRepository  articleRepository;

    // ── Public ───────────────────────────────────────
    public Page<Magazine> getPublished(int page, int size, Sort.Direction sortDir) {
        return magazineRepository
            .findByPublishedTrue(PageRequest.of(page, size, yearSort(sortDir)));
    }

    public List<Magazine> getLatest(int limit) {
        return magazineRepository
            .findTop3ByPublishedTrueOrderByYearDescIssueNumberDesc();
    }

    public Optional<Magazine> getLatestIssue() {
        return magazineRepository
            .findTopByPublishedTrueOrderByYearDescIssueNumberDesc();
    }

    public Optional<Magazine> getBySlug(String slug) {
        return magazineRepository.findBySlug(slug);
    }

    // ── Admin ────────────────────────────────────────
    public Page<Magazine> getAll(int page, int size) {
        return magazineRepository.findAll(PageRequest.of(page, size, yearSort(Sort.Direction.DESC)));
    }

    public Optional<Magazine> getById(Long id) {
        return magazineRepository.findById(id);
    }

    public Magazine save(Magazine magazine, List<Long> articleIds) {
        // Link selected articles to this issue
        if (articleIds != null && !articleIds.isEmpty()) {
            List<Article> articles = new ArrayList<>();
            for (Long articleId : articleIds) {
                articleRepository.findById(articleId)
                    .ifPresent(articles::add);
            }
            magazine.setFeaturedArticles(articles);
        } else {
            magazine.setFeaturedArticles(new ArrayList<>());
        }
        return magazineRepository.save(magazine);
    }

    public void delete(Long id) {
        magazineRepository.deleteById(id);
    }

    public long countAll() {
        return magazineRepository.count();
    }

    public String generateSlug(String title) {
        String base = title.toLowerCase().trim()
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-");
        String slug = base;
        int counter = 1;
        while (magazineRepository.findBySlug(slug).isPresent()) {
            slug = base + "-" + counter;
            counter++;
        }
        return slug;
    }

    // Paginated issues filtered by region — for /magazine?region=ASIA
public Page<Magazine> getByRegion(MagazineRegion region, int page, int size, Sort.Direction sortDir) {
    return magazineRepository
        .findByPublishedTrueAndRegion(
            region, PageRequest.of(page, size, yearSort(sortDir))
        );
}

// Paginated issues filtered by year — for /magazine?year=2026
public Page<Magazine> getByYear(int year, int page, int size, Sort.Direction sortDir) {
    return magazineRepository
        .findByPublishedTrueAndYear(
            year, PageRequest.of(page, size, yearSort(sortDir))
        );
}

// Paginated issues filtered by region + year — for /magazine?region=ASIA&year=2026
public Page<Magazine> getByRegionAndYear(MagazineRegion region, int year, int page, int size, Sort.Direction sortDir) {
    return magazineRepository
        .findByPublishedTrueAndRegionAndYear(
            region, year, PageRequest.of(page, size, yearSort(sortDir))
        );
}

// Sort by year then issue number, in the given direction
private Sort yearSort(Sort.Direction sortDir) {
    return Sort.by(sortDir, "year").and(Sort.by(sortDir, "issueNumber"));
}

// Distinct publication years across all published issues — for the year filter dropdown
public List<Integer> getAvailableYears() {
    return magazineRepository.findAll().stream()
        .filter(Magazine::isPublished)
        .map(Magazine::getYear)
        .distinct()
        .sorted(java.util.Comparator.reverseOrder())
        .collect(java.util.stream.Collectors.toList());
}

// Latest 4 issues per region — for homepage cover grid
public Map<MagazineRegion, List<Magazine>> getLatest4PerRegion() {
    Map<MagazineRegion, List<Magazine>> map = new LinkedHashMap<>();
    for (MagazineRegion region : MagazineRegion.values()) {
        map.put(region,
            magazineRepository
                .findTop4ByPublishedTrueAndRegionOrderByYearDescIssueNumberDesc(region)
        );
    }
    return map;
}

// Recent cover images (most recent issues first) — decorative backdrop for the homepage magazine showcase
public List<String> getRecentCoverUrls(int limit) {
    return magazineRepository.findAll(yearSort(Sort.Direction.DESC)).stream()
        .filter(Magazine::isPublished)
        .map(Magazine::getCoverImageUrl)
        .filter(url -> url != null && !url.isBlank())
        .limit(limit)
        .collect(java.util.stream.Collectors.toList());
}
}