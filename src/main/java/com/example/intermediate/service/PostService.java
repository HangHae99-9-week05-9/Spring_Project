package com.example.intermediate.service;

import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.controller.response.PostResponseDto;
import com.example.intermediate.domain.Likes;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.CommentRepository;
import com.example.intermediate.repository.LikesRepository;
import com.example.intermediate.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final LikesRepository likesRepository;

  private final TokenProvider tokenProvider;



  @Transactional
  public ResponseDto<?> createPost(PostRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = Post.builder()
        .title(requestDto.getTitle())
        .content(requestDto.getContent())
        .member(member)
        .build();
    postRepository.save(post);
    return ResponseDto.success(
        PostResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .author(post.getMember().getNickname())
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getPost(Long id) {
    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }
    List<CommentResponseDto> commentResponseDtoList = CommentResponseDto.toDtoList(commentRepository.findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc((id)));

    return ResponseDto.success(
            PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .commentResponseDtoList(commentResponseDtoList)
                    .author(post.getMember().getNickname())
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build()
    );

  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllPost(Pageable pageable) {

    // 매개 변수로 pagable을 넘기면 return형은 Page형이다.
    Page<Post> postList = postRepository.findAll(pageable);

    List<PostResponseDto> postResponseDtoList = new ArrayList<>();
    for (Post post : postList) {
      postResponseDtoList.add(PostResponseDto.builder()
              .id(post.getId())
              .title(post.getTitle())
              .content(post.getContent())
              .author(post.getMember().getNickname())
              .createdAt(post.getCreatedAt())
              .modifiedAt(post.getModifiedAt())
              .build()
      );
    }
    return ResponseDto.success(postResponseDtoList);
  }
  @Transactional(readOnly = true)
  public ResponseDto<?> getUserPosts(UserDetailsImpl userDetails) {

    // Post 테이블에 유저 아이디로 작성한 게시글 가져오기.
    List<Post> posts = postRepository.findAllByMemberId(userDetails.getMember().getId());

    // 만약 유저 아이디로 작성한 게시글이 없어 posts가 비어있다면 에러 처리.
    if(posts.isEmpty()){
      return ResponseDto.fail("NOT_FOUND", "해당 유저가 작성한 게시글이 존재하지 않습니다.");
    }

    // 게시물 반환할 객체 리스트 생성
    List<PostResponseDto> postResponseDtoList = new ArrayList<>();

    // 유저가 작성한 게시글들을 postResponseDto 형식으로 postResponseDtoList에 넣어주기.
    for(Post post : posts){

      // 사용자의 id를 통해 사용자가 작성한 게시물을 가져와 PostResponseDto로 변환 후 List에 넣어주기.
      postResponseDtoList.add(
              PostResponseDto.builder()
                      .id(post.getId())
                      .author(post.getMember().getNickname())
                      .title(post.getTitle())
                      .content(post.getContent())
                      .createdAt(post.getCreatedAt())
                      .modifiedAt(post.getModifiedAt())
                      .build()
      );
    }

    return ResponseDto.success(postResponseDtoList);
  }

  @Transactional
  public ResponseDto<Post> updatePost(Long id, PostRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    post.update(requestDto);
    return ResponseDto.success(post);
  }

  @Transactional
  public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
    }

    postRepository.delete(post);
    return ResponseDto.success("delete success");
  }

  @Transactional(readOnly = true)
  public Post isPresentPost(Long id) {
    Optional<Post> optionalPost = postRepository.findById(id);
    return optionalPost.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }

  @Transactional
  public ResponseDto<?> postLikes(long postId, HttpServletRequest request) {

    Post post = postRepository.findById(postId).orElseThrow(() -> {
      throw new RuntimeException("게시글이 존재하지 않습니다.");
    });

    Member member = validateMember(request);
    if(likesRepository.findLikesByMemberAndPost(member, post).isPresent()){
      return ResponseDto.fail("ALREADY_EXIST", "이미 좋아요를 하셨습니다.");
    }
    Likes likes = new Likes();


    likes.setPost(post);
    likes.setMember(member);
    likesRepository.save(likes);

    return ResponseDto.success("success");
  }

}
