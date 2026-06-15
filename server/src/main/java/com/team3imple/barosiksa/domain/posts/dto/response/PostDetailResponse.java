package com.team3imple.barosiksa.domain.posts.dto.response;

import com.team3imple.barosiksa.domain.posts.entity.Post;

import java.time.LocalDateTime;

public record PostDetailResponse(
        Long postId,
        String title,
        String writerName,
        String content,
        int commentCount,
        LocalDateTime createdAt
) {
    public PostDetailResponse(Post post) {
        this(
                post.getId(),
                post.getTitle(),
                post.getMember().getUsername(),
                post.getContent(),
                post.getCommentCount(),
                post.getCreatedAt()
        );
    }
}
