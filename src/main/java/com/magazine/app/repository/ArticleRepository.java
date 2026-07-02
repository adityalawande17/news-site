package com.magazine.app.repository;

import com.magazine.app.model.Article;
import com.magazine.app.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findBySlug(String slug);
    Page<Article> findByPublishedTrueOrderByCreatedAtDesc(Pageable pageable);
    Page<Article> findByPublishedTrueAndCategoryOrderByCreatedAtDesc(Category category, Pageable pageable);
    List<Article> findByPublishedTrueAndFeaturedTrueOrderByCreatedAtDesc();
    List<Article> findTop5ByPublishedTrueOrderByCreatedAtDesc();
    List<Article> findByPublishedTrueAndTitleContainingIgnoreCase(String keyword);
    long countByPublishedTrue();
}
