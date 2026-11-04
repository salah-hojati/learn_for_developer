package com.example.blog.repository;

import com.example.blog.entity.*;
import com.example.blog.dto.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    // با EntityGraph — profile رو fetch کن بدون N+1
    @EntityGraph(attributePaths = {"profile"})
    @Query("SELECT u FROM User u WHERE u.status = :status")
    List<User> findActiveWithProfile(@Param("status") User.UserStatus status);

    // پروجکشن مستقیم به DTO
    @Query("""
        SELECT new com.example.blog.dto.UserSummary(u.id, u.username, u.email)
        FROM User u WHERE u.status = 'ACTIVE'
    """)
    List<UserSummary> findAllActiveSummaries();

    // JPQL با join
    @Query("""
        SELECT u FROM User u
        JOIN u.posts p
        WHERE p.status = 'PUBLISHED'
        GROUP BY u
        HAVING COUNT(p) >= :minPosts
    """)
    List<User> findProlificAuthors(@Param("minPosts") long minPosts);

    boolean existsByEmail(String email);
}