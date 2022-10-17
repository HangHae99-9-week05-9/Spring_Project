package com.example.intermediate.controller;

import com.example.intermediate.controller.request.ReCommentRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.ReCommentService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Builder
@Validated
@RequiredArgsConstructor
@RestController
public class ReCommentController {
    private final ReCommentService reCommentService;

    // 생성
    @RequestMapping(value = "/api/auth/recomment", method = RequestMethod.POST)
    public ResponseDto<?> createReComment(@RequestBody ReCommentRequestDto requestDto,
                                          HttpServletRequest request) {
        return reCommentService.createReComment(requestDto, request);
    }

    //대댓글 조회
    @RequestMapping(value = "/api/recomment/{id}", method = RequestMethod.GET)
    public ResponseDto<?> getAllSubComments(@PathVariable Long id){
        return reCommentService.getAllReCommentsByComment(id);
    }

    // 수정
    @RequestMapping(value = "/api/auth/recomment/{id}", method = RequestMethod.PUT)
    public ResponseDto<?> updateSubComment(@PathVariable Long id, @RequestBody ReCommentRequestDto RequestDto,
                                           HttpServletRequest request) {
        return reCommentService.updateReComment(id, RequestDto, request);
    }

    // 삭제
    @RequestMapping(value = "/api/auth/recomment/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> deleteComment(@PathVariable Long id,
                                        HttpServletRequest request) {
        return reCommentService.deleteReComment(id, request);
    }

}
