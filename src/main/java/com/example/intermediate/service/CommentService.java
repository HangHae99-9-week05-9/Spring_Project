package com.example.intermediate.service;

import com.example.intermediate.controller.exception.CustomException;
import com.example.intermediate.controller.exception.ErrorCode;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.controller.request.CommentRequestDto;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.CommentRepository;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final TokenProvider tokenProvider;
  private final PostService postService;

  @Transactional
  public ResponseDto<?> createComment(Long postId, CommentRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token") || null == request.getHeader("Authorization")) {
      throw new CustomException(ErrorCode.MEMBER_LOGIN_REQUIRED);
    }

    Member member = validateMember(request);
    if (null == member) {
      throw new CustomException(ErrorCode.LOGIN_WRONG_FORM_JWT_TOKEN);
    }

    Post post = postService.isPresentPost(postId);
    if (null == post) {
      throw new CustomException(ErrorCode.POST_NOT_FOUND);
    }

    Comment parent = Optional.ofNullable(requestDto.getParentId())
            .map(parentId -> commentRepository.findById(parentId).orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND)))
            .orElse(null);

    Comment comment = commentRepository.save(
            Comment.builder()
                    .content(requestDto.getContent())
                    .member(member)
                    .post(post)
                    .parent(parent)
                    .build());

    return ResponseDto.success(
            CommentResponseDto.builder()
                    .id(comment.getPost().getId())
                    .member(comment.getMember().getNickname())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .modifiedAt(comment.getModifiedAt())
                    .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllCommentsByPost( Long id, Pageable pageable) {
    Post post = postService.isPresentPost(id);
    if (null == post) {
      throw new CustomException(ErrorCode.POST_NOT_FOUND);
    }

    // 매개 변수로 pagable을 넘기면 return형은 Page형이다.
    Page<Comment> pages = commentRepository.findAll(pageable);

    List<CommentResponseDto> commentResponseDtoList = CommentResponseDto.toDtoList(commentRepository.findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc((id)));

    return ResponseDto.success(commentResponseDtoList);
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllComments(@AuthenticationPrincipal UserDetailsImpl userDetails) {

    // 멤버 id를 통해 Comment 테이블에서 comment를 가져오기.
    List<Comment> comments = commentRepository.findAllByMemberId(userDetails.getMember().getId());

    // 만약 유저 아이디로 작성한 댓글이 없어 comments가 비어있다면 에러 처리.
    if(comments.isEmpty()){
      throw new CustomException(ErrorCode.POST_NOT_FOUND);
    }

    // 댓글 반환할 객체 리스트 생성
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

    for(Comment comment : comments){
      commentResponseDtoList.add(
          CommentResponseDto.builder()
             .id(comment.getId())
             .member(comment.getMember().getNickname())
             .content(comment.getContent())
             .createdAt(comment.getCreatedAt())
                  .modifiedAt(comment.getModifiedAt())
                  .build()
      );
    }

    return ResponseDto.success(commentResponseDtoList);

  }

  @Transactional
  public ResponseDto<?> updateComment( Long id, CommentRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token") || null == request.getHeader("Authorization")) {
      throw new CustomException(ErrorCode.MEMBER_LOGIN_REQUIRED);
    }
    Member member = validateMember(request);
    if (null == member) {
      throw new CustomException(ErrorCode.LOGIN_WRONG_FORM_JWT_TOKEN);
    }

    Comment comment = isPresentComment(id);
    if (null == comment) {
      throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
    }

    if (comment.validateMember(member)) {
      throw new CustomException(ErrorCode.MEMBER_NOT_VALIDATED);
    }

    comment.update(requestDto);
    return ResponseDto.success(
            CommentResponseDto.builder()
                    .id(comment.getId())
                    .member(comment.getMember().getNickname())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .modifiedAt(comment.getModifiedAt())
                    .build()
    );
  }


  @Transactional
  public ResponseDto<?> deleteComment( Long id, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token") || null == request.getHeader("Authorization")) {
      throw new CustomException(ErrorCode.MEMBER_LOGIN_REQUIRED);
    }
    Member member = validateMember(request);
    if (null == member) {
      throw new CustomException(ErrorCode.LOGIN_WRONG_FORM_JWT_TOKEN);
    }

    Comment commentId = isPresentComment(id);
    if (null == commentId) {
      throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
    }

    if (commentId.validateMember(member)) {
      throw new CustomException(ErrorCode.MEMBER_NOT_VALIDATED);
    }
    Comment comment = commentRepository.findWithParentById(id).orElseThrow( () -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    comment.findDeletableComment().ifPresentOrElse(commentRepository::delete, comment::remove);

    return ResponseDto.success("success");
  }

  @Transactional(readOnly = true)
  public Comment isPresentComment( Long id) {
    Optional<Comment> optionalComment = commentRepository.findById(id);
    return optionalComment.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }

}
