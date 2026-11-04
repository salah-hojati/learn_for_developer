package com.example.blog.dto;

public record PostSearchCriteria(
    String titleLike,
    String authorUsername,
    PostStatusView status,
    String tagName,
    Long minViews,
    int page,
    int size,
    String sortBy,     // "createdAt", "viewCount"
    boolean desc
) {
    public enum PostStatusView { DRAFT, PUBLISHED, ARCHIVED }
}