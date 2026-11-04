package com.example.blog.dto;

public record PostSummary(
    Long id,
    String title,
    String slug,
    String authorName,
    long commentCount,
    PostStatusView status
) {
    public enum PostStatusView { DRAFT, PUBLISHED, ARCHIVED }
}