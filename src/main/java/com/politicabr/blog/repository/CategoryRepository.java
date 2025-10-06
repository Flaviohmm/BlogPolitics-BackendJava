package com.politicabr.blog.repository;

import com.politicabr.blog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    List<Category> findByActiveTrue();

    List<Category> findByActiveTrueOrderByDisplayOrderAsc();

    boolean existsBySlug(String slug);

    @Query("SELECT c FROM Category c WHERE c.active = true ORDER BY c.name")
    List<Category> findAllActive();

    // Categorias com contagem de posts
    @Query("""
            SELECT c, COUNT(p) as postCount 
            FROM Category c 
            LEFT JOIN c.posts p 
            WHERE c.active = true AND (p.published = true OR p IS NULL)
            GROUP BY c 
            ORDER BY c.name
            """)
    List<Object[]> findActiveCategoriesWithPostCount();

    // Categorias mais populares
    @Query("""
            SELECT c 
            FROM Category c 
            JOIN c.posts p 
            WHERE c.active = true AND p.published = true 
            GROUP BY c 
            ORDER BY COUNT(p) DESC
            """)
    List<Category> findMostPopularCategories();

}
