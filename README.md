# BizPulse — Business Magazine CMS
A full-stack business publication platform built with **Java Spring Boot**, **Thymeleaf**, **Spring Security**, **Spring Data JPA**, and **H2** (file-based, swap to MySQL in one step).

---

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+

### Run the application
```bash
cd magazine
mvn spring-boot:run
```

The app starts at **http://localhost:8080**

### Default Admin Login
| URL | Username | Password |
|-----|----------|----------|
| http://localhost:8080/admin/login | `admin` | `admin123` |

> Change the password in production via the DB or by updating `DataInitializer.java`.

---

## Project Structure

```
magazine/
├── pom.xml
└── src/main/
    ├── java/com/magazine/app/
    │   ├── MagazineApplication.java       ← Spring Boot entry point
    │   ├── config/
    │   │   ├── SecurityConfig.java        ← Spring Security (login, routes, CSRF)
    │   │   └── DataInitializer.java       ← Seeds admin user + sample data on first run
    │   ├── model/
    │   │   ├── Article.java               ← Article entity (title, slug, content, featured…)
    │   │   ├── Category.java              ← Category entity
    │   │   └── AdminUser.java             ← Admin user entity
    │   ├── repository/
    │   │   ├── ArticleRepository.java
    │   │   ├── CategoryRepository.java
    │   │   └── AdminUserRepository.java
    │   ├── service/
    │   │   ├── ArticleService.java        ← Business logic, slug generation, pagination
    │   │   ├── CategoryService.java
    │   │   ├── FileStorageService.java    ← Image upload handling
    │   │   └── AdminUserDetailsService.java
    │   └── controller/
    │       ├── PublicController.java      ← Public routes: home, blog, article, search
    │       └── AdminController.java       ← Admin routes: dashboard, CRUD for articles & categories
    └── resources/
        ├── application.properties         ← DB config, upload path, server port
        ├── templates/
        │   ├── fragments.html             ← Shared header/footer for public site
        │   ├── index.html                 ← Homepage (hero + latest)
        │   ├── blog-list.html             ← Paginated article listing
        │   ├── category.html              ← Category-filtered listing
        │   ├── article-detail.html        ← Full article with sidebar
        │   ├── search.html                ← Search results
        │   ├── about.html
        │   ├── contact.html
        │   └── admin/
        │       ├── login.html
        │       ├── dashboard.html         ← Stats + recent articles
        │       ├── article-list.html      ← All articles table with edit/delete
        │       ├── article-form.html      ← Create / edit article
        │       ├── category-list.html
        │       └── category-form.html
        └── static/
            ├── css/
            │   ├── style.css              ← Public site styles
            │   └── admin.css              ← Admin dashboard styles
            └── uploads/                   ← Uploaded cover images land here
```

---

## Public Site Pages
| Route | Description |
|-------|-------------|
| `/` | Homepage — featured hero + latest articles |
| `/blog` | Paginated article listing |
| `/category/{slug}` | Articles filtered by category |
| `/article/{slug}` | Full article detail with sidebar |
| `/search?q=keyword` | Search by title |
| `/about` | About page |
| `/contact` | Contact form |

## Admin Dashboard Pages
| Route | Description |
|-------|-------------|
| `/admin/login` | Login |
| `/admin/dashboard` | Stats + recent articles |
| `/admin/articles` | All articles (view, edit, delete) |
| `/admin/articles/new` | Create new article |
| `/admin/articles/edit/{id}` | Edit article |
| `/admin/categories` | All categories |
| `/admin/categories/new` | Create category |
| `/admin/categories/edit/{id}` | Edit category |

---

## Switching to MySQL

1. Make sure MySQL is running and create a database:
   ```sql
   CREATE DATABASE magazinedb;
   ```
2. In `application.properties`, comment out the H2 block and uncomment the MySQL block:
   ```properties
   # Comment out:
   # spring.datasource.url=jdbc:h2:file:./data/magazinedb...
   
   # Uncomment:
   spring.datasource.url=jdbc:mysql://localhost:3306/magazinedb?createDatabaseIfNotExist=true
   spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   ```
3. Restart. JPA will auto-create the tables (`ddl-auto=update`).

---

## Key Features
- **Public Site**: Magazine-style homepage, paginated blog, category filtering, article detail with view counter, search, about, contact.
- **Admin Dashboard**: Secure login (BCrypt password hashing), article CRUD with image upload, category management, real-time stats.
- **Responsive**: Mobile-friendly CSS with grid layouts.
- **Seeded data**: 4 categories and 4 sample articles created on first run automatically.
- **Security**: Spring Security protects all `/admin/**` routes; CSRF protection on all forms.

---

## Next Steps (to extend)
- Wire JavaMailSender for contact form email delivery
- Add a rich text editor (e.g. Quill or TinyMCE) to the article body field
- Add author/profile management
- Add newsletter subscription model
- Deploy to a VPS (e.g. DigitalOcean, Railway) with MySQL
