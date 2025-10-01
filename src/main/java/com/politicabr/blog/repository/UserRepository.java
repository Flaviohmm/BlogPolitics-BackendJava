package com.politicabr.blog.repository;

import com.politicabr.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    @Query("SELECT u FROM User u WHERE u.active = true AND u.role = :role")
    List<User> findActiveUsersByRole();

    // Autores mais ativos
    @Query("""
        SELECT u, COUNT(p) as postCount 
        FROM User u 
        LEFT JOIN u.posts p 
        WHERE u.active = true AND u.role IN ('AUTHOR', 'ADMIN') 
        AND (p.published = true OR p IS NULL)
        GROUP BY u 
        ORDER BY COUNT(p) DESC
        """)
    List<Object[]> findMostActiveAuthors();

    // Usuário registrados recentemente
    @Query("SELECT u FROM User u WHERE u.active = true AND u.createdAt >= :since ORDER BY u.createdAt DESC")
    Page<User> findRecentUsers(@Param("since") LocalDateTime since, Pageable pageable);
}
