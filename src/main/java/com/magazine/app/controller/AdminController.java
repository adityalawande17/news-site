package com.magazine.app.controller;

import com.magazine.app.model.Article;
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

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private ArticleService articleService;
    @Autowired private CategoryService categoryService;
    @Autowired private FileStorageService fileStorageService;

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalArticles", articleService.countAll());
        model.addAttribute("publishedArticles", articleService.countPublished());
        model.addAttribute("totalCategories", categoryService.getAll().size());
        model.addAttribute("recentArticles", articleService.getAllArticles().stream().limit(5).toList());
        return "admin/dashboard";
    }

    // ---- Articles ----
    @GetMapping("/articles")
    public String listArticles(Model model) {
        model.addAttribute("articles", articleService.getAllArticles());
        return "admin/article-list";
    }

    @GetMapping("/articles/new")
    public String newArticleForm(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("isEdit", false);
        return "admin/article-form";
    }

    @GetMapping("/articles/edit/{id}")
    public String editArticleForm(@PathVariable Long id, Model model) {
        Article article = articleService.getById(id).orElseThrow();
        model.addAttribute("article", article);
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("isEdit", true);
        return "admin/article-form";
    }

    @PostMapping("/articles/save")
    public String saveArticle(@Valid @ModelAttribute("article") Article article,
                               BindingResult result,
                               @RequestParam(value = "categoryId", required = false) Long categoryId,
                               @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAll());
            model.addAttribute("isEdit", article.getId() != null);
            return "admin/article-form";
        }

        if (article.getSlug() == null || article.getSlug().isBlank()) {
            article.setSlug(articleService.generateSlug(article.getTitle()));
        }

        if (categoryId != null) {
            Category category = categoryService.getById(categoryId).orElse(null);
            article.setCategory(category);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String path = fileStorageService.store(imageFile);
                article.setImageUrl(path);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Image upload failed.");
            }
        } else if (article.getId() != null) {
            // keep existing image if editing and no new file uploaded
            Article existing = articleService.getById(article.getId()).orElse(null);
            if (existing != null) article.setImageUrl(existing.getImageUrl());
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

    // ---- Categories ----
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
    public String saveCategory(@Valid @ModelAttribute("category") Category category,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", category.getId() != null);
            return "admin/category-form";
        }
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("success", "Category saved successfully.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Category deleted.");
        return "redirect:/admin/categories";
    }
}
