package com.example.intermediate.jwt;

import com.example.intermediate.controller.exception.CustomException;
import com.example.intermediate.controller.exception.ErrorCode;
import com.example.intermediate.controller.response.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class AccessDeniedHandlerException implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException {
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().println(
        new ObjectMapper().writeValueAsString(
                new CustomException(ErrorCode.MEMBER_LOGIN_REQUIRED)
        )
    );
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
  }
}
