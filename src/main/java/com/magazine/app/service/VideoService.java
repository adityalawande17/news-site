package com.magazine.app.service;

import com.magazine.app.model.Video;
import com.magazine.app.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    // ── Public ───────────────────────────────────────
    public Page<Video> getPublished(int page, int size) {
        return videoRepository.findByPublishedTrueOrderByCreatedAtDesc(
            PageRequest.of(page, size)
        );
    }

    public List<Video> getFeatured() {
        return videoRepository.findByPublishedTrueAndFeaturedTrueOrderByCreatedAtDesc();
    }

    public List<Video> getLatest(int limit) {
        return videoRepository
            .findByPublishedTrueOrderByCreatedAtDesc(PageRequest.of(0, limit))
            .getContent();
    }

    public Optional<Video> getBySlug(String slug) {
        return videoRepository.findBySlug(slug);
    }

    public void incrementViews(Video video) {
        video.setViews(video.getViews() + 1);
        videoRepository.save(video);
    }

    // ── Admin ────────────────────────────────────────
    public List<Video> getAll() {
        return videoRepository.findAll(
            Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }

    public Optional<Video> getById(Long id) {
        return videoRepository.findById(id);
    }

    public Video save(Video video) {
        if (video.getId() == null) {
            video.setCreatedAt(LocalDateTime.now());
        }
        video.setUpdatedAt(LocalDateTime.now());
        return videoRepository.save(video);
    }

    public void delete(Long id) {
        videoRepository.deleteById(id);
    }

    public long countAll() {
        return videoRepository.count();
    }

    // ── Convert YouTube watch URL to embed URL ───────
    // Input:  https://www.youtube.com/watch?v=abc123
    // Output: https://www.youtube.com/embed/abc123
    public String toEmbedUrl(String url) {
        if (url == null || url.isBlank()) return url;

        // Already an embed URL
        if (url.contains("/embed/")) return url;

        // YouTube watch URL
        if (url.contains("youtube.com/watch?v=")) {
            String videoId = url.split("v=")[1];
            // Strip any extra params like &t=30
            if (videoId.contains("&")) {
                videoId = videoId.split("&")[0];
            }
            return "https://www.youtube.com/embed/" + videoId;
        }

        // YouTube short URL: https://youtu.be/abc123
        if (url.contains("youtu.be/")) {
            String videoId = url.split("youtu.be/")[1];
            if (videoId.contains("?")) {
                videoId = videoId.split("\\?")[0];
            }
            return "https://www.youtube.com/embed/" + videoId;
        }

        // Vimeo URL: https://vimeo.com/123456789
        if (url.contains("vimeo.com/")) {
            String videoId = url.split("vimeo.com/")[1];
            if (videoId.contains("?")) {
                videoId = videoId.split("\\?")[0];
            }
            return "https://player.vimeo.com/video/" + videoId;
        }

        return url;
    }

    // ── Auto-extract YouTube thumbnail ───────────────
    // Input:  https://www.youtube.com/watch?v=abc123
    // Output: https://img.youtube.com/vi/abc123/hqdefault.jpg
    public String extractYoutubeThumbnail(String url) {
        if (url == null || url.isBlank()) return null;

        String videoId = null;

        if (url.contains("youtube.com/watch?v=")) {
            videoId = url.split("v=")[1];
            if (videoId.contains("&")) videoId = videoId.split("&")[0];
        } else if (url.contains("youtu.be/")) {
            videoId = url.split("youtu.be/")[1];
            if (videoId.contains("?")) videoId = videoId.split("\\?")[0];
        } else if (url.contains("/embed/")) {
            videoId = url.split("/embed/")[1];
            if (videoId.contains("?")) videoId = videoId.split("\\?")[0];
        }

        if (videoId != null) {
            return "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
        }

        return null;
    }

    public String generateSlug(String title) {
        String base = title.toLowerCase().trim()
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-");
        String slug = base;
        int counter = 1;
        while (videoRepository.findBySlug(slug).isPresent()) {
            slug = base + "-" + counter;
            counter++;
        }
        return slug;
    }
}