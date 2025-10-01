package com.politicabr.blog.repository;

import com.politicabr.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Busca por slug
    Optional<Post> findBySlugAndPublishedTrue(String slug);

    // Posts publicados ordenados por data
    @Query("SELECT p FROM Post p WHERE p.published = true ORDER BY p.createdAt DESC")
    Page<Post> findPublishedPosts(Pageable pageable);

    // Posts em destaque
    @Query("SELECT p FROM Post p WHERE p.published = true AND p.featured = true ORDER BY p.createdAt DESC")
    Page<Post> findFeaturedPosts(Pageable pageable);

    // Posts por categoria
    @Query("SELECT p FROM Post p WHERE p.published = true AND p.category.slug = :categorySlug ORDER BY p.createdAt DESC")
    Page<Post> findByCategory(@Param("categorySlug") String categorySlug, Pageable pageable);

    // Posts por autor
    @Query("SELECT p FROM Post p WHERE p.published = true AND p.author.id = :authorId ORDER BY p.createdAt DESC")
    Page<Post> findByAuthor(@Param("authorId") Long authorId, Pageable pageable);

    // Busca full-text em PostgreSQL
    @Query(value = """
             SELECT p.* FROM posts p\s
                    WHERE p.published = true\s
                    AND (to_tsvector('portuguese', p.title || ' ' || p.excerpt || ' ' || p.content) @@ plainto_tsquery('portuguese', :searchTerm))
                    ORDER BY ts_rank(to_tsvector('portuguese', p.title || ' ' || p.excerpt || ' ' || p.content), plainto_tsquery('portuguese', :searchTerm)) DESC
            """, nativeQuery = true)
    Page<Post> searchPosts(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Posts relacionados (mesma categoria, excluindo o atual)
    @Query("SELECT p FROM Post p WHERE p.published = true AND p.category.id = :categoryId AND p.id != :currentPostId ORDER BY p.createdAt DESC")
    List<Post> findRelatedPosts(@Param("categoryId") Long categoryId, @Param("currentPostId") Long currentPostId, Pageable pageable);

    // Post mais visualizados
    @Query("SELECT p FROM Post p WHERE p.published = true ORDER BY p.viewCount DESC")
    Page<Post> findMostViewed(Pageable pageable);

    // Posts recentes (últimos N dias)
    @Query("SELECT p FROM Post p WHERE p.published = true AND p.publishedAt >= :since ORDER BY p.publishedAt DESC")
    Page<Post> findRecentPosts(@Param("since") LocalDateTime since, Pageable pageable);

    // Incrementar contador de visualizações
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);

    // Contar posts por categoria
    @Query("SELECT COUNT(p) FROM Post p WHERE p.published = true AND p.category.id = :categoryId")
    Long countByCategory(@Param("categoryId") Long categoryId);

    // Contar posts por autor
    @Query("SELECT COUNT(p) FROM Post p WHERE p.published = true AND p.author.id = :authorId")
    Long countByAuthor(@Param("authorId") Long authorId);

    // Posts por tag
    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t WHERE p.published = true AND t.slug = :tagSlug ORDER BY p.createdAt DESC")
    Page<Post> findByTag(@Param("tagSlug") String tagSlug, Pageable pageable);

    // Verificar se slug já existe
    boolean existsBySlug(String slug);

    // Posts do dashboard admin
    @Query("SELECT p FROM Post p ORDER BY p.updatedAt DESC")
    Page<Post> findAllForAdmin(Pageable pageable);

    // Estatísticas mensais
    @Query(value = """
            SELECT EXTRACT(YEAR FROM created_at) as year, 
                   EXTRACT(MONTH FROM created_at) as month, 
                   COUNT(*) as total
            FROM posts 
            WHERE published = true 
            AND created_at >= :startDate
            GROUP BY EXTRACT(YEAR FROM created_at), EXTRACT(MONTH FROM created_at)
            ORDER BY year DESC, month DESC
            """, nativeQuery = true)
    List<Object[]> getMonthlyStats(@Param("startDate") LocalDateTime startDate);
}
