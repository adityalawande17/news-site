package com.magazine.app.controller;

import com.magazine.app.model.Article;
import com.magazine.app.model.ArticleType;
import com.magazine.app.model.Category;
import com.magazine.app.service.ArticleService;
import com.magazine.app.service.CategoryService;
import com.magazine.app.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.magazine.app.model.Video;
import com.magazine.app.service.VideoService;
import com.magazine.app.model.Magazine;
import com.magazine.app.service.MagazineService;
import java.util.List;
import com.magazine.app.model.MagazineRegion;
import com.magazine.app.model.Company;
import com.magazine.app.service.CompanyService;
import com.magazine.app.model.FundingStage;
import com.magazine.app.model.Startup;
import com.magazine.app.service.StartupService;

import java.io.IOException;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private ArticleService articleService;
    @Autowired private CategoryService categoryService;
    @Autowired private FileStorageService fileStorageService;
    @Autowired private VideoService videoService;
    @Autowired private MagazineService magazineService;
    @Autowired private CompanyService companyService;
    @Autowired private StartupService startupService;

    // ── Login ───────────────────────────────────────
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    // ── Dashboard ───────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalArticles",    articleService.countAll());
        model.addAttribute("publishedArticles", articleService.countPublished());
        model.addAttribute("totalCategories",  categoryService.getAll().size());
        model.addAttribute("totalInterviews",  articleService.countByType(ArticleType.INTERVIEW));
        model.addAttribute("totalNews",  articleService.countByType(ArticleType.NEWS));
        model.addAttribute("totalCaseStudies",
    articleService.countByType(ArticleType.CASE_STUDY));
        model.addAttribute("recentArticles",   articleService.getAllArticles().stream().limit(8).toList());
        model.addAttribute("totalPressReleases",
    articleService.countByType(ArticleType.PRESS_RELEASE));
    model.addAttribute("totalVideos", videoService.countAll());
    model.addAttribute("totalMagazines", magazineService.countAll());
    model.addAttribute("totalCompanies", companyService.countAll());
    model.addAttribute("totalStartups", startupService.countAll());

        return "admin/dashboard";
    }

    // ── Articles (all types) ────────────────────────
    @GetMapping("/articles")
    public String listArticles(Model model) {
        model.addAttribute("articles", articleService.getAllArticles());
        return "admin/article-list";
    }

    @GetMapping("/articles/new")
    public String newArticleForm(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("articleTypes", ArticleType.values());
        model.addAttribute("isEdit", false);
        return "admin/article-form";
    }

    @GetMapping("/articles/edit/{id}")
    public String editArticleForm(@PathVariable Long id, Model model) {
        model.addAttribute("article", articleService.getById(id).orElseThrow());
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("articleTypes", ArticleType.values());
        model.addAttribute("isEdit", true);
        return "admin/article-form";
    }

    @PostMapping("/articles/save")
    public String saveArticle(
            @Valid @ModelAttribute("article") Article article,
            BindingResult result,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "intervieweePhotoFile", required = false) MultipartFile intervieweePhotoFile,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAll());
            model.addAttribute("articleTypes", ArticleType.values());
            model.addAttribute("isEdit", article.getId() != null);
            return "admin/article-form";
        }

        // Auto-generate slug if blank
        if (article.getSlug() == null || article.getSlug().isBlank()) {
            article.setSlug(articleService.generateSlug(article.getTitle()));
        }

        // Set category
        if (categoryId != null) {
            categoryService.getById(categoryId).ifPresent(article::setCategory);
        }

        // Cover image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                article.setImageUrl(fileStorageService.store(imageFile));
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Cover image upload failed.");
            }
        } else if (article.getId() != null) {
            // Keep existing cover image when editing
            articleService.getById(article.getId())
                .ifPresent(ex -> article.setImageUrl(ex.getImageUrl()));
        }

        // Interviewee photo upload — only when type is INTERVIEW
        if (article.getArticleType() == ArticleType.INTERVIEW
                && intervieweePhotoFile != null
                && !intervieweePhotoFile.isEmpty()) {
            try {
                article.setIntervieweePhoto(fileStorageService.store(intervieweePhotoFile));
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Interviewee photo upload failed.");
            }
        } else if (article.getId() != null) {
            // Keep existing interviewee photo when editing
            articleService.getById(article.getId())
                .ifPresent(ex -> article.setIntervieweePhoto(ex.getIntervieweePhoto()));
        }

        articleService.save(article);
        redirectAttributes.addFlashAttribute("success", "Article saved successfully.");
        return "redirect:/admin/articles";
    }

    @PostMapping("/articles/delete/{id}")
    public String deleteArticle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        articleService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Article deleted.");
        return "redirect:/admin/articles";
    }

    // ── Interviews (dedicated admin section) ────────
    @GetMapping("/interviews")
    public String listInterviews(Model model) {
        model.addAttribute("interviews",
            articleService.getByType(ArticleType.INTERVIEW, 0, 100).getContent());
        return "admin/interview-list";
    }

    @GetMapping("/interviews/new")
    public String newInterviewForm(Model model) {
        Article article = new Article();
        article.setArticleType(ArticleType.INTERVIEW); // pre-select type
        model.addAttribute("article", article);
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("articleTypes", ArticleType.values());
        model.addAttribute("isEdit", false);
        return "admin/article-form"; // reuses same form — type is pre-selected
    }

    @GetMapping("/interviews/edit/{id}")
    public String editInterviewForm(@PathVariable Long id, Model model) {
        model.addAttribute("article", articleService.getById(id).orElseThrow());
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("articleTypes", ArticleType.values());
        model.addAttribute("isEdit", true);
        return "admin/article-form";
    }

    @PostMapping("/interviews/delete/{id}")
    public String deleteInterview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        articleService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Interview deleted.");
        return "redirect:/admin/interviews";
    }

    // ── Categories ──────────────────────────────────
    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAll());
        return "admin/category-list";
    }

    @GetMapping("/categories/new")
    public String newCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("isEdit", false);
        return "admin/category-form";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getById(id).orElseThrow());
        model.addAttribute("isEdit", true);
        return "admin/category-form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(
            @Valid @ModelAttribute("category") Category category,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", category.getId() != null);
            return "admin/category-form";
        }
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("success", "Category saved.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Category deleted.");
        return "redirect:/admin/categories";
    }

    @GetMapping("/news")
    public String listNews(Model model) {
        model.addAttribute("newsList",
            articleService.getByType(ArticleType.NEWS, 0, 100).getContent());
        return "admin/news-list";
    }

    @GetMapping("/news/new")
    public String newNewsForm(Model model) {
        Article article = new Article();
        article.setArticleType(ArticleType.NEWS);
        model.addAttribute("article", article);
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("articleTypes", ArticleType.values());
        model.addAttribute("isEdit", false);
        return "admin/article-form";
    }

    @GetMapping("/news/edit/{id}")
    public String editNewsForm(@PathVariable Long id, Model model) {
        model.addAttribute("article", articleService.getById(id).orElseThrow());
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("articleTypes", ArticleType.values());
        model.addAttribute("isEdit", true);
        return "admin/article-form";
    }
    @PostMapping("/news/delete/{id}")
        public String deleteNews(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        articleService.delete(id);
        redirectAttributes.addFlashAttribute("success", "News article deleted.");
        return "redirect:/admin/news";
    }

    //case study section

    // ── Case Studies ─────────────────────────────────────
