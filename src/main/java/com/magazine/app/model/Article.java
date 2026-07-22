package com.magazine.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @Column(unique = true, nullable = false)
    private String slug;

    @NotBlank(message = "Summary is required")
    @Column(length = 500)
    private String summary;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private String authorName;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // ── Article type ────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "article_type")
    private ArticleType articleType = ArticleType.NEWS;

    // ── Interview-specific fields ───────────────────
    private String intervieweeName;

    private String intervieweeTitle;

    private String intervieweeCompany;

    private String intervieweePhoto;

    // News Specific Fields
    @Column(name = "is_breaking")
    private Boolean breaking = false;

    private String newsSource;

    // ── Case Study fields ────────────────────────────────
    private String clientName;
    private String clientIndustry;

    @Column(columnDefinition = "TEXT")
    private String caseChallenge;

    @Column(columnDefinition = "TEXT")
    private String caseSolution;

    private String caseResult;

    // ── Press Release fields ─────────────────────────────
    private String pressCompanyName;
    private String pressContactName;
    private String pressContactEmail;

    // ── Flags ───────────────────────────────────────
    private boolean published = true;

    private boolean featured = false;

    // Soft-delete: trashed articles are hidden from admin lists and (via the
    // published flag also being cleared on delete) from the public site, but
    // stay in the DB so they can be restored instead of lost to a misclick.
    // columnDefinition gives existing rows a default when this column is added
    // to the table via ddl-auto=update, avoiding a NOT NULL violation.
    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean deleted = false;

    private long views = 0;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    public Article() {}

    // ── Getters and Setters ─────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public ArticleType getArticleType() { return articleType; }
    public void setArticleType(ArticleType articleType) { this.articleType = articleType; }

    public String getIntervieweeName() { return intervieweeName; }
    public void setIntervieweeName(String intervieweeName) { this.intervieweeName = intervieweeName; }

    public String getIntervieweeTitle() { return intervieweeTitle; }
    public void setIntervieweeTitle(String intervieweeTitle) { this.intervieweeTitle = intervieweeTitle; }

    public String getIntervieweeCompany() { return intervieweeCompany; }
    public void setIntervieweeCompany(String intervieweeCompany) { this.intervieweeCompany = intervieweeCompany; }

    public String getIntervieweePhoto() { return intervieweePhoto; }
    public void setIntervieweePhoto(String intervieweePhoto) { this.intervieweePhoto = intervieweePhoto; }

    public Boolean getBreaking() { return breaking; }
    public void setBreaking(Boolean breaking) { this.breaking = breaking; }

    public String getNewsSource() { return newsSource; }
    public void setNewsSource(String newsSource) { this.newsSource = newsSource; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public long getViews() { return views; }
    public void setViews(long views) { this.views = views; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Getter and Setter for clientName
public String getClientName() {
    return clientName;
}

public void setClientName(String clientName) {
    this.clientName = clientName;
}

// Getter and Setter for clientIndustry
public String getClientIndustry() {
    return clientIndustry;
}

public void setClientIndustry(String clientIndustry) {
    this.clientIndustry = clientIndustry;
}

// Getter and Setter for caseChallenge
public String getCaseChallenge() {
    return caseChallenge;
}

public void setCaseChallenge(String caseChallenge) {
    this.caseChallenge = caseChallenge;
}

// Getter and Setter for caseSolution
public String getCaseSolution() {
    return caseSolution;
}

public void setCaseSolution(String caseSolution) {
    this.caseSolution = caseSolution;
}

// Getter and Setter for caseResult
public String getCaseResult() {
    return caseResult;
}

public void setCaseResult(String caseResult) {
    this.caseResult = caseResult;
}

public String getPressCompanyName() { return pressCompanyName; }
public void setPressCompanyName(String pressCompanyName) { this.pressCompanyName = pressCompanyName; }

public String getPressContactName() { return pressContactName; }
public void setPressContactName(String pressContactName) { this.pressContactName = pressContactName; }

public String getPressContactEmail() { return pressContactEmail; }
public void setPressContactEmail(String pressContactEmail) { this.pressContactEmail = pressContactEmail; }

}
