package com.example.blog.repository;

import com.example.blog.dto.PostSummary;
import com.example.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    Optional<Post> findBySlug(String slug);

    // JOIN FETCH برای جلوگیری از N+1
    @Query("""
        SELECT DISTINCT p FROM Post p
        JOIN FETCH p.author
        LEFT JOIN FETCH p.tags
        WHERE p.status = 'PUBLISHED'
    """)
    List<Post> findAllPublishedWithDetails(Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.id = :id")
    Optional<Post> findByIdWithAuthor(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    int incrementViewCount(@Param("id") Long id);

    // Projection با interface
    @Query("SELECT p.id AS id, p.title AS title, p.slug AS slug, u.username AS authorName " +
           "FROM Post p JOIN p.author u WHERE p.status = 'PUBLISHED'")
    List<PostListView> findAllAsView();

    interface PostListView {
        Long getId();
        String getTitle();
        String getSlug();
        String getAuthorName();
    }

    // Projection با constructor expression
    @Query("""
        SELECT new com.example.blog.dto.PostSummary(
            p.id, p.title, p.slug, u.username,
            (SELECT COUNT(c) FROM Comment c WHERE c.post = p AND c.approved = true),
            com.example.blog.dto.PostSummary.PostStatusView.PUBLISHED
        )
        FROM Post p JOIN p.author u
        WHERE p.status = 'PUBLISHED'
    """)
    Page<PostSummary> findSummaries(Pageable pageable);
}