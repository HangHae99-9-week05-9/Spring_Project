package com.example.intermediate.controller;

import com.example.intermediate.configuration.SwaggerAnnotation;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.controller.request.CommentRequestDto;
import com.example.intermediate.service.CommentService;
import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;


    //**********************************************
    //                   POST                      *
    //**********************************************


    // 게시글에 대한 댓글 작성
    @SwaggerAnnotation
    @PostMapping(value = "/api/auth/{postId}/comments")
    public ResponseDto<?> createComments(@PathVariable Long postId,
                                         @RequestBody CommentRequestDto requestDto,
                                         HttpServletRequest request) {
        return commentService.createComment(postId, requestDto, request);
    }


    //**********************************************
    //                   GET                       *
    //**********************************************

    // 게시글에 대한 모든 댓글 조회
    @GetMapping(value = "/api/comments/{id}")
    // 정렬 기준이 여러 개일 시 @PageableDefault만으로 안되고 @SortDefault를 사용하여 정렬해아 한다.
    public ResponseDto<?> getAllComments(@PathVariable Long id,
                                         @PageableDefault(page = 0, size = 3)
                                         @SortDefault.SortDefaults({
                                                 @SortDefault(sort = "post", direction = Sort.Direction.DESC),
                                                 @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                         })
                                         Pageable pageable) {
        return commentService.getAllCommentsByPost(id, pageable);
    }


    // 멤버가 작성한 댓글 조회
    @SwaggerAnnotation
    @GetMapping(value = "/api/auth/comments")
    public ResponseDto<?> getUserComments(HttpServletRequest request) {
        return commentService.getAllComments(request);
    }


    //**********************************************
    //                   PUT                       *
    //**********************************************

    // 댓글 수정
    @SwaggerAnnotation
    @PutMapping(value = "/api/auth/comments/{id}")
    public ResponseDto<?> updateComments(@PathVariable Long id, @RequestBody CommentRequestDto requestDto,
                                         HttpServletRequest request) {
        return commentService.updateComment(id, requestDto, request);
    }


    //**********************************************
    //                   DELETE                    *
    //**********************************************

    // 댓글 삭제
    @SwaggerAnnotation
    @DeleteMapping(value = "/api/auth/comments/{id}")
    public ResponseDto<?> deleteComments(@PathVariable Long id,
                                         HttpServletRequest request) {
        return commentService.deleteComment(id, request);
    }
}

