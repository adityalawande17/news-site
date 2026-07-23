package com.magazine.app.config;

import com.magazine.app.model.*;
import com.magazine.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private AdminUserRepository  adminUserRepository;
    @Autowired private CategoryRepository   categoryRepository;
    @Autowired private ArticleRepository    articleRepository;
    @Autowired private StartupRepository    startupRepository;
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

        // Tops up categories, interviews, and featured articles on every
        // startup (idempotent) — runs even when the rest of this method
        // short-circuits below because categories already exist from a
        // previous run.
        seedExtraCategories();
        seedExtraInterviews();
        seedExtraFeaturedArticle();
        seedExtraCaseStudiesAndPressReleases();
        seedExtraStartups();
        seedExtraClimateArticles();

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

    // Tops up categories to 10 (idempotent, per-slug) so the nav's category
    // drawer has a full list to show.
    private void seedExtraCategories() {
        addCategoryIfMissing("Healthcare", "healthcare", "Innovation and leadership in healthcare and life sciences");
        addCategoryIfMissing("Marketing",  "marketing",  "Brand strategy, growth, and marketing leadership");
        addCategoryIfMissing("Real Estate","real-estate","Property, development, and real estate investment");
        addCategoryIfMissing("Energy",     "energy",     "Energy markets, sustainability, and infrastructure");
    }

    private void addCategoryIfMissing(String name, String slug, String description) {
        if (categoryRepository.findBySlug(slug).isEmpty()) {
            categoryRepository.save(new Category(name, slug, description));
        }
    }

    // Tops up featured articles to 10 (idempotent) so the homepage hero
    // carousel (3 slides) + side list (7 items) both have content to show.
    private void seedExtraFeaturedArticle() {
        if (articleRepository.countByPublishedTrueAndFeaturedTrue() >= 10) {
            return; // already topped up
        }
        if (articleRepository.findBySlug("global-markets-rally-inflation-cools").isPresent()) {
            return;
        }

        Category finance = categoryRepository.findBySlug("finance").orElse(null);

        Article a = new Article();
        a.setTitle("Global Markets Rally as Inflation Cools Faster Than Expected");
        a.setSlug("global-markets-rally-inflation-cools");
        a.setSummary("Equity markets posted their strongest week in months after fresh data showed inflation easing across major economies.");
        a.setContent(
            "<p>Global equity markets rallied this week after inflation data across the US, Europe, and Asia came in below "
            + "analyst expectations, fueling bets that central banks are nearing the end of their tightening cycles.</p>"
            + "<p>The rally was broad-based, with technology and financial stocks leading gains. Bond yields fell in tandem "
            + "as investors priced in a higher probability of rate cuts later this year.</p>"
            + "<p>Analysts caution that a single month of cooler data isn't a trend, but the market reaction underscores how "
            + "sensitive investor sentiment remains to every incoming inflation print.</p>"
        );
        a.setAuthorName("Editorial Team");
        a.setCategory(finance);
        a.setArticleType(ArticleType.NEWS);
        a.setNewsSource("Market Desk");
        a.setImageUrl("https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=900&auto=format&fit=crop");
        a.setFeatured(true);
        a.setPublished(true);
        articleRepository.save(a);
    }

    // Tops up case studies and press releases to 3 each (idempotent, per-slug)
    // so the homepage's merged 3-column section has a "cover + 2 title-only"
    // set for both.
    private void seedExtraCaseStudiesAndPressReleases() {
        Category finance = categoryRepository.findBySlug("finance").orElse(null);
        Category tech    = categoryRepository.findBySlug("technology").orElse(null);
        Category startups = categoryRepository.findBySlug("startups").orElse(null);

        if (articleRepository.findBySlug("regional-bank-loan-approval-case-study").isEmpty()) {
            Article a = new Article();
            a.setTitle("How a Regional Bank Cut Loan Approval Time by 65%");
            a.setSlug("regional-bank-loan-approval-case-study");
            a.setSummary("Meridian Community Bank restructured its underwriting workflow to move from a 12-day average approval time to just over 4.");
            a.setContent(
                "<p>Meridian Community Bank's loan approval pipeline relied on manual document handoffs between five "
                + "separate departments, each with its own queue and turnaround expectations.</p>"
                + "<p>By consolidating document intake into a single digital workflow and automating routine compliance "
                + "checks, the bank cut average approval time from 12 days to just over 4 — without adding headcount.</p>"
            );
            a.setAuthorName("Editorial Team");
            a.setCategory(finance);
            a.setArticleType(ArticleType.CASE_STUDY);
            a.setClientName("Meridian Community Bank");
            a.setClientIndustry("Banking");
            a.setCaseChallenge("A five-department manual handoff process was creating a 12-day average loan approval time.");
            a.setCaseSolution("Consolidated document intake into one digital workflow with automated compliance checks.");
            a.setCaseResult("65% faster approvals");
            a.setImageUrl("https://images.unsplash.com/photo-1541354329998-f4d9a9f9297f?w=700&auto=format&fit=crop");
            a.setFeatured(false);
            a.setPublished(true);
            articleRepository.save(a);
        }

        if (articleRepository.findBySlug("brightpath-logistics-support-case-study").isEmpty()) {
            Article a = new Article();
            a.setTitle("Scaling Customer Support Without Scaling Headcount");
            a.setSlug("brightpath-logistics-support-case-study");
            a.setSummary("BrightPath Logistics tripled shipment volume while keeping its support team the same size, by routing tickets before they reach a human.");
            a.setContent(
                "<p>As BrightPath Logistics' shipment volume tripled year over year, its support queue grew far faster "
                + "than its team could scale to match.</p>"
                + "<p>A triage layer that automatically resolves routine status-check tickets before they reach a human "
                + "agent freed the team to focus on the exceptions that actually need a person — keeping response times "
                + "flat through the growth.</p>"
            );
            a.setAuthorName("Editorial Team");
            a.setCategory(startups);
            a.setArticleType(ArticleType.CASE_STUDY);
            a.setClientName("BrightPath Logistics");
            a.setClientIndustry("Logistics");
            a.setCaseChallenge("Support ticket volume tripled while the support team stayed the same size.");
            a.setCaseSolution("Added an automated triage layer to resolve routine tickets before human handoff.");
            a.setCaseResult("3x volume, flat headcount");
            a.setImageUrl("https://images.unsplash.com/photo-1556761175-4b46a572b786?w=700&auto=format&fit=crop");
            a.setFeatured(false);
            a.setPublished(true);
            articleRepository.save(a);
        }

        if (articleRepository.findBySlug("nexapay-fraud-detection-launch").isEmpty()) {
            Article a = new Article();
            a.setTitle("NexaPay Launches AI-Powered Fraud Detection for SMBs");
            a.setSlug("nexapay-fraud-detection-launch");
            a.setSummary("The new tool flags suspicious transactions in real time, aimed at small businesses that can't afford a dedicated fraud team.");
            a.setContent(
                "<p>NexaPay today announced the launch of its AI-powered fraud detection engine, built specifically for "
                + "small and mid-sized businesses that process payments without a dedicated fraud team.</p>"
                + "<p>The tool flags suspicious transactions in real time and learns from each merchant's typical "
                + "transaction patterns, reducing false positives compared to rule-based systems.</p>"
            );
            a.setAuthorName("Editorial Team");
            a.setCategory(tech);
            a.setArticleType(ArticleType.PRESS_RELEASE);
            a.setPressCompanyName("NexaPay");
            a.setPressContactName("Media Relations");
            a.setPressContactEmail("press@nexapay.example.com");
            a.setImageUrl("https://images.unsplash.com/photo-1553877522-43269d4ea984?w=700&auto=format&fit=crop");
            a.setFeatured(false);
            a.setPublished(true);
            articleRepository.save(a);
        }

        if (articleRepository.findBySlug("atlas-portlink-strategic-partnership").isEmpty()) {
            Article a = new Article();
            a.setTitle("Atlas Logistics and PortLink Announce Strategic Partnership");
            a.setSlug("atlas-portlink-strategic-partnership");
            a.setSummary("The partnership will integrate PortLink's customs clearance platform directly into Atlas's shipment tracking system.");
            a.setContent(
                "<p>Atlas Logistics and PortLink today announced a strategic partnership that will integrate PortLink's "
                + "customs clearance platform directly into Atlas's shipment tracking system.</p>"
                + "<p>The integration is expected to cut customs-related delays for Atlas customers by giving them "
                + "real-time visibility into clearance status alongside shipment location.</p>"
            );
            a.setAuthorName("Editorial Team");
            a.setCategory(startups);
            a.setArticleType(ArticleType.PRESS_RELEASE);
            a.setPressCompanyName("Atlas Logistics");
            a.setPressContactName("Media Relations");
            a.setPressContactEmail("press@atlaslogistics.example.com");
            a.setImageUrl("https://images.unsplash.com/photo-1521791136064-7986c2920216?w=700&auto=format&fit=crop");
            a.setFeatured(false);
            a.setPublished(true);
            articleRepository.save(a);
        }
    }

    // Tops up startups to 6 (idempotent, per-slug) so the homepage's
    // Startup Spotlight marquee has a full row to scroll through.
    private void seedExtraStartups() {
        addStartupIfMissing(
            "PayNest", "paynest",
            "Meera Iyer", "Founder & CEO",
            "https://images.unsplash.com/photo-1580489944761-15a19d654956?w=400&auto=format&fit=crop",
            "Fintech", "Bengaluru", "India", 2020, FundingStage.SERIES_C, "$60M"
        );
        addStartupIfMissing(
            "CarePulse", "carepulse",
            "Daniel Osei", "Co-Founder & CEO",
            "https://images.unsplash.com/photo-1600180758890-6b94519a8ba6?w=400&auto=format&fit=crop",
            "HealthTech", "Austin", "USA", 2019, FundingStage.SERIES_B, "$32M"
        );
        addStartupIfMissing(
            "AgroSense", "agrosense",
            "Grace Wanjiru", "Founder & CEO",
            "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?w=400&auto=format&fit=crop",
            "AgriTech", "Nairobi", "Kenya", 2022, FundingStage.SERIES_A, "$9M"
        );
        addStartupIfMissing(
            "LearnLoop", "learnloop",
            "Oliver Bennett", "Co-Founder & CEO",
            "https://images.unsplash.com/photo-1531891437562-4301cf35b7e4?w=400&auto=format&fit=crop",
            "EdTech", "London", "UK", 2021, FundingStage.SERIES_A, "$14M"
        );
        addStartupIfMissing(
            "VoltGrid", "voltgrid",
            "Lukas Weber", "Founder & CEO",
            "https://images.unsplash.com/photo-1618077360395-f3068be8e001?w=400&auto=format&fit=crop",
            "CleanTech", "Berlin", "Germany", 2020, FundingStage.SEED, "$5M"
        );
    }

    private void addStartupIfMissing(String name, String slug,
                                      String founderName, String founderTitle, String founderPhoto,
                                      String sector, String city, String country,
                                      int foundingYear, FundingStage stage, String amountRaised) {
        if (startupRepository.findBySlug(slug).isPresent()) return;

        Startup s = new Startup();
        s.setName(name);
        s.setSlug(slug);
        s.setPitch(name + " is a " + sector + " startup based in " + city + ", " + country + ".");
        s.setFounderName(founderName);
        s.setFounderTitle(founderTitle);
        s.setFounderPhoto(founderPhoto);
        s.setSector(sector);
        s.setCity(city);
        s.setCountry(country);
        s.setFoundingYear(foundingYear);
        s.setFundingStage(stage);
        s.setAmountRaised(amountRaised);
        s.setFeatured(false);
        s.setPublished(true);
        startupRepository.save(s);
    }

    // Tops up the Climate category to 3 articles (idempotent, per-slug) so
    // the homepage's 4-category showcase has a full row for it, matching
    // Leadership/Technology/Startups which already have 3 each.
    private void seedExtraClimateArticles() {
        Category climate = categoryRepository.findBySlug("climate").orElse(null);
        if (climate == null) return;

        if (articleRepository.findBySlug("global-emissions-record-high-2026").isEmpty()) {
            Article a = new Article();
            a.setTitle("Global Emissions Hit Record High Despite Renewable Energy Boom");
            a.setSlug("global-emissions-record-high-2026");
            a.setSummary("Even as solar and wind capacity grew faster than ever, global carbon emissions climbed to a new record last year.");
            a.setContent(
                "<p>Global carbon emissions reached a record high last year, according to new data, even as renewable "
                + "energy capacity grew at its fastest pace on record.</p>"
                + "<p>Analysts point to rising energy demand from data centers and continued reliance on coal in parts "
                + "of Asia as the main drivers, offsetting gains made elsewhere.</p>"
            );
            a.setAuthorName("Editorial Team");
            a.setCategory(climate);
            a.setArticleType(ArticleType.NEWS);
            a.setNewsSource("Climate Desk");
            a.setImageUrl("https://images.unsplash.com/photo-1611273426858-450d8e3c9fce?w=700&auto=format&fit=crop");
            a.setFeatured(false);
            a.setPublished(true);
            articleRepository.save(a);
        }

        if (articleRepository.findBySlug("ocean-plastic-cleanup-100000-tons-2026").isEmpty()) {
            Article a = new Article();
            a.setTitle("Ocean Plastic Cleanup Effort Removes 100,000 Tons of Waste in 2026");
            a.setSlug("ocean-plastic-cleanup-100000-tons-2026");
            a.setSummary("A coalition of nonprofits and shipping companies hit a major milestone in a multi-year ocean cleanup initiative.");
            a.setContent(
                "<p>A coalition of environmental nonprofits and commercial shipping partners announced they've removed "
                + "100,000 tons of plastic waste from the world's oceans this year, the largest single-year total since "
                + "the initiative began.</p>"
                + "<p>Organizers credit new collection vessels and expanded partnerships with coastal communities for "
                + "the jump, though they caution the scale of ocean plastic pollution still far outpaces cleanup capacity.</p>"
            );
            a.setAuthorName("Editorial Team");
            a.setCategory(climate);
            a.setArticleType(ArticleType.NEWS);
            a.setNewsSource("Climate Desk");
            a.setImageUrl("https://images.unsplash.com/photo-1621451537084-482c73073a0f?w=700&auto=format&fit=crop");
            a.setFeatured(false);
            a.setPublished(true);
            articleRepository.save(a);
        }
    }

    // Adds 5 more interviews (with cover + interviewee photos) so the homepage
    // slideshow (latest 3) and stacked list (next 3) both have content to show.
    private void seedExtraInterviews() {
        Category leadership = categoryRepository.findBySlug("leadership").orElse(null);
        Category tech       = categoryRepository.findBySlug("technology").orElse(null);
        Category startups   = categoryRepository.findBySlug("startups").orElse(null);
        Category finance    = categoryRepository.findBySlug("finance").orElse(null);

        LocalDateTime now = LocalDateTime.now();

        if (articleRepository.findBySlug("scaling-enterprise-cloud-david-kessler").isEmpty())
        save(interviewWithPhoto(
            "\"The Playbook for Scaling Enterprise Cloud Infrastructure\" — David Kessler, CTO of Meridian Cloud",
            "scaling-enterprise-cloud-david-kessler",
            "Meridian Cloud's CTO on what breaks first when infrastructure scales 100x in a year.",
            "<p><strong>Q: What breaks first when you scale that fast?</strong></p>"
            + "<p>Not the servers — the assumptions. Every architectural shortcut you took at 1x traffic becomes a production incident at 100x. We rebuilt our data layer twice in eighteen months.</p>"
            + "<p><strong>Q: How do you decide what to rebuild versus patch?</strong></p>"
            + "<p>If a patch buys us more than two quarters of runway, we patch. Anything less, we rebuild properly. Technical debt compounds faster than people expect.</p>",
            tech, "David Kessler", "Chief Technology Officer", "Meridian Cloud",
            "https://images.unsplash.com/photo-1560250097-0b93528c311a?w=300&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=900&auto=format&fit=crop",
            true, now.minusHours(1)
        ));

        if (articleRepository.findBySlug("200m-turnaround-marcus-whitfield-ashford").isEmpty())
        save(interviewWithPhoto(
            "\"Inside the $200M Turnaround\" — Marcus Whitfield, CEO of Ashford Capital Group",
            "200m-turnaround-marcus-whitfield-ashford",
            "How Ashford Capital Group's new CEO rebuilt investor trust after two brutal quarters.",
            "<p><strong>Q: Where did you start?</strong></p>"
            + "<p>With the balance sheet, not the press release. Everyone wanted a comeback story on day one. I wanted to know exactly how bad things were before promising anything.</p>"
            + "<p><strong>Q: What convinced investors to stay?</strong></p>"
            + "<p>Transparency, mostly. We over-communicated for two years — monthly letters instead of quarterly ones. People forgive bad numbers faster than they forgive silence.</p>",
            finance, "Marcus Whitfield", "Chief Executive Officer", "Ashford Capital Group",
            "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=300&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=900&auto=format&fit=crop",
            false, now.minusHours(2)
        ));

        if (articleRepository.findBySlug("reimagining-patient-care-elena-torres").isEmpty())
        save(interviewWithPhoto(
            "\"Reimagining Patient Care Through Data\" — Dr. Elena Torres, COO of Meridian Health Network",
            "reimagining-patient-care-elena-torres",
            "Meridian Health Network's COO on the operational changes behind shorter wait times and better outcomes.",
            "<p><strong>Q: What's the biggest operational bottleneck in healthcare?</strong></p>"
            + "<p>Handoffs. Every time a patient moves between departments, information gets lost. We mapped every handoff in our system and it changed how we staff entirely.</p>"
            + "<p><strong>Q: What result are you proudest of?</strong></p>"
            + "<p>Average emergency wait times dropped by 40% in a year, without adding headcount. That came purely from fixing coordination, not spending more.</p>",
            leadership, "Elena Torres", "Chief Operating Officer", "Meridian Health Network",
            "https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?w=300&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1556761175-5973dc0f32e7?w=900&auto=format&fit=crop",
            false, now.minusHours(3)
        ));

        if (articleRepository.findBySlug("building-memorable-brands-nadia-farouk").isEmpty())
        save(interviewWithPhoto(
            "\"Building Brands People Actually Remember\" — Nadia Farouk, CMO of Lumen Creative Co.",
            "building-memorable-brands-nadia-farouk",
            "Lumen Creative's CMO on why most brand campaigns are forgotten within a week — and what she does differently.",
            "<p><strong>Q: Why do most campaigns fail to stick?</strong></p>"
            + "<p>They're built to be liked, not remembered. Those aren't the same goal. We optimise for one specific, ownable idea per campaign instead of trying to say everything at once.</p>"
            + "<p><strong>Q: How do you measure that?</strong></p>"
            + "<p>Unprompted recall surveys, four weeks after a campaign ends. If people can't describe it unprompted, it didn't work — no matter what the engagement metrics said.</p>",
            leadership, "Nadia Farouk", "Chief Marketing Officer", "Lumen Creative Co.",
            "https://images.unsplash.com/photo-1607746882042-944635dfe10e?w=300&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=900&auto=format&fit=crop",
            false, now.minusHours(4)
        ));

        if (articleRepository.findBySlug("garage-to-series-c-jordan-reyes-pathfinder").isEmpty())
        save(interviewWithPhoto(
            "\"From Garage to Series C in 18 Months\" — Jordan Reyes, Founder & CEO of Pathfinder Robotics",
            "garage-to-series-c-jordan-reyes-pathfinder",
            "Pathfinder Robotics' founder on the fundraising sprint that took the company from three people to three hundred.",
            "<p><strong>Q: What changed once you raised the Series A?</strong></p>"
            + "<p>The problems got more expensive to get wrong. At the garage stage, a bad decision costs you weeks. At scale, it costs you quarters and half your engineering team's trust.</p>"
            + "<p><strong>Q: Any advice for founders in that jump?</strong></p>"
            + "<p>Hire slower than you think you need to. We over-hired once trying to match investor expectations, and undoing that was harder than the original hiring push.</p>",
            startups, "Jordan Reyes", "Founder & CEO", "Pathfinder Robotics",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=900&auto=format&fit=crop",
            true, now.minusHours(5)
        ));
    }

    private Article interviewWithPhoto(String title, String slug, String summary,
                                        String content, Category cat,
                                        String name, String jobTitle, String company,
                                        String intervieweePhoto, String coverImageUrl,
                                        boolean featured, LocalDateTime createdAt) {
        Article a = new Article();
        a.setTitle(title);           a.setSlug(slug);
        a.setSummary(summary);       a.setContent(content);
        a.setAuthorName("Editorial Team");
        a.setCategory(cat);          a.setArticleType(ArticleType.INTERVIEW);
        a.setIntervieweeName(name);  a.setIntervieweeTitle(jobTitle);
        a.setIntervieweeCompany(company);
        a.setIntervieweePhoto(intervieweePhoto);
        a.setImageUrl(coverImageUrl);
        a.setFeatured(featured);     a.setPublished(true);
        a.setCreatedAt(createdAt);   a.setUpdatedAt(createdAt);
        return a;
    }

    private void save(Article a) {
        articleRepository.save(a);
    }
}
