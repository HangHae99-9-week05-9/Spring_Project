package com.example.intermediate.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
  @Nullable
  private Long parentId;
  private Long postId;
  private String content;
}
