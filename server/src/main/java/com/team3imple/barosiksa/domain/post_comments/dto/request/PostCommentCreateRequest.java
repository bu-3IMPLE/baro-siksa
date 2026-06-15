package com.team3imple.barosiksa.domain.post_comments.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PostCommentCreateRequest(
        @NotBlank String content
) {}
