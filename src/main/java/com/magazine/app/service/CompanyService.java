package com.magazine.app.service;

import com.magazine.app.model.Company;
import com.magazine.app.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    // ── Public ───────────────────────────────────────
    public Page<Company> getPublished(int page, int size) {
        return companyRepository.findByPublishedTrueOrderByNameAsc(
            PageRequest.of(page, size)
        );
    }

    public Page<Company> getBySector(String sector, int page, int size) {
        return companyRepository.findByPublishedTrueAndSectorOrderByNameAsc(
            sector, PageRequest.of(page, size)
        );
    }

    public List<Company> getFeatured() {
        return companyRepository.findByPublishedTrueAndFeaturedTrueOrderByNameAsc();
    }

    public List<Company> getLatest(int limit) {
        return companyRepository.findTop6ByPublishedTrueOrderByCreatedAtDesc();
    }

    public Optional<Company> getBySlug(String slug) {
        return companyRepository.findBySlug(slug);
    }

    public List<String> getAllSectors() {
        return companyRepository.findDistinctSectorByPublishedTrue()
            .stream()
            .filter(s -> s != null && !s.isBlank())
            .sorted()
            .toList();
    }

    // ── Admin ────────────────────────────────────────
    public List<Company> getAll() {
        return companyRepository.findAll(
            Sort.by(Sort.Direction.ASC, "name")
        );
    }

    public Optional<Company> getById(Long id) {
        return companyRepository.findById(id);
    }

    public Company save(Company company) {
        if (company.getId() == null) {
            company.setCreatedAt(LocalDateTime.now());
        }
        company.setUpdatedAt(LocalDateTime.now());
        return companyRepository.save(company);
    }

    public void delete(Long id) {
        companyRepository.deleteById(id);
    }

    public long countAll() {
        return companyRepository.count();
    }

    public String generateSlug(String name) {
        String base = name.toLowerCase().trim()
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-");
        String slug = base;
        int counter = 1;
        while (companyRepository.findBySlug(slug).isPresent()) {
            slug = base + "-" + counter;
            counter++;
        }
        return slug;
    }
}