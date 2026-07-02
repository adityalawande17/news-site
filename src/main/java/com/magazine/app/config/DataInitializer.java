package com.magazine.app.config;

import com.magazine.app.model.AdminUser;
import com.magazine.app.model.Article;
import com.magazine.app.model.Category;
import com.magazine.app.repository.AdminUserRepository;
import com.magazine.app.repository.ArticleRepository;
import com.magazine.app.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private AdminUserRepository adminUserRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ArticleRepository articleRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Seed default admin user
        if (adminUserRepository.findByUsername("admin").isEmpty()) {
            AdminUser admin = new AdminUser("admin", passwordEncoder.encode("admin123"), "ROLE_ADMIN");
            adminUserRepository.save(admin);
            System.out.println(">>> Default admin created: username=admin password=admin123");
        }

        // Seed categories
        if (categoryRepository.count() == 0) {
            Category leadership = categoryRepository.save(new Category("Leadership", "leadership", "Insights on leadership and management"));
            Category tech = categoryRepository.save(new Category("Technology", "technology", "Tech trends shaping business"));
            Category startups = categoryRepository.save(new Category("Startups", "startups", "Stories from the startup world"));
            Category finance = categoryRepository.save(new Category("Finance", "finance", "Finance and investment insights"));

            // Seed a few sample articles
            Article a1 = new Article();
            a1.setTitle("How Modern Leaders Build Resilient Teams");
            a1.setSlug("how-modern-leaders-build-resilient-teams");
            a1.setSummary("A look at the leadership habits driving resilient, high-performing teams in 2026.");
            a1.setContent("Resilient teams aren't built overnight. They are the result of consistent, intentional leadership choices around trust, communication, and psychological safety. In this piece, we explore the habits separating average leaders from exceptional ones, drawing on interviews with executives across industries.");
            a1.setAuthorName("Editorial Team");
            a1.setCategory(leadership);
            a1.setFeatured(true);
            articleRepository.save(a1);

            Article a2 = new Article();
            a2.setTitle("The Rise of AI-Native Startups");
            a2.setSlug("the-rise-of-ai-native-startups");
            a2.setSummary("Why a new wave of startups is being built AI-first from day one.");
            a2.setContent("Unlike companies that bolted AI onto existing products, a new generation of startups is being designed AI-native from the ground up. This shift is changing how products are built, how teams are structured, and how fast companies can scale.");
            a2.setAuthorName("Editorial Team");
            a2.setCategory(startups);
            a2.setFeatured(true);
            articleRepository.save(a2);

            Article a3 = new Article();
            a3.setTitle("Smart Capital: Where Investors Are Looking in 2026");
            a3.setSlug("smart-capital-where-investors-are-looking-2026");
            a3.setSummary("A breakdown of the sectors attracting serious investor attention this year.");
            a3.setContent("Investor appetite has shifted notably this year toward infrastructure, climate tech, and applied AI tooling. We break down the data behind this shift and what it signals for founders raising capital in the current environment.");
            a3.setAuthorName("Editorial Team");
            a3.setCategory(finance);
            articleRepository.save(a3);

            Article a4 = new Article();
            a4.setTitle("Cloud Infrastructure Trends Every CTO Should Watch");
            a4.setSlug("cloud-infrastructure-trends-every-cto-should-watch");
            a4.setSummary("Key infrastructure shifts reshaping enterprise technology strategy.");
            a4.setContent("From edge computing to cost-optimized multi-cloud strategies, CTOs are rethinking infrastructure decisions. This article rounds up the trends worth paying attention to heading into the back half of 2026.");
            a4.setAuthorName("Editorial Team");
            a4.setCategory(tech);
            a4.setFeatured(true);
            articleRepository.save(a4);
        }
    }
}