@GetMapping("/case-studies")
public String listCaseStudies(Model model) {
    model.addAttribute("caseStudies",
        articleService.getByType(ArticleType.CASE_STUDY, 0, 100).getContent());
    return "admin/case-study-list";
}

@GetMapping("/case-studies/new")
public String newCaseStudyForm(Model model) {
    Article article = new Article();
    article.setArticleType(ArticleType.CASE_STUDY);
    model.addAttribute("article",      article);
    model.addAttribute("categories",   categoryService.getAll());
    model.addAttribute("articleTypes", ArticleType.values());
    model.addAttribute("isEdit",       false);
    return "admin/article-form";
}

@GetMapping("/case-studies/edit/{id}")
public String editCaseStudyForm(@PathVariable Long id, Model model) {
    model.addAttribute("article",      articleService.getById(id).orElseThrow());
    model.addAttribute("categories",   categoryService.getAll());
    model.addAttribute("articleTypes", ArticleType.values());
    model.addAttribute("isEdit",       true);
    return "admin/article-form";
}

@PostMapping("/case-studies/delete/{id}")
public String deleteCaseStudy(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
    articleService.delete(id);
    redirectAttributes.addFlashAttribute("success", "Case study deleted.");
    return "redirect:/admin/case-studies";
}

// ── Press Releases ───────────────────────────────────
@GetMapping("/press-releases")
public String listPressReleases(Model model) {
    model.addAttribute("pressReleases",
        articleService.getByType(ArticleType.PRESS_RELEASE, 0, 100).getContent());
    return "admin/press-release-list";
}

