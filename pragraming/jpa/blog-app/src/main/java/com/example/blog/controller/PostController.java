package com.example.blog.controller;

import com.example.blog.dto.*;
import com.example.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDetail> create(
            @RequestParam String username,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Set<String> tags) {
        return ResponseEntity.ok(postService.createPost(username, title, content, tags));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<PostDetail> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(postService.getPostBySlug(slug));
    }

    @GetMapping("/published")
    public ResponseEntity<List<PostDetail>> getPublished(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(postService.getPublishedPosts(page, size));
    }

    // Criteria search
    @PostMapping("/search")
    public ResponseEntity<Page<PostSummary>> search(@RequestBody PostSearchCriteria criteria) {
        return ResponseEntity.ok(postService.search(criteria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDetail> update(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Long version) {
        return ResponseEntity.ok(postService.updatePost(id, title, content, version));
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Integer> incrementView(@PathVariable Long id) {
        return ResponseEntity.ok(postService.incrementView(id));
    }
}