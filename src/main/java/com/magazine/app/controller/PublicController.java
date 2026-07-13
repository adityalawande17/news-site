package com.magazine.app.controller;

import com.magazine.app.model.Article;
import com.magazine.app.model.ArticleType;
import com.magazine.app.model.Category;
import com.magazine.app.service.ArticleService;
import com.magazine.app.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.magazine.app.model.Video;
import com.magazine.app.service.VideoService;
import com.magazine.app.model.Magazine;
import com.magazine.app.service.MagazineService;
import com.magazine.app.model.MagazineRegion;
import com.magazine.app.model.Company;
import com.magazine.app.service.CompanyService;
import com.magazine.app.model.FundingStage;
import com.magazine.app.model.Startup;
import com.magazine.app.service.StartupService;

import java.util.*;

@Controller
public class PublicController {

    @Autowired private ArticleService articleService;
    @Autowired private CategoryService categoryService;
    @Autowired private VideoService videoService;
    @Autowired private MagazineService magazineService;
    @Autowired private CompanyService companyService;
    @Autowired private StartupService startupService;

    // ── Homepage ────────────────────────────────────
    @GetMapping("/")
    public String home(Model model) {
        List<Category> categories  = categoryService.getAll();
        List<Article>  featured    = articleService.getFeaturedArticles();

        // Category sections on homepage
        Map<String, List<Article>> catArticles = new LinkedHashMap<>();
        for (Category cat : categories) {
            Page<Article> page = articleService.getPublishedArticlesByCategory(cat, 0, 5);
            if (!page.isEmpty()) catArticles.put(cat.getSlug(), page.getContent());
        }

        model.addAttribute("featured",          featured);
        model.addAttribute("categories",        categories);
        model.addAttribute("catArticles",        catArticles);
        // Interview preview section on homepage — latest 6: first 3 in the
        // slideshow, next 3 in the stacked list
        model.addAttribute("latestInterviews",
            articleService.getLatestByType(ArticleType.INTERVIEW, 6));
        model.addAttribute("latestNews",
            articleService.getLatestByType(ArticleType.NEWS, 3));
        model.addAttribute("latestCaseStudies",
            articleService.getLatestByType(ArticleType.CASE_STUDY, 3));
        model.addAttribute("latestPressReleases",
            articleService.getLatestByType(ArticleType.PRESS_RELEASE, 3));
        model.addAttribute("latestVideos", videoService.getLatest(4));
        // Map of region → latest 4 issue covers for homepage buttons
        model.addAttribute("latestPerRegion", magazineService.getLatest4PerRegion());
        model.addAttribute("regions",         MagazineRegion.values());
        model.addAttribute("featuredCompanies", companyService.getFeatured());
        model.addAttribute("featuredStartups", startupService.getFeatured());

        return "index";
    }

