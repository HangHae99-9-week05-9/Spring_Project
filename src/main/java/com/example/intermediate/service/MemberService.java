package com.example.intermediate.service;

import com.example.intermediate.controller.exception.CustomException;
import com.example.intermediate.controller.exception.ErrorCode;
import com.example.intermediate.controller.response.MemberResponseDto;
import com.example.intermediate.domain.Member;
import com.example.intermediate.controller.request.LoginRequestDto;
import com.example.intermediate.controller.request.MemberRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.controller.request.TokenDto;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.MemberRepository;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberRepository memberRepository;

  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;

  @Transactional
  public ResponseDto<?> createMember(MemberRequestDto requestDto) {
    if (null != isPresentMember(requestDto.getEmailId())) {
      throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
    }

    if (null != isPresentMemberNickname(requestDto.getNickname())) {
      throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);

    }

    if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
      throw new CustomException(ErrorCode.PASSWORD_CONFIRM_FAIL);
    }

    Member member = Member.builder()
            .emailId(requestDto.getEmailId())
            .nickname(requestDto.getNickname())
            .password(passwordEncoder.encode(requestDto.getPassword()))
            .build();
    memberRepository.save(member);
    return ResponseDto.success(
        MemberResponseDto.builder()
            .id(member.getId())
            .emailId(member.getEmailId())
            .nickname(member.getNickname())
            .createdAt(member.getCreatedAt())
            .modifiedAt(member.getModifiedAt())
            .build()
    );
  }

  @Transactional
  public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
    Member member = isPresentMember(requestDto.getEmailId());
    if (null == member) {
      throw new CustomException(ErrorCode.LOGIN_MEMBER_ID_FAIL);
    }

    if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
      throw new CustomException(ErrorCode.LOGIN_MEMBER_ID_FAIL);
    }

    TokenDto tokenDto = tokenProvider.generateTokenDto(member);
    tokenToHeaders(tokenDto, response);

    return ResponseDto.success(
        MemberResponseDto.builder()
            .id(member.getId())
            .emailId(member.getEmailId())
            .nickname(member.getNickname())
            .createdAt(member.getCreatedAt())
            .modifiedAt(member.getModifiedAt())
            .build()
    );
  }


  public ResponseDto<?> logout(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh_Token"))) {
      throw new CustomException(ErrorCode.LOGIN_WRONG_FORM_JWT_TOKEN);
    }
    Member member = tokenProvider.getMemberFromAuthentication();
    if (null == member) {
      throw new CustomException(ErrorCode.LOGIN_MEMBER_ID_FAIL);
    }
    return tokenProvider.deleteRefreshToken(member);
  }

  @Transactional(readOnly = true)
  public Member isPresentMember(String emailId) {
    Optional<Member> optionalMember = memberRepository.findByEmailId(emailId);
    return optionalMember.orElse(null);
  }

  @Transactional(readOnly = true)
  public Member isPresentMemberNickname(String nickname) {
    Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
    return optionalMember.orElse(null);
  }


  public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
    response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
    response.addHeader("Refresh_Token", tokenDto.getRefreshToken());
    response.addHeader("Access_Token_Expire_Time", tokenDto.getAccessTokenExpiresIn().toString());
  }

}
