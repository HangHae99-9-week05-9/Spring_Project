package com.example.intermediate.controller.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private int status;
    private String message;
    private String code;

    public ErrorResponse(ErrorCode errorCode){
        this.status = errorCode.getHttpStatus();
        this.message = errorCode.getMessage();
        this.code = errorCode.getErrorCode();
    }
}
