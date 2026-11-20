package com.example.blog.service;

import com.example.blog.entity.Profile;
import com.example.blog.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @PersistenceContext
    private EntityManager em;

    // مثال JPQL خام
    @Transactional(readOnly = true)
    public List<User> findUsersWithMinPosts(int minPosts) {
        String jpql = """
            SELECT u FROM User u
            WHERE (SELECT COUNT(p) FROM Post p WHERE p.author = u AND p.status = 'PUBLISHED') >= :min
            ORDER BY u.createdAt DESC
        """;
        TypedQuery<User> q = em.createQuery(jpql, User.class);
        q.setParameter("min", (long) minPosts);
        return q.getResultList();
    }

    // مثال find vs getReference
    @Transactional
    public void assignProfile(Long userId, String bio, String website) {
        // getReference → بدون SELECT، فقط proxy می‌سازه (مفید برای FK)
        User userRef = em.getReference(User.class, userId);

        Profile profile = new Profile();
        profile.setBio(bio);
        profile.setWebsite(website);
        profile.setUser(userRef);

        em.persist(profile);
    }
}