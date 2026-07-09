package com.magazine.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "startups")
public class Startup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Startup name is required")
    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    // One-line pitch
    @Column(length = 300)
    private String pitch;

    // Longer description
    @Column(length = 2000)
    private String description;

    private String logoUrl;

    private String coverImageUrl;

    private String founderName;

    private String founderTitle;      // e.g. "CEO & Co-Founder"

    private String founderPhoto;

    private String sector;            // e.g. "Fintech", "AgriTech"

    private String city;              // e.g. "Bengaluru"

    private String country;           // e.g. "India"

    private Integer foundingYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "funding_stage")
    private FundingStage fundingStage;

    private String amountRaised;      // e.g. "$8M" or "Rs. 50 Cr"

    private String websiteUrl;

    private String linkedinUrl;

    private String twitterUrl;

    private boolean featured = false;

    private boolean published = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    public Startup() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getPitch() { return pitch; }
    public void setPitch(String pitch) { this.pitch = pitch; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getFounderName() { return founderName; }
    public void setFounderName(String founderName) { this.founderName = founderName; }

    public String getFounderTitle() { return founderTitle; }
    public void setFounderTitle(String founderTitle) { this.founderTitle = founderTitle; }

    public String getFounderPhoto() { return founderPhoto; }
    public void setFounderPhoto(String founderPhoto) { this.founderPhoto = founderPhoto; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Integer getFoundingYear() { return foundingYear; }
    public void setFoundingYear(Integer foundingYear) { this.foundingYear = foundingYear; }

    public FundingStage getFundingStage() { return fundingStage; }
    public void setFundingStage(FundingStage fundingStage) { this.fundingStage = fundingStage; }

    public String getAmountRaised() { return amountRaised; }
    public void setAmountRaised(String amountRaised) { this.amountRaised = amountRaised; }

    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

    public String getTwitterUrl() { return twitterUrl; }
    public void setTwitterUrl(String twitterUrl) { this.twitterUrl = twitterUrl; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}