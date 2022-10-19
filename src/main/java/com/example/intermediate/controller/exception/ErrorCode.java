package com.example.intermediate.controller.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    DUPLICATED_NICKNAME(404, "COMMON-ERR-404", "중복된 닉네임이 존재합니다."),

    DUPLICATED_EMAIL(400, "COMMON-ERR-400", "중복된 이메일이 존재합니다."),

    PASSWORD_CONFIRM_FAIL(400, "COMMON-ERR-400", "비밀번호가 일치하지 않습니다. "),

    LOGIN_MEMBER_ID_FAIL(404, "COMMON-ERR-404", "해당하는 멤버가 없습니다."),

    LOGIN_PASSWORD_FAIL(400, "COMMON-ERR-400", "Password가 틀렸습니다."),

    COMMENT_NOT_FOUND(404, "COMMON-ERR-404", "해당하는 ID의 댓글이 없습니다."),

    POST_NOT_FOUND(404, "COMMON-ERR-404", "해당하는 ID의 글이 없습니다."),

    MEMBER_POST_NOT_FOUND(404, "COMMON-ERR-404", "해당 유저가 작성한 글이 없습니다."),

    MEMBER_COMMENT_NOT_FOUND(404, "COMMON-ERR-404", "해당 유저가 작성한 댓글이 없습니다."),

    MEMBER_LOGIN_REQUIRED(404, "COMMON-ERR-404", "로그인이 필요합니다."),

    MEMBER_NOT_VALIDATED(400, "COMMON-ERR-400", "작성자만 수정할 수 있습니다."),

    ALREADY_PUT_LIKE(400, "COMMON-ERR-400", "이미 좋아요를 하셨습니다."),

    NOT_FOUND_LIKES(404, "COMMON-ERR-404", "좋아요를 누른 게시글이 없습니다."),
    LOGIN_WRONG_FORM_JWT_TOKEN(400, "COMMON-ERR-400", "유효한 JWT 토큰이 아닙니다."),

    LOGIN_MEMBER_REQUIRED_INFORMATION_FAIL(400, "COMMON-ERR-400", "필수 입력 정보를 입력 후 시도해주세요"),

    INTERNAL_SERVER_ERROR_PLZ_CHECK(500, "SERVER-ERROR-500", "알수없는 서버 내부 에러 발생 "),

    CONVERT_NESTED_STRUCTURE(500, "SERVER-ERROR-500", "댓글 변환작업 실패");

    private final int httpStatus;
    private final String errorCode;
    private final String message;
}