@GetMapping("/press-releases/new")
public String newPressReleaseForm(Model model) {
    Article article = new Article();
    article.setArticleType(ArticleType.PRESS_RELEASE);
    model.addAttribute("article",      article);
    model.addAttribute("categories",   categoryService.getAll());
    model.addAttribute("articleTypes", ArticleType.values());
    model.addAttribute("isEdit",       false);
    return "admin/article-form";
}

@GetMapping("/press-releases/edit/{id}")
public String editPressReleaseForm(@PathVariable Long id, Model model) {
    model.addAttribute("article",      articleService.getById(id).orElseThrow());
    model.addAttribute("categories",   categoryService.getAll());
    model.addAttribute("articleTypes", ArticleType.values());
    model.addAttribute("isEdit",       true);
    return "admin/article-form";
}

@PostMapping("/press-releases/delete/{id}")
public String deletePressRelease(@PathVariable Long id,
                                  RedirectAttributes redirectAttributes) {
    articleService.delete(id);
    redirectAttributes.addFlashAttribute("success", "Press release deleted.");
    return "redirect:/admin/press-releases";
}

// ── Videos ───────────────────────────────────────────
@GetMapping("/videos")
public String listVideos(Model model) {
    model.addAttribute("videos", videoService.getAll());
    return "admin/video-list";
}

@GetMapping("/videos/new")
public String newVideoForm(Model model) {
    model.addAttribute("video",      new Video());
    model.addAttribute("categories", categoryService.getAll());
    model.addAttribute("isEdit",     false);
    return "admin/video-form";
}

@GetMapping("/videos/edit/{id}")
public String editVideoForm(@PathVariable Long id, Model model) {
    model.addAttribute("video",      videoService.getById(id).orElseThrow());
    model.addAttribute("categories", categoryService.getAll());
    model.addAttribute("isEdit",     true);
    return "admin/video-form";
}

@PostMapping("/videos/save")
public String saveVideo(
        @Valid @ModelAttribute("video") Video video,
        BindingResult result,
        @RequestParam(value = "categoryId", required = false) Long categoryId,
        @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
        Model model,
        RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("isEdit", video.getId() != null);
        return "admin/video-form";
    }

    // Auto-generate slug
    if (video.getSlug() == null || video.getSlug().isBlank()) {
        video.setSlug(videoService.generateSlug(video.getTitle()));
    }

    // Set category
    if (categoryId != null) {
        categoryService.getById(categoryId).ifPresent(video::setCategory);
    }

    // Convert watch URL to embed URL
    video.setEmbedUrl(videoService.toEmbedUrl(video.getEmbedUrl()));

    // Thumbnail: uploaded file takes priority
    if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
        try {
            video.setThumbnailUrl(fileStorageService.store(thumbnailFile));
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error",
                "Thumbnail upload failed.");
        }
    } else if (video.getThumbnailUrl() == null
               || video.getThumbnailUrl().isBlank()) {
        // Auto-extract YouTube thumbnail if no image uploaded
        String autoThumb =
            videoService.extractYoutubeThumbnail(video.getEmbedUrl());
        if (autoThumb != null) video.setThumbnailUrl(autoThumb);
    } else if (video.getId() != null) {
        // Keep existing thumbnail when editing
        videoService.getById(video.getId())
            .ifPresent(ex -> video.setThumbnailUrl(ex.getThumbnailUrl()));
    }

    videoService.save(video);
    redirectAttributes.addFlashAttribute("success", "Video saved.");
    return "redirect:/admin/videos";
}

