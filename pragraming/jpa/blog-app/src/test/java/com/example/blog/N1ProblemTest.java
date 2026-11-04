package com.example.blog;

import com.example.blog.entity.*;
import com.example.blog.repository.*;
import com.example.blog.service.PostService;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class N1ProblemTest {

    @Autowired PostService postService;
    @Autowired EntityManager em;

    @Test
    @Transactional
    void detectN1() {
        Statistics stats = em.getEntityManagerFactory()
                .unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        // این ممکنه N+1 بزنه (لازمه همه lazy هستن)
        var posts = postService.getPublishedPosts(0, 10);

        long queryCount = stats.getPrepareStatementCount();
        System.out.println(">>> Queries executed: " + queryCount);

        // اگه درست fetch کرده باشیم، با 10 post باید ~1-2 query بزنه نه 20+
        assertThat(queryCount).isLessThan(10);
    }
}