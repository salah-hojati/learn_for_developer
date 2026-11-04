package com.example.blog.service;

import com.example.blog.dto.*;
import com.example.blog.entity.*;
import com.example.blog.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final EntityManager em;

    // ---------------- CRUD ----------------

    @Transactional
    public PostDetail createPost(String username, String title, String content, Set<String> tagNames) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Post post = new Post();
        post.setTitle(title);
        post.setSlug(slugify(title));
        post.setContent(content);
        post.setStatus(Post.PostStatus.DRAFT);

        author.addPost(post);

        for (String name : tagNames) {
            Tag tag = tagRepository.findByName(name)
                    .orElseGet(() -> {
                        Tag t = new Tag();
                        t.setName(name);
                        return tagRepository.save(t);
                    });
            post.addTag(tag);
        }

        Post saved = postRepository.save(post);
        return toDetail(saved);
    }

    // ---------------- Read با JOIN FETCH ----------------

    @Transactional(readOnly = true)
    public PostDetail getPostBySlug(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        // هنوز lazy هستن؛ به on-demand بارگذاری می‌شن
        return toDetail(post);
    }

    @Transactional(readOnly = true)
    public List<PostDetail> getPublishedPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // JOIN FETCH → یک query، بدون N+1
        return postRepository.findAllPublishedWithDetails(pageable).stream()
                .map(this::toDetail)
                .toList();
    }

    // ---------------- Criteria Search ----------------

    @Transactional(readOnly = true)
    public Page<PostSummary> search(PostSearchCriteria criteria) {
        return postRepository.searchDynamic(criteria);
    }

    // ---------------- آپدیت با Optimistic Locking ----------------

    @Transactional
    public PostDetail updatePost(Long id, String newTitle, String newContent, Long expectedVersion) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());

        // چک دستی نسخه (یا بذار خود JPA در commit چک کنه)
        if (!Objects.equals(post.getVersion(), expectedVersion)) {
            throw new ObjectOptimisticLockingFailureException(Post.class, id);
        }

        post.setTitle(newTitle);
        post.setContent(newContent);
        // flush می‌کنه و @Version رو چک می‌کنه
        try {
            em.flush();
        } catch (OptimisticLockException ex) {
            throw new ObjectOptimisticLockingFailureException(Post.class, id);
        }

        return toDetail(post);
    }

    // ---------------- Bulk update ----------------

    @Transactional
    public int incrementView(Long postId) {
        return postRepository.incrementViewCount(postId);
    }

    // ---------------- Mapping ----------------

    private PostDetail toDetail(Post post) {
        return new PostDetail(
            post.getId(),
            post.getTitle(),
            post.getSlug(),
            post.getContent(),
            post.getAuthor().getUsername(),    // lazy → query اضافه (مراقب باش)
            post.getAuthor().getEmail(),
            post.getTags().stream().map(Tag::getName).toList(),
            post.getComments().stream()
                .filter(Comment::isApproved)
                .map(c -> new PostDetail.CommentView(
                        c.getId(), c.getText(), c.getUser().getUsername()))
                .toList(),
            post.getCreatedAt()
        );
    }

    private String slugify(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                + "-" + System.currentTimeMillis();
    }
}