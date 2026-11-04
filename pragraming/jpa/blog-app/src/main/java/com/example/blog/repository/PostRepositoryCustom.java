package com.example.blog.repository;

import com.example.blog.dto.PostSearchCriteria;
import com.example.blog.dto.PostSummary;
import org.springframework.data.domain.Page;

public interface PostRepositoryCustom {
    Page<PostSummary> searchDynamic(PostSearchCriteria criteria);
}