    // ── Blog (all articles paginated) ───────────────
    @GetMapping("/blog")
    public String blogList(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("articles",    articleService.getPublishedArticles(page, 9));
        model.addAttribute("categories",  categoryService.getAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("featured",    articleService.getFeaturedArticles());
        return "blog-list";
    }

    // ── Category filtered listing ───────────────────
    @GetMapping("/category/{slug}")
    public String byCategory(@PathVariable String slug,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        Category category = categoryService.getBySlug(slug).orElseThrow();
        model.addAttribute("articles",    articleService.getPublishedArticlesByCategory(category, page, 9));
        model.addAttribute("category",   category);
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("featured",   articleService.getFeaturedArticles());
        return "category";
    }

    // ── Interviews public listing ───────────────────
    @GetMapping("/interviews")
    public String interviews(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("interviews",  articleService.getByType(ArticleType.INTERVIEW, page, 9));
        model.addAttribute("currentPage", page);
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("featured",   articleService.getFeaturedArticles());
        return "interviews";
    }

    // ── News public listing ─────────────────────────────
    @GetMapping("/news")
    public String news(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("newsList",    articleService.getByType(ArticleType.NEWS, page, 12));
        model.addAttribute("currentPage", page);
        model.addAttribute("categories",  categoryService.getAll());
        model.addAttribute("featured",    articleService.getFeaturedArticles());
        return "news";
    }

    @GetMapping("/case-studies")
    public String caseStudies(@RequestParam(defaultValue = "0") int page,
                           Model model) {
        model.addAttribute("caseStudies",
            articleService.getByType(ArticleType.CASE_STUDY, page, 9));
        model.addAttribute("currentPage", page);
        model.addAttribute("categories",  categoryService.getAll());
        model.addAttribute("featured",    articleService.getFeaturedArticles());
        return "case-studies";
    }

    // ── Article / Interview detail ──────────────────
    @GetMapping("/article/{slug}")
    public String articleDetail(@PathVariable String slug, Model model) {
        Article article = articleService.getBySlug(slug).orElseThrow();
        articleService.incrementViews(article);

        model.addAttribute("article",          article);
        model.addAttribute("categories",       categoryService.getAll());
        model.addAttribute("latest",           articleService.getLatestArticles());
        model.addAttribute("featured",         articleService.getFeaturedArticles());
        model.addAttribute("latestInterviews", articleService.getLatestByType(ArticleType.INTERVIEW, 4));
        model.addAttribute("latestCaseStudies", articleService.getLatestByType(ArticleType.CASE_STUDY, 4));

        // Route to the correct detail template based on article type
        if (article.getArticleType() == ArticleType.INTERVIEW) {
            return "interview-detail";
        }
        if (article.getArticleType() == ArticleType.NEWS
            || article.getArticleType() == ArticleType.INDUSTRY_NEWS
            || article.getArticleType() == ArticleType.MARKET_NEWS) {
            return "news-detail";
        }
        if (article.getArticleType() == ArticleType.CASE_STUDY) {
            return "case-study-detail";
        }
        if (article.getArticleType() == ArticleType.PRESS_RELEASE) {
    return "press-release-detail";
}
        return "article-detail";
    }

    @GetMapping("/press-releases")
public String pressReleases(@RequestParam(defaultValue = "0") int page,
                             Model model) {
    model.addAttribute("pressReleases",
        articleService.getByType(ArticleType.PRESS_RELEASE, page, 12));
    model.addAttribute("currentPage", page);
    model.addAttribute("categories",  categoryService.getAll());
    model.addAttribute("featured",    articleService.getFeaturedArticles());
    return "press-releases";
}

// ── Videos ───────────────────────────────────────────
@GetMapping("/videos")
public String videos(@RequestParam(defaultValue = "0") int page,
                     Model model) {
    model.addAttribute("videos",      videoService.getPublished(page, 12));
    model.addAttribute("currentPage", page);
    model.addAttribute("categories",  categoryService.getAll());
    model.addAttribute("featured",    articleService.getFeaturedArticles());
    return "videos";
}

@GetMapping("/video/{slug}")
public String videoDetail(@PathVariable String slug, Model model) {
    Video video = videoService.getBySlug(slug).orElseThrow();
    videoService.incrementViews(video);
    model.addAttribute("video",       video);
    model.addAttribute("categories",  categoryService.getAll());
    model.addAttribute("featured",    articleService.getFeaturedArticles());
    model.addAttribute("latestVideos", videoService.getLatest(4));
    return "video-detail";
}

// ── Magazine ─────────────────────────────────────────
@GetMapping("/magazine")
public String magazine(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) MagazineRegion region,
        Model model) {

    if (region != null) {
        model.addAttribute("magazines",
            magazineService.getByRegion(region, page, 9));
        model.addAttribute("activeRegion", region);
    } else {
        model.addAttribute("magazines",
            magazineService.getPublished(page, 9));
        model.addAttribute("activeRegion", null);
    }

    model.addAttribute("regions",     MagazineRegion.values());
    model.addAttribute("currentPage", page);
    model.addAttribute("categories",  categoryService.getAll());
    model.addAttribute("featured",    articleService.getFeaturedArticles());
    return "magazine";
}

@GetMapping("/magazine/{slug}")
public String magazineDetail(@PathVariable String slug, Model model) {
    Magazine mag = magazineService.getBySlug(slug).orElseThrow();
    model.addAttribute("magazine",    mag);
    model.addAttribute("categories",  categoryService.getAll());
    model.addAttribute("featured",    articleService.getFeaturedArticles());
    model.addAttribute("latestIssues", magazineService.getLatest(3));
    return "magazine-detail";
}

// ── Companies ────────────────────────────────────────
@GetMapping("/companies")
public String companies(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) String sector,
        Model model) {

    if (sector != null && !sector.isBlank()) {
        model.addAttribute("companies",
            companyService.getBySector(sector, page, 12));
        model.addAttribute("activeSector", sector);
    } else {
        model.addAttribute("companies",
            companyService.getPublished(page, 12));
        model.addAttribute("activeSector", null);
    }

    model.addAttribute("sectors",     companyService.getAllSectors());
    model.addAttribute("currentPage", page);
    model.addAttribute("categories",  categoryService.getAll());
    model.addAttribute("featured",    articleService.getFeaturedArticles());
    return "companies";
}

