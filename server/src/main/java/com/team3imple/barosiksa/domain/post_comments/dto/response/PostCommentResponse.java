package com.team3imple.barosiksa.domain.post_comments.dto.response;

import com.team3imple.barosiksa.domain.post_comments.entity.PostComment;

import java.time.LocalDateTime;

public record PostCommentResponse(
        Long commentId,
        String writerName,
        String content,
        LocalDateTime createdAt
) {
    public PostCommentResponse(PostComment comment) {
        this(
                comment.getId(),
                comment.getMember().getUsername(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
