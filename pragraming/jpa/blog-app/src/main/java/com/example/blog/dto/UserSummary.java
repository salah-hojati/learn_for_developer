package com.example.blog.dto;

public record UserSummary(
    Long id,
    String username,
    String email
) {}