@GetMapping("/company/{slug}")
public String companyDetail(@PathVariable String slug, Model model) {
    Company company = companyService.getBySlug(slug).orElseThrow();
    model.addAttribute("company",    company);
    model.addAttribute("categories", categoryService.getAll());
    model.addAttribute("featured",   articleService.getFeaturedArticles());
    model.addAttribute("featured_companies",
        companyService.getFeatured());
    return "company-detail";
}

// ── Startups ─────────────────────────────────────────
@GetMapping("/startups")
public String startups(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) String sector,
        @RequestParam(required = false) FundingStage stage,
        Model model) {

    if (sector != null && !sector.isBlank()) {
        model.addAttribute("startups",
            startupService.getBySector(sector, page, 12));
        model.addAttribute("activeSector", sector);
        model.addAttribute("activeStage",  null);
    } else if (stage != null) {
        model.addAttribute("startups",
            startupService.getByFundingStage(stage, page, 12));
        model.addAttribute("activeSector", null);
        model.addAttribute("activeStage",  stage);
    } else {
        model.addAttribute("startups",
            startupService.getPublished(page, 12));
        model.addAttribute("activeSector", null);
        model.addAttribute("activeStage",  null);
    }

    model.addAttribute("sectors",       startupService.getAllSectors());
    model.addAttribute("fundingStages", FundingStage.values());
    model.addAttribute("currentPage",   page);
    model.addAttribute("categories",    categoryService.getAll());
    model.addAttribute("featured",      articleService.getFeaturedArticles());
    return "startups";
}

@GetMapping("/startup/{slug}")
public String startupDetail(@PathVariable String slug, Model model) {
    Startup startup = startupService.getBySlug(slug).orElseThrow();
    model.addAttribute("startup",          startup);
    model.addAttribute("categories",       categoryService.getAll());
    model.addAttribute("featured",         articleService.getFeaturedArticles());
    model.addAttribute("featuredStartups", startupService.getFeatured());
    return "startup-detail";
}

    // ── Search ──────────────────────────────────────
    @GetMapping("/search")
    public String search(@RequestParam String q, Model model) {
        model.addAttribute("results",    articleService.search(q));
        model.addAttribute("query",      q);
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("featured",   articleService.getFeaturedArticles());
        return "search";
    }

    // ── Static pages ────────────────────────────────
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("featured",   articleService.getFeaturedArticles());
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("featured",   articleService.getFeaturedArticles());
        return "contact";
    }

    @PostMapping("/contact")
    public String contactSubmit(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam(required = false) String subject,
                                @RequestParam String message,
                                RedirectAttributes redirectAttributes) {
        System.out.printf("Contact: %s (%s) — %s%n", name, email, message);
        redirectAttributes.addFlashAttribute("contactSuccess",
            "Thanks " + name + "! We'll be in touch soon.");
        return "redirect:/contact";
    }
}
