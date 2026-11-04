package com.example.blog.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetail(
    Long id,
    String title,
    String slug,
    String content,
    String authorName,
    String authorEmail,
    List<String> tags,
    List<CommentView> comments,
    LocalDateTime createdAt
) {
    public record CommentView(Long id, String text, String userName) {}
}