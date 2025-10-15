package com.politicabr.blog.repository;

import com.politicabr.blog.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Tag> findByNameContainingIgnoreCase(String name);

    // Tags mais usadas
    @Query("""
            SELECT t, COUNT(p) as postCount
            FROM Tag t
            LEFT JOIN t.posts p
            WHERE p.published = true OR p IS NULL
            GROUP BY t
            ORDER BY COUNT(p) DESC
            """)
    List<Object[]> findPopularTags();

    // Buscar todas as tags com contagem
    @Query("""
            SELECT t, COUNT(p) as postCount
            FROM Tag t
            LEFT JOIN t.posts p
            WHERE p.published = true OR p IS NULL
            GROUP BY t
            ORDER BY t.name ASC
            """)
    List<Object[]> findAllWithPostCount();
}
