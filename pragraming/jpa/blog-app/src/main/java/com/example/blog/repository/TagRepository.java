package com.example.blog.repository;

import com.example.blog.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    // متد مشتق‌شده — Spring Data خودش query می‌سازه
    Optional<Tag> findByName(String name);

    // پیدا کردن چند tag با هم (برای fetch گروهی)
    List<Tag> findByNameIn(Set<String> names);

    // چک وجود
    boolean existsByName(String name);

    // Tagهای پرکاربرد — با JPQL aggregate
    @Query("""
        SELECT t FROM Tag t
        JOIN t.posts p
        WHERE p.status = 'PUBLISHED'
        GROUP BY t
        ORDER BY COUNT(p) DESC
    """)
    List<Tag> findMostUsedTags();   // می‌تونی Pageable هم اضافه کنی

    // Tagهای بدون پست (برای cleanup)
    @Query("SELECT t FROM Tag t WHERE t.posts IS EMPTY")
    List<Tag> findOrphanTags();

    // شمارش پست‌های هر tag در یک پروجکشن
    @Query("""
        SELECT t.name AS name, COUNT(p) AS postCount
        FROM Tag t LEFT JOIN t.posts p
        WHERE p.status = 'PUBLISHED' OR p IS NULL
        GROUP BY t.name
        ORDER BY COUNT(p) DESC
    """)
    List<TagCountView> findTagCounts();

    interface TagCountView {
        String getName();
        Long getPostCount();
    }
}