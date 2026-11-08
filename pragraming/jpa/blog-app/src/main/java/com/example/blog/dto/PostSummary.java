package com.example.blog.dto;

public record PostSummary(
        Long id,
        String title,
        String slug,
        String authorName,
        Long commentCount
) {}