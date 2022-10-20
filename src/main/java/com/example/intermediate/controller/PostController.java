package com.example.intermediate.controller;

import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.service.PostService;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;


    //**********************************************
    //                   POST                      *
    //**********************************************

    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "Refresh-Token",
                    required = true,
                    dataType = "string",
                    paramType = "header"
            )
    })
    // 게시글 작성
    @PostMapping(value = "/api/auth/posts")
    public ResponseDto<?> createPosts(@RequestBody PostRequestDto requestDto,
                                      HttpServletRequest request) {
        return postService.createPost(requestDto, request);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "Refresh-Token",
                    required = true,
                    dataType = "string",
                    paramType = "header"
            )
    })
    // 게시글 좋아요
    @PostMapping(value = "api/posts/{postid}/likes")
    public ResponseDto<?> likesPosts(@PathVariable Long postid, HttpServletRequest request) {
        return postService.postLikes(postid, request);
    }


    //**********************************************
    //                   GET                       *
    //**********************************************

    // 모든 게시물 조회
    @GetMapping(value = "/api/posts")
    // @Pagable을 통해 보여줄 페이시 위치(0이 시작), 한 페이지에 게시글 개수(15), 정렬 기준(createdAt), 정렬 기준의 순서(내림차순)을 정의
    public ResponseDto<?> getAllPosts(@PageableDefault(page = 0, size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return postService.getAllPost(pageable);
    }

    // 게시물 상세조회
    @GetMapping(value = "/api/posts/{id}")
    public ResponseDto<?> getPosts(@PathVariable Long id) {
        return postService.getPost(id);
    }

    // 멤버가 작성한 글 조회
    @GetMapping(value = "/api/auth/members/posts")
    public ResponseDto<?> getMembersPosts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getUserPosts(userDetails);
    }

    // 멤버가 좋아요한 글 조회
    @GetMapping(value = "/api/members/posts/likes")
    public ResponseDto<?> getLikesPosts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getPostsLike(userDetails);
    }


    //**********************************************
    //                   PUT                       *
    //**********************************************

    // 게시글 수정
    @PutMapping(value = "/api/auth/posts/{id}")
    public ResponseDto<?> updatePosts(@PathVariable Long id, @RequestBody PostRequestDto postRequestDto,
                                      HttpServletRequest request) {
        return postService.updatePost(id, postRequestDto, request);
    }


    //**********************************************
    //                   DELETE                    *
    //**********************************************

    //게시글 삭제
    @DeleteMapping(value = "/api/auth/posts/{id}")
    public ResponseDto<?> deletePosts(@PathVariable Long id,
                                      HttpServletRequest request) {
        return postService.deletePost(id, request);
    }
}
