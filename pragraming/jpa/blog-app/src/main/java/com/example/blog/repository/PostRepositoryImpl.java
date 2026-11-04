package com.example.blog.repository;

import com.example.blog.dto.PostSearchCriteria;
import com.example.blog.dto.PostSummary;
import com.example.blog.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PostRepositoryImpl implements PostRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<PostSummary> searchDynamic(PostSearchCriteria c) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // ----- Query اصلی -----
        CriteriaQuery<PostSummary> cq = cb.createQuery(PostSummary.class);
        Root<Post> post = cq.from(Post.class);
        Join<Post, User> author = post.join("author", JoinType.INNER);

        // ----- Subquery برای commentCount -----
        Subquery<Long> commentCount = cq.subquery(Long.class);
        Root<Comment> comment = commentCount.from(Comment.class);
        commentCount.select(cb.count(comment))
                    .where(
                        cb.equal(comment.get("post"), post),
                        cb.isTrue(comment.get("approved"))
                    );

        // ----- Projection -----
        cq.select(cb.construct(
            PostSummary.class,
            post.get("id"),
            post.get("title"),
            post.get("slug"),
            author.get("username"),
            commentCount.getSelection(),
            cb.literal(PostSummary.PostStatusView.PUBLISHED)
        ));

        // ----- Predicates داینامیک -----
        List<Predicate> predicates = new ArrayList<>();

        if (c.titleLike() != null && !c.titleLike().isBlank()) {
            predicates.add(cb.like(cb.lower(post.get("title")),
                    "%" + c.titleLike().toLowerCase() + "%"));
        }
        if (c.authorUsername() != null && !c.authorUsername().isBlank()) {
            predicates.add(cb.equal(author.get("username"), c.authorUsername()));
        }
        if (c.status() != null) {
            predicates.add(cb.equal(post.get("status"),
                    Post.PostStatus.valueOf(c.status().name())));
        }
        if (c.minViews() != null) {
            predicates.add(cb.greaterThanOrEqualTo(post.get("viewCount"), c.minViews()));
        }
        if (c.tagName() != null && !c.tagName().isBlank()) {
            // با subquery برای tag چون ManyToMany
            Subquery<Long> tagSub = cq.subquery(Long.class);
            Root<Post> postSub = tagSub.from(Post.class);
            Join<Post, Tag> tagJoin = postSub.join("tags", JoinType.INNER);
            tagSub.select(postSub.get("id"))
                  .where(cb.equal(tagJoin.get("name"), c.tagName()));
            predicates.add(post.get("id").in(tagSub));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        // ----- Sort -----
        Path<?> sortPath = "viewCount".equals(c.sortBy())
                ? post.get("viewCount")
                : post.get("createdAt");
        cq.orderBy(c.desc() ? cb.desc(sortPath) : cb.asc(sortPath));

        TypedQuery<PostSummary> query = em.createQuery(cq);
        query.setFirstResult(c.page() * c.size());
        query.setMaxResults(c.size());

        List<PostSummary> content = query.getResultList();

        // ----- Count Query جدا -----
        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<Post> countRoot = countCq.from(Post.class);
        Join<Post, User> countAuthor = countRoot.join("author", JoinType.INNER);
        countCq.select(cb.count(countRoot));

        List<Predicate> countPredicates = new ArrayList<>();
        if (c.titleLike() != null && !c.titleLike().isBlank()) {
            countPredicates.add(cb.like(cb.lower(countRoot.get("title")),
                    "%" + c.titleLike().toLowerCase() + "%"));
        }
        if (c.authorUsername() != null && !c.authorUsername().isBlank()) {
            countPredicates.add(cb.equal(countAuthor.get("username"), c.authorUsername()));
        }
        if (c.status() != null) {
            countPredicates.add(cb.equal(countRoot.get("status"),
                    Post.PostStatus.valueOf(c.status().name())));
        }
        if (c.minViews() != null) {
            countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("viewCount"), c.minViews()));
        }
        if (c.tagName() != null && !c.tagName().isBlank()) {
            Subquery<Long> tagSub = countCq.subquery(Long.class);
            Root<Post> postSub = tagSub.from(Post.class);
            Join<Post, Tag> tagJoin = postSub.join("tags", JoinType.INNER);
            tagSub.select(postSub.get("id"))
                  .where(cb.equal(tagJoin.get("name"), c.tagName()));
            countPredicates.add(countRoot.get("id").in(tagSub));
        }
        countCq.where(cb.and(countPredicates.toArray(new Predicate[0])));

        Long total = em.createQuery(countCq).getSingleResult();

        return new PageImpl<>(content, PageRequest.of(c.page(), c.size()), total);
    }
}