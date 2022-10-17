package com.example.intermediate.service;

import com.example.intermediate.controller.response.ReCommentResponseDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.controller.request.CommentRequestDto;
import com.example.intermediate.domain.ReComment;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.CommentRepository;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

import com.example.intermediate.repository.ReCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final ReCommentRepository recommentRepository;

  private final TokenProvider tokenProvider;
  private final PostService postService;

  @Transactional
  public ResponseDto<?> createComment(CommentRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    if (null == request.getHeader("memberization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = postService.isPresentPost(requestDto.getId());
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    Comment comment = Comment.builder()
            .member(member)
            .post(post)
            .content(requestDto.getContent())
            .build();
    commentRepository.save(comment);
    return ResponseDto.success(
            CommentResponseDto.builder()
                    .id(comment.getPost().getId())
                    .commentId(comment.getId())
                    .member(comment.getMember().getNickname())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .modifiedAt(comment.getModifiedAt())
                    .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllCommentsByPost(Long id) {
    Post post = postService.isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    List<Comment> commentList = commentRepository.findAllByPost(post);
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
    List<ReComment> reCommentList = new ArrayList<>();
    List<ReCommentResponseDto> reCommentResponseDtoList = new ArrayList<>();

    for (Comment comment : commentList) {
      reCommentList = recommentRepository.findAllByComment(comment);
      for (ReComment reComment : reCommentList) {
        reCommentResponseDtoList.add(
                ReCommentResponseDto.builder()
                        .reCommentId(reComment.getId())
                        .member(reComment.getMember().getNickname())
                        .content(reComment.getContent())
                        .createdAt(reComment.getCreatedAt())
                        .modifiedAt(reComment.getModifiedAt())
                        .build()
        );
      }
      commentResponseDtoList.add(
              CommentResponseDto.builder()
                      .id(comment.getPost().getId())
                      .commentId(comment.getId())
                      .member(comment.getMember().getNickname())
                      .content(comment.getContent())
                      .reCommentResponseDtoList(reCommentResponseDtoList)
                      .createdAt(comment.getCreatedAt())
                      .modifiedAt(comment.getModifiedAt())
                      .build()
      );
    }
    return ResponseDto.success(commentResponseDtoList);
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllComments(@AuthenticationPrincipal UserDetailsImpl userDetails) {

    // 멤버 id를 통해 Comment 테이블에서 comment를 가져오기.
    List<Comment> comments = commentRepository.findAllByMemberId(userDetails.getMember().getId());

    // 만약 유저 아이디로 작성한 댓글이 없어 comments가 비어있다면 에러 처리.
    if(comments.isEmpty()){
      return ResponseDto.fail("NOT_FOUND", "해당 유저가 작성한 게시글이 존재하지 않습니다.");
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
  public ResponseDto<?> updateComment(Long id, CommentRequestDto requestDto, HttpServletRequest request) {
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

    Post post = postService.isPresentPost(requestDto.getId());
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    Comment comment = isPresentComment(id);
    if (null == comment) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
    }

    if (comment.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    comment.update(requestDto);
    return ResponseDto.success(
            CommentResponseDto.builder()
                    .id(comment.getPost().getId())
                    .commentId(comment.getId())
                    .member(comment.getMember().getNickname())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .modifiedAt(comment.getModifiedAt())
                    .build()
    );
  }




  @Transactional
  public ResponseDto<?> deleteComment(Long id, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    if (null == request.getHeader("memberization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Comment comment = isPresentComment(id);
    if (null == comment) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
    }

    if (comment.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    commentRepository.delete(comment);
    return ResponseDto.success("success");
  }

  @Transactional(readOnly = true)
  public Comment isPresentComment(Long id) {
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
