package com.example.intermediate.controller.request;

import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
  private Long postId;
  private String content;

}
