package com.team3imple.barosiksa.domain.post_comments.service;

import com.team3imple.barosiksa.domain.member.entity.Member;
import com.team3imple.barosiksa.domain.member.repository.MemberRepository;
import com.team3imple.barosiksa.domain.post_comments.dto.request.PostCommentCreateRequest;
import com.team3imple.barosiksa.domain.post_comments.dto.response.PostCommentResponse;
import com.team3imple.barosiksa.domain.post_comments.entity.PostComment;
import com.team3imple.barosiksa.domain.post_comments.repository.PostCommentRepository;
import com.team3imple.barosiksa.domain.posts.entity.Post;
import com.team3imple.barosiksa.domain.posts.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<PostCommentResponse> getComments(Long postId) {
        return postCommentRepository.findByPostIdOrderByIdAsc(postId).stream()
                .map(PostCommentResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createComment(Long memberId, Long postId, PostCommentCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        PostComment comment = PostComment.builder()
                .post(post)
                .member(member)
                .content(request.content())
                .build();

        return postCommentRepository.save(comment).getId();
    }

    @Transactional
    public void deleteComment(Long memberId, Long postId, Long commentId) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if (!comment.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("댓글 작성자 본인만 삭제할 수 있습니다.");
        }

        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("해당 게시글의 댓글이 아닙니다.");
        }

        postCommentRepository.delete(comment);
    }
}
