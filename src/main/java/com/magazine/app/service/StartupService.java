package com.magazine.app.service;

import com.magazine.app.model.FundingStage;
import com.magazine.app.model.Startup;
import com.magazine.app.repository.StartupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StartupService {

    @Autowired
    private StartupRepository startupRepository;

    // ── Public ───────────────────────────────────────
    public Page<Startup> getPublished(int page, int size) {
        return startupRepository
            .findByPublishedTrueOrderByCreatedAtDesc(
                PageRequest.of(page, size)
            );
    }

    // Combinable sector / stage / city filters — pass null for "any"
    public Page<Startup> getFiltered(String sector, FundingStage stage, String city, int page, int size) {
        String sectorParam = (sector != null && !sector.isBlank()) ? sector : null;
        String cityParam   = (city != null && !city.isBlank()) ? city : null;
        return startupRepository.findFiltered(
            sectorParam, stage, cityParam, PageRequest.of(page, size)
        );
    }

    public List<String> getAllCities() {
        return startupRepository.findDistinctCityByPublishedTrue();
    }

    public List<Startup> getFeatured() {
        return startupRepository
            .findByPublishedTrueAndFeaturedTrueOrderByCreatedAtDesc();
    }

    public List<Startup> getLatest(int limit) {
        return startupRepository
            .findTop6ByPublishedTrueOrderByCreatedAtDesc();
    }

    public Optional<Startup> getBySlug(String slug) {
        return startupRepository.findBySlug(slug);
    }

    public List<String> getAllSectors() {
        return startupRepository
            .findDistinctSectorByPublishedTrue()
            .stream()
            .filter(s -> s != null && !s.isBlank())
            .sorted()
            .toList();
    }

    // ── Admin ────────────────────────────────────────
    public List<Startup> getAll() {
        return startupRepository.findAll(
            Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }

    public Optional<Startup> getById(Long id) {
        return startupRepository.findById(id);
    }

    public Startup save(Startup startup) {
        if (startup.getId() == null) {
            startup.setCreatedAt(LocalDateTime.now());
        }
        startup.setUpdatedAt(LocalDateTime.now());
        return startupRepository.save(startup);
    }

    public void delete(Long id) {
        startupRepository.deleteById(id);
    }

    public long countAll() {
        return startupRepository.count();
    }

    public String generateSlug(String name) {
        String base = name.toLowerCase().trim()
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-");
        String slug = base;
        int counter = 1;
        while (startupRepository.findBySlug(slug).isPresent()) {
            slug = base + "-" + counter;
            counter++;
        }
        return slug;
    }
}