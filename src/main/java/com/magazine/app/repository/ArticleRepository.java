package com.magazine.app.repository;

import com.magazine.app.model.Article;
import com.magazine.app.model.ArticleType;
import com.magazine.app.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    // ── Existing methods ────────────────────────────
    Optional<Article> findBySlug(String slug);
    Optional<Article> findBySlugAndPublishedTrueAndDeletedFalse(String slug);
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

    // ── Admin: active list (excludes trashed) ────────
    List<Article> findByDeletedFalseOrderByCreatedAtDesc();

    // ── Admin: trash view (paginated) ────────────────
    Page<Article> findByDeletedTrue(Pageable pageable);

    // ── Admin: combinable search + filter (all optional), paginated ─
    @Query("""
        SELECT a FROM Article a
        WHERE a.deleted = false
          AND (:keyword IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:type IS NULL OR a.articleType = :type)
          AND (:categoryId IS NULL OR a.category.id = :categoryId)
          AND (:published IS NULL OR a.published = :published)
        ORDER BY a.createdAt DESC
        """)
    Page<Article> searchAdmin(
        @Param("keyword") String keyword,
        @Param("type") ArticleType type,
        @Param("categoryId") Long categoryId,
        @Param("published") Boolean published,
        Pageable pageable
    );

    // ── Admin: Interviews search by interviewee name / company (both optional) ─
    @Query("""
        SELECT a FROM Article a
        WHERE a.deleted = false
          AND a.published = true
          AND a.articleType = :type
          AND (:name IS NULL OR LOWER(a.intervieweeName) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:company IS NULL OR LOWER(a.intervieweeCompany) LIKE LOWER(CONCAT('%', :company, '%')))
        ORDER BY a.createdAt DESC
        """)
    Page<Article> searchByIntervieweeAndCompany(
        @Param("type") ArticleType type,
        @Param("name") String name,
        @Param("company") String company,
        Pageable pageable
    );
}
