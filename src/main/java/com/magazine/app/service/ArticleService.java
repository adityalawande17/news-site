package com.magazine.app.service;

import com.magazine.app.model.Article;
import com.magazine.app.model.Category;
import com.magazine.app.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    public Page<Article> getPublishedArticles(int page, int size) {
        return articleRepository.findByPublishedTrueOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    public Page<Article> getPublishedArticlesByCategory(Category category, int page, int size) {
        return articleRepository.findByPublishedTrueAndCategoryOrderByCreatedAtDesc(category, PageRequest.of(page, size));
    }

    public List<Article> getFeaturedArticles() {
        return articleRepository.findByPublishedTrueAndFeaturedTrueOrderByCreatedAtDesc();
    }

    public List<Article> getLatestArticles() {
        return articleRepository.findTop5ByPublishedTrueOrderByCreatedAtDesc();
    }

    public Optional<Article> getBySlug(String slug) {
        return articleRepository.findBySlug(slug);
    }

    public List<Article> search(String keyword) {
        return articleRepository.findByPublishedTrueAndTitleContainingIgnoreCase(keyword);
    }

    public void incrementViews(Article article) {
        article.setViews(article.getViews() + 1);
        articleRepository.save(article);
    }

    // ---- Admin operations ----
    public List<Article> getAllArticles() {
        return articleRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
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

    public void delete(Long id) {
        articleRepository.deleteById(id);
    }

    public long countPublished() {
        return articleRepository.countByPublishedTrue();
    }

    public long countAll() {
        return articleRepository.count();
    }

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
