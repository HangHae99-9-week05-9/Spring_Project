package com.example.intermediate.service;

import com.example.intermediate.controller.exception.CustomException;
import com.example.intermediate.controller.exception.ErrorCode;
import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.controller.response.PostResponseDto;
import com.example.intermediate.domain.*;
import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
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
    if (null == request.getHeader("Refresh_Token") || null == request.getHeader("Authorization")) {
      throw new CustomException(ErrorCode.MEMBER_LOGIN_REQUIRED);
    }
    Member member = validateMember(request);
    if (null == member) {
      throw new CustomException(ErrorCode.LOGIN_WRONG_FORM_JWT_TOKEN);
    }

    Post post = Post.builder()
        .title(requestDto.getTitle())
        .content(requestDto.getContent())
        .member(member)
        .postCategory(requestDto.getPostCategory())
        .build();
    postRepository.save(post);
    return ResponseDto.success(
        PostResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .author(post.getMember().getNickname())
            .postCategory(post.getPostCategory())
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getPost(Long id) {
    Post post = isPresentPost(id);
    if (null == post) {
      throw new CustomException(ErrorCode.POST_NOT_FOUND);
    }
    List<CommentResponseDto> commentResponseDtoList = CommentResponseDto.toDtoList(commentRepository.findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc((id)));

    return ResponseDto.success(
            PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .author(post.getMember().getNickname())
                    .postCategory(post.getPostCategory())
                    .commentResponseDtoList(commentResponseDtoList)
                    .likesCount(post.getLikes().size())
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
              .postCategory(post.getPostCategory())
              .likesCount(post.getLikes().size())
              .createdAt(post.getCreatedAt())
              .modifiedAt(post.getModifiedAt())
              .build()
      );
    }
    return ResponseDto.success(postResponseDtoList);
  }

  // 카테고리 별로 게시글 조회하기
  @Transactional
  public ResponseDto<?> getPostsByCategory(String category) {
    PostCategory categoryEnum = PostCategory.valueOf(category);
    List<Post> posts = postRepository.findByPostCategory(categoryEnum);

    if(posts.isEmpty()){
      return ResponseDto.fail("NOT_FOUND", "해당 유저가 작성한 게시글이 존재하지 않습니다.");
    }

    List<PostResponseDto> postResponseDtoList = new ArrayList<>();

    for (Post post : posts) {
      postResponseDtoList.add(PostResponseDto.builder()
              .id(post.getId())
              .title(post.getTitle())
              .content(post.getContent())
              .author(post.getMember().getNickname())
              .postCategory(post.getPostCategory())
              .postCategory(post.getPostCategory())
              .likesCount(post.getLikes().size())
              .createdAt(post.getCreatedAt())
              .modifiedAt(post.getModifiedAt())
              .build()
      );
    }
    return ResponseDto.success(postResponseDtoList);
  }


  @Transactional(readOnly = true)
  public ResponseDto<?> getUserPosts(HttpServletRequest request) {

    if (null == request.getHeader("Refresh_Token") || null == request.getHeader("Authorization")) {
      throw new CustomException(ErrorCode.MEMBER_LOGIN_REQUIRED);
    }
    Member member = validateMember(request);
    if (null == member) {
      throw new CustomException(ErrorCode.LOGIN_WRONG_FORM_JWT_TOKEN);
    }

    // Post 테이블에 유저 아이디로 작성한 게시글 가져오기.
    List<Post> posts = postRepository.findAllByMemberId(member.getId());

    // 만약 유저 아이디로 작성한 게시글이 없어 posts가 비어있다면 에러 처리.
    if(posts.isEmpty()){
      throw new CustomException(ErrorCode.MEMBER_POST_NOT_FOUND);
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
                      .postCategory(post.getPostCategory())
                      .likesCount(post.getLikes().size())
                      .createdAt(post.getCreatedAt())
                      .modifiedAt(post.getModifiedAt())
                      .build()
      );
    }

    return ResponseDto.success(postResponseDtoList);
  }

  @Transactional
  public ResponseDto<Post> updatePost(Long id, PostRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("Refresh_Token") || null == request.getHeader("Authorization")) {
      throw new CustomException(ErrorCode.MEMBER_LOGIN_REQUIRED);
    }
    Member member = validateMember(request);
    if (null == member) {
      throw new CustomException(ErrorCode.LOGIN_WRONG_FORM_JWT_TOKEN);
    }

    Post post = isPresentPost(id);
    if (null == post) {
      throw new CustomException(ErrorCode.POST_NOT_FOUND);
    }


    if (post.validateMember(member)) {
      throw new CustomException(ErrorCode.MEMBER_NOT_VALIDATED);
    }

    post.update(requestDto);
    return ResponseDto.success(post);
  }

  @Transactional
  public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {
    if (null == request.getHeader("Refresh_Token") || null == request.getHeader("Authorization")) {
      throw new CustomException(ErrorCode.MEMBER_LOGIN_REQUIRED);
    }
    Member member = validateMember(request);
    if (null == member) {
      throw new CustomException(ErrorCode.LOGIN_WRONG_FORM_JWT_TOKEN);
    }

    Post post = isPresentPost(id);
    if (null == post) {
      throw new CustomException(ErrorCode.POST_NOT_FOUND);
    }


    if (post.validateMember(member)) {
      throw new CustomException(ErrorCode.MEMBER_NOT_VALIDATED);
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
    if (!tokenProvider.validateToken(request.getHeader("Refresh_Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }

  @Transactional
  public ResponseDto<?> postLikes(Long postId, HttpServletRequest request) {

    Post post = isPresentPost(postId);
    if (null == post) {
      throw new CustomException(ErrorCode.POST_NOT_FOUND);
    }

    if (null == request.getHeader("Refresh_Token") || null == request.getHeader("Authorization")) {
      throw new CustomException(ErrorCode.MEMBER_LOGIN_REQUIRED);
    }
    Member member = validateMember(request);
    if (null == member) {
      throw new CustomException(ErrorCode.LOGIN_WRONG_FORM_JWT_TOKEN);
    }


    if(likesRepository.findLikesByMemberAndPost(member, post).isPresent()){
      throw new CustomException(ErrorCode.ALREADY_PUT_LIKE);

    }
    Likes likes = new Likes();


    likes.setPost(post);
    likes.setMember(member);
    likesRepository.save(likes);

    return ResponseDto.success("좋아요를 하셨습니다.");
  }

  public ResponseDto<?> getPostsLike(HttpServletRequest request) {

    if (null == request.getHeader("Refresh_Token") || null == request.getHeader("Authorization")) {
      throw new CustomException(ErrorCode.MEMBER_LOGIN_REQUIRED);
    }
    Member member = validateMember(request);
    if (null == member) {
      throw new CustomException(ErrorCode.LOGIN_WRONG_FORM_JWT_TOKEN);
    }

    List<Likes> likelist = likesRepository.findLikesByMember(member);

    if (likelist.isEmpty()) {
      throw new CustomException(ErrorCode.NOT_FOUND_LIKES);
    }

    List<PostResponseDto> postResDtoList = new ArrayList<>();

    for (Likes like : likelist) {
      Post post = like.getPost();

      postResDtoList.add(
              PostResponseDto.builder()
                      .id(post.getId())
                      .title(post.getTitle())
                      .content(post.getContent())
                      .author(post.getMember().getNickname())
                      .postCategory(post.getPostCategory())
                      .likesCount(post.getLikes().size())
                      .createdAt(post.getCreatedAt())
                      .modifiedAt(post.getModifiedAt())
                      .build()
      );
    }

    return ResponseDto.success(postResDtoList);
  }


}
