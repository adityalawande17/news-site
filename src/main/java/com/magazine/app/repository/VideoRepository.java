package com.magazine.app.repository;

import com.magazine.app.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {

    Optional<Video> findBySlug(String slug);

    Page<Video> findByPublishedTrueOrderByCreatedAtDesc(Pageable pageable);

    List<Video> findByPublishedTrueAndFeaturedTrueOrderByCreatedAtDesc();

    List<Video> findTop4ByPublishedTrueOrderByCreatedAtDesc();

    long countByPublishedTrue();
}