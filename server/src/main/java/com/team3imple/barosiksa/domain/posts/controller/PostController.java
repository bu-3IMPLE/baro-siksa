package com.team3imple.barosiksa.domain.posts.controller;

import com.team3imple.barosiksa.domain.post_comments.dto.request.PostCommentCreateRequest;
import com.team3imple.barosiksa.domain.post_comments.dto.response.PostCommentResponse;
import com.team3imple.barosiksa.domain.post_comments.service.PostCommentService;
import com.team3imple.barosiksa.domain.posts.dto.request.PostCreateRequest;
import com.team3imple.barosiksa.domain.posts.dto.response.PostDetailResponse;
import com.team3imple.barosiksa.domain.posts.dto.response.PostResponse;
import com.team3imple.barosiksa.domain.posts.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Post", description = "맛집 공유 게시판 API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostCommentService postCommentService;

    @Operation(summary = "게시글 목록 조회", description = "게시글을 최신순 페이징으로 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getPosts(
            @PageableDefault(size = 4, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(postService.getPosts(pageable));
    }

    @Operation(summary = "게시글 상세 조회")
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @Operation(summary = "게시글 작성")
    @PostMapping
    public ResponseEntity<Long> createPost(
            Principal principal,
            @Valid @RequestBody PostCreateRequest request) {
        Long memberId = Long.valueOf(principal.getName());
        return ResponseEntity.ok(postService.createPost(memberId, request));
    }

    @Operation(summary = "게시글 삭제", description = "작성자 본인만 삭제 가능합니다.")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            Principal principal,
            @PathVariable Long postId) {
        Long memberId = Long.valueOf(principal.getName());
        postService.deletePost(memberId, postId);
        return ResponseEntity.ok().build();
    }

    // ========================= 댓글 =========================

    @Operation(summary = "댓글 목록 조회")
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<PostCommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(postCommentService.getComments(postId));
    }

    @Operation(summary = "댓글 작성")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Long> createComment(
            Principal principal,
            @PathVariable Long postId,
            @Valid @RequestBody PostCommentCreateRequest request) {
        Long memberId = Long.valueOf(principal.getName());
        return ResponseEntity.ok(postCommentService.createComment(memberId, postId, request));
    }

    @Operation(summary = "댓글 삭제", description = "작성자 본인만 삭제 가능합니다.")
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            Principal principal,
            @PathVariable Long postId,
            @PathVariable Long commentId) {
        Long memberId = Long.valueOf(principal.getName());
        postCommentService.deleteComment(memberId, postId, commentId);
        return ResponseEntity.ok().build();
    }
}
