package com.team3imple.barosiksa.domain.post_comments.repository;

import com.team3imple.barosiksa.domain.post_comments.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    List<PostComment> findByPostIdOrderByIdAsc(Long postId);
}
