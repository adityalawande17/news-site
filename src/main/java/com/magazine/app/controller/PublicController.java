package com.magazine.app.controller;

import com.magazine.app.model.Article;
import com.magazine.app.model.Category;
import com.magazine.app.service.ArticleService;
import com.magazine.app.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
public class PublicController {

    @Autowired private ArticleService articleService;
    @Autowired private CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {
        List<Category> categories = categoryService.getAll();
        List<Article> featured = articleService.getFeaturedArticles();

        // Build a map: category slug → list of up to 5 published articles
        Map<String, List<Article>> catArticles = new LinkedHashMap<>();
        for (Category cat : categories) {
            Page<Article> page = articleService.getPublishedArticlesByCategory(cat, 0, 5);
            if (!page.isEmpty()) {
                catArticles.put(cat.getSlug(), page.getContent());
            }
        }

        model.addAttribute("featured", featured);
        model.addAttribute("categories", categories);
        model.addAttribute("catArticles", catArticles);
        return "index";
    }

    @GetMapping("/blog")
    public String blogList(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Article> articles = articleService.getPublishedArticles(page, 9);
        List<Category> categories = categoryService.getAll();
        model.addAttribute("articles", articles);
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("featured", articleService.getFeaturedArticles());
        return "blog-list";
    }

    @GetMapping("/category/{slug}")
    public String byCategory(@PathVariable String slug,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        Category category = categoryService.getBySlug(slug).orElseThrow();
        Page<Article> articles = articleService.getPublishedArticlesByCategory(category, page, 9);
        List<Category> categories = categoryService.getAll();
        model.addAttribute("articles", articles);
        model.addAttribute("category", category);
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("featured", articleService.getFeaturedArticles());
        return "category";
    }

    @GetMapping("/article/{slug}")
    public String articleDetail(@PathVariable String slug, Model model) {
        Article article = articleService.getBySlug(slug).orElseThrow();
        articleService.incrementViews(article);
        List<Category> categories = categoryService.getAll();
        List<Article> featured = articleService.getFeaturedArticles();
        model.addAttribute("article", article);
        model.addAttribute("categories", categories);
        model.addAttribute("latest", articleService.getLatestArticles());
        model.addAttribute("featured", featured);
        return "article-detail";
    }

    @GetMapping("/search")
    public String search(@RequestParam String q, Model model) {
        List<Category> categories = categoryService.getAll();
        List<Article> featured = articleService.getFeaturedArticles();
        model.addAttribute("results", articleService.search(q));
        model.addAttribute("query", q);
        model.addAttribute("categories", categories);
        model.addAttribute("featured", featured);
        return "search";
    }

    @GetMapping("/about")
    public String about(Model model) {
        List<Category> categories = categoryService.getAll();
        List<Article> featured = articleService.getFeaturedArticles();
        model.addAttribute("categories", categories);
        model.addAttribute("featured", featured);
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        List<Category> categories = categoryService.getAll();
        List<Article> featured = articleService.getFeaturedArticles();
        model.addAttribute("categories", categories);
        model.addAttribute("featured", featured);
        return "contact";
    }

    @PostMapping("/contact")
    public String contactSubmit(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam(required = false) String subject,
                                @RequestParam String message,
                                RedirectAttributes redirectAttributes) {
        System.out.printf("Contact form: %s (%s) — %s%n", name, email, message);
        redirectAttributes.addFlashAttribute("contactSuccess",
            "Thanks " + name + "! We've received your message and will be in touch soon.");
        return "redirect:/contact";
    }
}
