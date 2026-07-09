package com.magazine.app.config;

import com.magazine.app.model.*;
import com.magazine.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private AdminUserRepository  adminUserRepository;
    @Autowired private CategoryRepository   categoryRepository;
    @Autowired private ArticleRepository    articleRepository;
    @Autowired private PasswordEncoder      passwordEncoder;

    @Override
    public void run(String... args) {

        // ── Default admin user ──────────────────────
        if (adminUserRepository.findByUsername("admin").isEmpty()) {
            adminUserRepository.save(
                new AdminUser("admin", passwordEncoder.encode("admin123"), "ROLE_ADMIN")
            );
            System.out.println(">>> Admin created: admin / admin123");
        }

        if (categoryRepository.count() > 0) return; // already seeded

        // ── Seed categories ─────────────────────────
        Category leadership = categoryRepository.save(new Category("Leadership",  "leadership",  "Insights on leadership and management"));
        Category tech       = categoryRepository.save(new Category("Technology",  "technology",  "Tech trends shaping business"));
        Category startups   = categoryRepository.save(new Category("Startups",    "startups",    "Stories from the startup world"));
        Category finance    = categoryRepository.save(new Category("Finance",     "finance",     "Finance and investment insights"));

        // ── Seed regular articles ───────────────────
        save(articleOf("How Modern Leaders Build Resilient Teams",
            "how-modern-leaders-build-resilient-teams",
            "A look at the leadership habits driving resilient, high-performing teams in 2026.",
            "Resilient teams aren't built overnight. They are the result of consistent, intentional leadership choices around trust, communication, and psychological safety.",
            leadership, ArticleType.NEWS, true));

        save(articleOf("The Rise of AI-Native Startups",
            "the-rise-of-ai-native-startups",
            "Why a new wave of startups is being built AI-first from day one.",
            "Unlike companies that bolted AI onto existing products, a new generation of startups is designed AI-native from the ground up.",
            startups, ArticleType.NEWS, true));

        save(articleOf("Smart Capital: Where Investors Are Looking in 2026",
            "smart-capital-investors-2026",
            "A breakdown of the sectors attracting serious investor attention this year.",
            "Investor appetite has shifted notably this year toward infrastructure, climate tech, and applied AI tooling.",
            finance, ArticleType.NEWS, false));

        save(articleOf("Cloud Infrastructure Trends Every CTO Should Watch",
            "cloud-infrastructure-trends-cto",
            "Key infrastructure shifts reshaping enterprise technology strategy.",
            "From edge computing to cost-optimised multi-cloud strategies, CTOs are rethinking infrastructure decisions.",
            tech, ArticleType.NEWS, true));

        // ── Seed interviews ─────────────────────────
        save(interviewOf(
            "\"Building a Billion Dollar Company From Scratch\" — Rahul Sharma, CEO of TechVentures",
            "building-billion-dollar-company-rahul-sharma",
            "We sat down with one of India's most successful founders to understand what it really takes to build at scale.",
            "<p><strong>Q: What was the hardest part of the first year?</strong></p>"
            + "<p>The hardest part was not the lack of money — it was the uncertainty. Every decision felt like it could be the one that ends everything. You just have to keep moving.</p>"
            + "<p><strong>Q: How did you find your first customers?</strong></p>"
            + "<p>We did things that didn't scale. We knocked on doors, made cold calls, and sat with customers watching them use the product. There are no shortcuts to understanding your customer.</p>"
            + "<p><strong>Q: What advice would you give your younger self?</strong></p>"
            + "<p>Trust the process. The compounding effect of consistent effort is real, but you can't see it in year one. You can only see it in year five. Stay in the game.</p>",
            leadership,
            "Rahul Sharma", "Founder & CEO", "TechVentures India", true
        ));

        save(interviewOf(
            "\"Why I Turned Down a $50M Acquisition Offer\" — Priya Kumar, Founder of FinEdge",
            "turned-down-acquisition-priya-kumar-finedge",
            "The decision that redefined FinEdge's trajectory — and what Priya Kumar learned about conviction.",
            "<p><strong>Q: Walk us through the moment you got the offer.</strong></p>"
            + "<p>It was a Monday morning. The number looked life-changing on paper. But when I asked myself whether their mission matched ours, the answer was clearly no.</p>"
            + "<p><strong>Q: Was the team aligned with your decision?</strong></p>"
            + "<p>Not initially. Some people were excited about the exit. It took a hard conversation about why we started and who we were building for. Once we reconnected with that, everyone was back on board.</p>"
            + "<p><strong>Q: What happened next?</strong></p>"
            + "<p>We raised our Series B six months later at a valuation three times higher than the acquisition offer. Conviction pays.</p>",
            finance,
            "Priya Kumar", "Founder & CEO", "FinEdge Solutions", true
        ));

        save(interviewOf(
            "\"The Future of Enterprise AI is Infrastructure\" — Arjun Mehta, CTO of CloudBase",
            "future-enterprise-ai-infrastructure-arjun-mehta",
            "Why the next decade belongs to whoever builds the compute layer — and what India's CTO community should be thinking about right now.",
            "<p><strong>Q: Everyone talks about AI at the application layer. Why do you focus on infrastructure?</strong></p>"
            + "<p>Because applications come and go. The infrastructure layer compounds. Whoever owns compute, storage, and networking owns the next decade of enterprise software. It's that simple.</p>"
            + "<p><strong>Q: How is India positioned in this race?</strong></p>"
            + "<p>Better than people think. We have the engineering talent and the cost advantage. What we're missing is patient capital willing to fund 5-7 year infrastructure bets. That's changing.</p>",
            tech,
            "Arjun Mehta", "Chief Technology Officer", "CloudBase Systems", false
        ));

        // ── Seed news articles ──────────────────────────────
Article news1 = new Article();
news1.setTitle("RBI Holds Interest Rates Steady for Third Consecutive Quarter");
news1.setSlug("rbi-holds-interest-rates-q3-2026");
news1.setSummary("The Reserve Bank of India kept its key repo rate unchanged at 6.5%, citing easing inflation and global headwinds.");
news1.setContent("<p>The Monetary Policy Committee (MPC) voted unanimously to hold the repo rate at 6.5% in its July meeting. Governor Shaktikanta Das said the committee would remain focused on withdrawing accommodation to ensure inflation aligns durably with the target.</p><p>Markets reacted positively, with the Sensex rising 1.2% on the news. Bond yields dipped slightly as investors adjusted expectations for the rate cycle.</p>");
news1.setAuthorName("Editorial Team");
news1.setArticleType(ArticleType.NEWS);
news1.setNewsSource("Reuters");
news1.setBreaking(true);
news1.setCategory(finance);
news1.setPublished(true);
news1.setFeatured(false);
articleRepository.save(news1);

Article news2 = new Article();
news2.setTitle("Sensex Breaches 85,000 Mark for First Time in History");
news2.setSlug("sensex-85000-all-time-high-2026");
news2.setSummary("India's benchmark index crossed the 85,000 level intraday, driven by FII inflows and strong Q1 corporate earnings.");
news2.setContent("<p>The BSE Sensex crossed the historic 85,000 mark during Thursday's trading session, propelled by strong buying in banking, IT, and energy stocks. Foreign institutional investors (FIIs) pumped in over ₹4,200 crore in a single session.</p><p>Analysts attributed the rally to a combination of positive domestic macro data and easing global uncertainty.</p>");
news2.setAuthorName("Editorial Team");
news2.setArticleType(ArticleType.NEWS);
news2.setNewsSource("Bloomberg");
news2.setBreaking(false);
news2.setCategory(finance);
news2.setPublished(true);
news2.setFeatured(false);
articleRepository.save(news2);

Article news3 = new Article();
news3.setTitle("Infosys Launches AI-Native BPO Division With $100M Investment");
news3.setSlug("infosys-ai-bpo-division-launch");
news3.setSummary("The IT giant's new unit will automate back-office operations for 200+ global enterprise clients using generative AI.");
news3.setContent("<p>Infosys announced the launch of its AI-native Business Process Operations (BPO) division, backed by a $100 million investment over two years. The unit will initially serve clients in banking, insurance, and retail sectors.</p><p>CEO Salil Parekh said the division represents the company's biggest single bet on applied AI in its 40-year history.</p>");
news3.setAuthorName("Editorial Team");
news3.setArticleType(ArticleType.NEWS);
news3.setNewsSource("Economic Times");
news3.setBreaking(false);
news3.setCategory(tech);
news3.setPublished(true);
news3.setFeatured(false);
articleRepository.save(news3);
        
        System.out.println(">>> Sample data seeded: categories, articles, interviews.");
    }

    // ── Helpers ─────────────────────────────────────
    private Article articleOf(String title, String slug, String summary,
                               String content, Category cat,
                               ArticleType type, boolean featured) {
        Article a = new Article();
        a.setTitle(title);       a.setSlug(slug);
        a.setSummary(summary);   a.setContent(content);
        a.setAuthorName("Editorial Team");
        a.setCategory(cat);      a.setArticleType(type);
        a.setFeatured(featured); a.setPublished(true);
        return a;
    }

    private Article interviewOf(String title, String slug, String summary,
                                 String content, Category cat,
                                 String name, String jobTitle,
                                 String company, boolean featured) {
        Article a = new Article();
        a.setTitle(title);           a.setSlug(slug);
        a.setSummary(summary);       a.setContent(content);
        a.setAuthorName("Editorial Team");
        a.setCategory(cat);          a.setArticleType(ArticleType.INTERVIEW);
        a.setIntervieweeName(name);  a.setIntervieweeTitle(jobTitle);
        a.setIntervieweeCompany(company);
        a.setFeatured(featured);     a.setPublished(true);
        return a;
    }

    private void save(Article a) {
        articleRepository.save(a);
    }
}
