package com.example.intermediate.controller;

import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.controller.request.CommentRequestDto;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.service.CommentService;
import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
public class CommentController {



  private final CommentService commentService;

  @ApiImplicitParams({
          @ApiImplicitParam(
                  name = "Refresh-Token",
                  required = true,
                  dataType = "string",
                  paramType = "header"
          )
  })

  @PostMapping(value = "/api/auth/{postId}/comments")
  public ResponseDto<?> createComment(@PathVariable  Long id, @RequestBody CommentRequestDto requestDto,
                                      HttpServletRequest request) {
    return commentService.createComment(id, requestDto, request);
  }

  @GetMapping(value = "/api/comments/{id}")

  // 정렬 기준이 여러 개일 시 @PageableDefault만으로 안되고 @SortDefault를 사용하여 정렬해아 한다.
  public ResponseDto<?> getAllComments(@PathVariable  Long id,
                                       @PageableDefault(page = 0, size = 3)
                                       @SortDefault.SortDefaults({
                                               @SortDefault(sort = "post", direction = Sort.Direction.DESC),
                                               @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                       })
                                               Pageable pageable) {
    return commentService.getAllCommentsByPost(id, pageable);
  }

  // 멤버가 작성한 댓글 조회
  @GetMapping(value = "/api/auth/comments")
  public ResponseDto<?> userComments(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    return commentService.getAllComments(userDetails);
  }

  @PutMapping(value = "/api/auth/comments/{commentId}")
  public ResponseDto<?> updateComment(@PathVariable Long id, @RequestBody CommentRequestDto requestDto,
                                      HttpServletRequest request) {
    return commentService.updateComment(id, requestDto, request);
  }

  @DeleteMapping(value = "/api/auth/comments/{commentId}")
  public ResponseDto<?> deleteComment(@PathVariable Long id,
                                      HttpServletRequest request) {
    return commentService.deleteComment(id, request);
  }


}

