package com.magazine.app.service;

import com.magazine.app.model.Article;
import com.magazine.app.model.ArticleType;
import com.magazine.app.model.Category;
import com.magazine.app.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    // ── Public: general listing ─────────────────────
    public Page<Article> getPublishedArticles(int page, int size) {
        return articleRepository.findByPublishedTrueOrderByCreatedAtDesc(
            PageRequest.of(page, size)
        );
    }

    public Page<Article> getPublishedArticlesByCategory(Category category, int page, int size) {
        return articleRepository.findByPublishedTrueAndCategoryOrderByCreatedAtDesc(
            category, PageRequest.of(page, size)
        );
    }

    public List<Article> getFeaturedArticles() {
        return articleRepository.findByPublishedTrueAndFeaturedTrueOrderByCreatedAtDesc();
    }

    public List<Article> getLatestArticles() {
        return articleRepository.findTop5ByPublishedTrueOrderByCreatedAtDesc();
    }

    // ── Public: by type (handles ALL content types) ─
    // Pass ArticleType.INTERVIEW, ArticleType.NEWS, etc.
    public Page<Article> getByType(ArticleType type, int page, int size) {
        return articleRepository
            .findByPublishedTrueAndArticleTypeOrderByCreatedAtDesc(
                type, PageRequest.of(page, size)
            );
    }

    // Type, optionally + category, with an explicit sort direction — e.g. /news?sort=asc
    public Page<Article> getByTypeSorted(ArticleType type, Category category, int page, int size, Sort.Direction sortDir) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortDir, "createdAt"));
        return category != null
            ? articleRepository.findByPublishedTrueAndArticleTypeAndCategory(type, category, pageable)
            : articleRepository.findByPublishedTrueAndArticleType(type, pageable);
    }

    // Top 5 most-viewed articles of a type — for "Trending" sidebar widgets
    public List<Article> getTrendingByType(ArticleType type) {
        return articleRepository.findTop5ByPublishedTrueAndArticleTypeOrderByViewsDesc(type);
    }

    // Admin Interviews list — search by interviewee name and/or company and/or category (any may be blank/null)
    public List<Article> searchInterviews(String name, String company, Long categoryId) {
        String normalizedName = (name == null || name.isBlank()) ? null : name.trim();
        String normalizedCompany = (company == null || company.isBlank()) ? null : company.trim();
        return articleRepository
            .searchByIntervieweeAndCompany(ArticleType.INTERVIEW, normalizedName, normalizedCompany, categoryId, PageRequest.of(0, 100))
            .getContent();
    }

    // For homepage sections — get latest 3 of any type
    public List<Article> getLatestByType(ArticleType type, int limit) {
        return articleRepository
            .findByPublishedTrueAndArticleTypeOrderByCreatedAtDesc(
                type, PageRequest.of(0, limit)
            )
            .getContent();
    }

    // ── Slug and slug lookup ────────────────────────
    // Public-facing lookup — excludes drafts and trashed articles, so an
    // unpublished/deleted article 404s like a missing slug would.
    public Optional<Article> getBySlug(String slug) {
        return articleRepository.findBySlugAndPublishedTrueAndDeletedFalse(slug);
    }

    public void incrementViews(Article article) {
        article.setViews(article.getViews() + 1);
        articleRepository.save(article);
    }

    public List<Article> search(String keyword) {
        return articleRepository.findByPublishedTrueAndTitleContainingIgnoreCase(keyword);
    }

    // ── Admin operations ────────────────────────────
    public List<Article> getAllArticles() {
        return articleRepository.findByDeletedFalseOrderByCreatedAtDesc();
    }

    // Admin list search + filter — any/all of these may be null (no filter applied)
    public Page<Article> searchAdmin(String keyword, ArticleType type, Long categoryId, Boolean published, int page, int size) {
        String normalizedKeyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return articleRepository.searchAdmin(normalizedKeyword, type, categoryId, published, PageRequest.of(page, size));
    }

    public Optional<Article> getById(Long id) {
        return articleRepository.findById(id);
    }

    public Article save(Article article) {
        if (article.getId() == null) {
            article.setCreatedAt(LocalDateTime.now());
        }
        article.setUpdatedAt(LocalDateTime.now());
        return articleRepository.save(article);
    }

    // ── Trash (soft delete) ──────────────────────────
    public Page<Article> getTrash(int page, int size) {
        return articleRepository.findByDeletedTrue(
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"))
        );
    }

    // Moves to trash and unpublishes, so it also disappears from the public site
    public void delete(Long id) {
        articleRepository.findById(id).ifPresent(article -> {
            article.setDeleted(true);
            article.setPublished(false);
            article.setUpdatedAt(LocalDateTime.now());
            articleRepository.save(article);
        });
    }

    public void restore(Long id) {
        articleRepository.findById(id).ifPresent(article -> {
            article.setDeleted(false);
            article.setUpdatedAt(LocalDateTime.now());
            articleRepository.save(article);
        });
    }

    public void deletePermanently(Long id) {
        articleRepository.deleteById(id);
    }

    // ── Counts for dashboard ────────────────────────
    public long countPublished() {
        return articleRepository.countByPublishedTrue();
    }

    public long countAll() {
        return articleRepository.count();
    }

    public long countByType(ArticleType type) {
        return articleRepository.countByPublishedTrueAndArticleType(type);
    }

    // ── Slug generation ─────────────────────────────
    public String generateSlug(String title) {
        String base = title.toLowerCase().trim()
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-");
        String slug = base;
        int counter = 1;
        while (articleRepository.findBySlug(slug).isPresent()) {
            slug = base + "-" + counter;
            counter++;
        }
        return slug;
    }
}