@PostMapping("/videos/delete/{id}")
public String deleteVideo(@PathVariable Long id,
                           RedirectAttributes redirectAttributes) {
    videoService.delete(id);
    redirectAttributes.addFlashAttribute("success", "Video deleted.");
    return "redirect:/admin/videos";
}

// ── Magazine ─────────────────────────────────────────
@GetMapping("/magazine")
public String listMagazines(Model model) {
    model.addAttribute("magazines", magazineService.getAll());
    return "admin/magazine-list";
}

@GetMapping("/magazine/new")
public String newMagazineForm(Model model) {
    model.addAttribute("magazine",  new Magazine());
    model.addAttribute("articles",  articleService.getAllArticles());
    model.addAttribute("isEdit",    false);
    model.addAttribute("regions", MagazineRegion.values());
    return "admin/magazine-form";
}

@GetMapping("/magazine/edit/{id}")
public String editMagazineForm(@PathVariable Long id, Model model) {
    model.addAttribute("magazine",  magazineService.getById(id).orElseThrow());
    model.addAttribute("articles",  articleService.getAllArticles());
    model.addAttribute("isEdit",    true);
    model.addAttribute("regions", MagazineRegion.values());
    return "admin/magazine-form";
}

@PostMapping("/magazine/save")
public String saveMagazine(
        @Valid @ModelAttribute("magazine") Magazine magazine,
        BindingResult result,
        @RequestParam(value = "articleIds", required = false) List<Long> articleIds,
        @RequestParam(value = "coverImageFile", required = false) MultipartFile coverImageFile,
        Model model,
        RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
        model.addAttribute("articles", articleService.getAllArticles());
        model.addAttribute("isEdit", magazine.getId() != null);
        model.addAttribute("regions", MagazineRegion.values());
        return "admin/magazine-form";
    }

    if (magazine.getSlug() == null || magazine.getSlug().isBlank()) {
        magazine.setSlug(magazineService.generateSlug(magazine.getTitle()));
    }

    if (coverImageFile != null && !coverImageFile.isEmpty()) {
        try {
            magazine.setCoverImageUrl(fileStorageService.store(coverImageFile));
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Cover upload failed.");
        }
    } else if (magazine.getId() != null) {
        magazineService.getById(magazine.getId())
            .ifPresent(ex -> magazine.setCoverImageUrl(ex.getCoverImageUrl()));
    }

    magazineService.save(magazine, articleIds);
    redirectAttributes.addFlashAttribute("success", "Magazine issue saved.");
    return "redirect:/admin/magazine";
}

