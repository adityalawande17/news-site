package com.magazine.app.repository;

import com.magazine.app.model.Article;
import com.magazine.app.model.ArticleType;
import com.magazine.app.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    // ── Existing methods ────────────────────────────
    Optional<Article> findBySlug(String slug);
    Page<Article> findByPublishedTrueOrderByCreatedAtDesc(Pageable pageable);
    Page<Article> findByPublishedTrueAndCategoryOrderByCreatedAtDesc(Category category, Pageable pageable);
    List<Article> findByPublishedTrueAndFeaturedTrueOrderByCreatedAtDesc();
    long countByPublishedTrueAndFeaturedTrue();
    List<Article> findTop5ByPublishedTrueOrderByCreatedAtDesc();
    List<Article> findByPublishedTrueAndTitleContainingIgnoreCase(String keyword);
    long countByPublishedTrue();

    // ── Type-based methods (used for ALL content types) ─
    // One method handles Blog, Interview, News, Case Study — just pass the type
    Page<Article> findByPublishedTrueAndArticleTypeOrderByCreatedAtDesc(
        ArticleType articleType, Pageable pageable
    );

    // For homepage preview sections — get latest N of any type
    List<Article> findTop3ByPublishedTrueAndArticleTypeOrderByCreatedAtDesc(
        ArticleType articleType
    );

    // Sort-direction-driven variants (sort order comes from the Pageable's Sort) — News listing
    Page<Article> findByPublishedTrueAndArticleType(
        ArticleType articleType, Pageable pageable
    );

    Page<Article> findByPublishedTrueAndArticleTypeAndCategory(
        ArticleType articleType, Category category, Pageable pageable
    );

    // Count by type — for admin dashboard stats
    long countByPublishedTrueAndArticleType(ArticleType articleType);
}
