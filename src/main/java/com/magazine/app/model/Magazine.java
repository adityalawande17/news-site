package com.magazine.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "magazines")
public class Magazine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @Column(unique = true, nullable = false)
    private String slug;

    private int issueNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "region")
    private MagazineRegion region;

    @Column(name = "issue_month")
    private String month;       // "July"

    @Column(name = "publication_year")
    private int year;           // 2026

    @Column(length = 500)
    private String description;

    private String coverImageUrl;

    private String pdfUrl;      // downloadable PDF link

    // Featured articles inside this magazine issue
    @ManyToMany
    @JoinTable(
        name = "magazine_articles",
        joinColumns = @JoinColumn(name = "magazine_id"),
        inverseJoinColumns = @JoinColumn(name = "article_id")
    )
    private List<Article> featuredArticles = new ArrayList<>();

    private boolean published = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Magazine() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public int getIssueNumber() { return issueNumber; }
    public void setIssueNumber(int issueNumber) { this.issueNumber = issueNumber; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }

    public List<Article> getFeaturedArticles() { return featuredArticles; }
    public void setFeaturedArticles(List<Article> featuredArticles) { this.featuredArticles = featuredArticles; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public MagazineRegion getRegion() { return region; }
    public void setRegion(MagazineRegion region) { this.region = region; }
}