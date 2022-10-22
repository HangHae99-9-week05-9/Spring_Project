package com.example.intermediate.controller;

import com.example.intermediate.controller.request.LoginRequestDto;
import com.example.intermediate.controller.request.MemberRequestDto;
import com.example.intermediate.controller.request.TokenDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.MemberService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;


    //**********************************************
    //                   POST                      *
    //**********************************************

    // 멤버 가입
    @PostMapping(value = "/api/members/signup")
    public ResponseDto<?> signupMembers(@RequestBody @Valid MemberRequestDto requestDto) {
        return memberService.createMember(requestDto);
    }


    // 멤버 로그인
    @PostMapping(value = "/api/members/login")
    public ResponseDto<?> loginMembers(@RequestBody @Valid LoginRequestDto requestDto,
                                       HttpServletResponse response
    ) {
        return memberService.login(requestDto, response);
    }


    // 멤버 로그아웃
    @PostMapping(value = "/api/auth/members/logout")
    public ResponseDto<?> logoutMembers(HttpServletRequest request) {
        return memberService.logout(request);
    }

}