@PostMapping("/magazine/delete/{id}")
public String deleteMagazine(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {
    magazineService.delete(id);
    redirectAttributes.addFlashAttribute("success", "Issue deleted.");
    return "redirect:/admin/magazine";
}

// ── Companies ────────────────────────────────────────
@GetMapping("/companies")
public String listCompanies(Model model) {
    model.addAttribute("companies", companyService.getAll());
    return "admin/company-list";
}

@GetMapping("/companies/new")
public String newCompanyForm(Model model) {
    model.addAttribute("company", new Company());
    model.addAttribute("isEdit",  false);
    return "admin/company-form";
}

@GetMapping("/companies/edit/{id}")
public String editCompanyForm(@PathVariable Long id, Model model) {
    model.addAttribute("company", companyService.getById(id).orElseThrow());
    model.addAttribute("isEdit",  true);
    return "admin/company-form";
}

@PostMapping("/companies/save")
public String saveCompany(
        @ModelAttribute("company") Company company,
        @RequestParam(value = "logoFile",  required = false) MultipartFile logoFile,
        @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
        RedirectAttributes redirectAttributes) {

    if (company.getSlug() == null || company.getSlug().isBlank()) {
        company.setSlug(companyService.generateSlug(company.getName()));
    }

    if (logoFile != null && !logoFile.isEmpty()) {
        try {
            company.setLogoUrl(fileStorageService.store(logoFile));
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Logo upload failed.");
        }
    } else if (company.getId() != null) {
        companyService.getById(company.getId())
            .ifPresent(ex -> company.setLogoUrl(ex.getLogoUrl()));
    }

    if (coverFile != null && !coverFile.isEmpty()) {
        try {
            company.setCoverImageUrl(fileStorageService.store(coverFile));
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Cover upload failed.");
        }
    } else if (company.getId() != null) {
        companyService.getById(company.getId())
            .ifPresent(ex -> company.setCoverImageUrl(ex.getCoverImageUrl()));
    }

    companyService.save(company);
    redirectAttributes.addFlashAttribute("success", "Company saved.");
    return "redirect:/admin/companies";
}

@PostMapping("/companies/delete/{id}")
public String deleteCompany(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
    companyService.delete(id);
    redirectAttributes.addFlashAttribute("success", "Company deleted.");
    return "redirect:/admin/companies";
}

// ── Startups ─────────────────────────────────────────
@GetMapping("/startups")
public String listStartups(Model model) {
    model.addAttribute("startups", startupService.getAll());
    return "admin/startup-list";
}

@GetMapping("/startups/new")
public String newStartupForm(Model model) {
    model.addAttribute("startup",       new Startup());
    model.addAttribute("fundingStages", FundingStage.values());
    model.addAttribute("isEdit",        false);
    return "admin/startup-form";
}

@GetMapping("/startups/edit/{id}")
public String editStartupForm(@PathVariable Long id, Model model) {
    model.addAttribute("startup",       startupService.getById(id).orElseThrow());
    model.addAttribute("fundingStages", FundingStage.values());
    model.addAttribute("isEdit",        true);
    return "admin/startup-form";
}

@PostMapping("/startups/save")
public String saveStartup(
        @ModelAttribute("startup") Startup startup,
        @RequestParam(value = "logoFile",        required = false) MultipartFile logoFile,
        @RequestParam(value = "coverFile",       required = false) MultipartFile coverFile,
        @RequestParam(value = "founderPhotoFile",required = false) MultipartFile founderPhotoFile,
        RedirectAttributes redirectAttributes) {

    if (startup.getSlug() == null || startup.getSlug().isBlank()) {
        startup.setSlug(startupService.generateSlug(startup.getName()));
    }

    // Logo
    if (logoFile != null && !logoFile.isEmpty()) {
        try {
            startup.setLogoUrl(fileStorageService.store(logoFile));
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Logo upload failed.");
        }
    } else if (startup.getId() != null) {
        startupService.getById(startup.getId())
            .ifPresent(ex -> startup.setLogoUrl(ex.getLogoUrl()));
    }

    // Cover image
    if (coverFile != null && !coverFile.isEmpty()) {
        try {
            startup.setCoverImageUrl(fileStorageService.store(coverFile));
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Cover upload failed.");
        }
    } else if (startup.getId() != null) {
        startupService.getById(startup.getId())
            .ifPresent(ex -> startup.setCoverImageUrl(ex.getCoverImageUrl()));
    }

    // Founder photo
    if (founderPhotoFile != null && !founderPhotoFile.isEmpty()) {
        try {
            startup.setFounderPhoto(fileStorageService.store(founderPhotoFile));
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Founder photo upload failed.");
        }
    } else if (startup.getId() != null) {
        startupService.getById(startup.getId())
            .ifPresent(ex -> startup.setFounderPhoto(ex.getFounderPhoto()));
    }

    startupService.save(startup);
    redirectAttributes.addFlashAttribute("success", "Startup saved.");
    return "redirect:/admin/startups";
}

@PostMapping("/startups/delete/{id}")
public String deleteStartup(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
    startupService.delete(id);
    redirectAttributes.addFlashAttribute("success", "Startup deleted.");
    return "redirect:/admin/startups";
}
}
