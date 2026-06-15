package com.team3imple.barosiksa.domain.posts.dto.response;

import com.team3imple.barosiksa.domain.posts.entity.Post;

import java.time.LocalDateTime;

public record PostResponse(
        Long postId,
        String title,
        String writerName,
        int commentCount,
        LocalDateTime createdAt
) {
    public PostResponse(Post post) {
        this(
                post.getId(),
                post.getTitle(),
                post.getMember().getUsername(),
                post.getCommentCount(),
                post.getCreatedAt()
        );
    }
}
