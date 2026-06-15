package com.team3imple.barosiksa.domain.posts.service;

import com.team3imple.barosiksa.domain.member.entity.Member;
import com.team3imple.barosiksa.domain.member.repository.MemberRepository;
import com.team3imple.barosiksa.domain.posts.dto.request.PostCreateRequest;
import com.team3imple.barosiksa.domain.posts.dto.response.PostDetailResponse;
import com.team3imple.barosiksa.domain.posts.dto.response.PostResponse;
import com.team3imple.barosiksa.domain.posts.entity.Post;
import com.team3imple.barosiksa.domain.posts.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Page<PostResponse> getPosts(Pageable pageable) {
        return postRepository.findAllByOrderByIdDesc(pageable)
                .map(PostResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getMyPosts(Long memberId, Pageable pageable) {
        return postRepository.findByMemberIdOrderByIdDesc(memberId, pageable)
                .map(PostResponse::new);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        return new PostDetailResponse(post);
    }

    @Transactional
    public Long createPost(Long memberId, PostCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Post post = Post.builder()
                .member(member)
                .title(request.title())
                .content(request.content())
                .build();

        return postRepository.save(post).getId();
    }

    @Transactional
    public void deletePost(Long memberId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        if (!post.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("게시글 작성자 본인만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
    }
}
