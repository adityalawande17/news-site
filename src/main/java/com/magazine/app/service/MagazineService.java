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
    public Page<Magazine> getPublished(int page, int size) {
        return magazineRepository
            .findByPublishedTrueOrderByYearDescIssueNumberDesc(
                PageRequest.of(page, size)
            );
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
    public List<Magazine> getAll() {
        return magazineRepository.findAll(
            Sort.by(Sort.Direction.DESC, "year")
                .and(Sort.by(Sort.Direction.DESC, "issueNumber"))
        );
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
public Page<Magazine> getByRegion(MagazineRegion region, int page, int size) {
    return magazineRepository
        .findByPublishedTrueAndRegionOrderByYearDescIssueNumberDesc(
            region, PageRequest.of(page, size)
        );
}

// Latest issue per region — for homepage buttons
// Returns a map: region → latest magazine (null if none published yet)
public Map<MagazineRegion, Magazine> getLatestPerRegion() {
    Map<MagazineRegion, Magazine> map = new LinkedHashMap<>();
    for (MagazineRegion region : MagazineRegion.values()) {
        magazineRepository
            .findTopByPublishedTrueAndRegionOrderByYearDescIssueNumberDesc(region)
            .ifPresentOrElse(
                mag -> map.put(region, mag),
                ()  -> map.put(region, null)
            );
    }
    return map;
}
}