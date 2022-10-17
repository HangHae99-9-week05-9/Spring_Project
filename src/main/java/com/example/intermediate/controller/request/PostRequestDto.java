package com.example.intermediate.controller.request;

import com.example.intermediate.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
  private String title;
  private String content;

  public Post toEntity() {
    return Post.builder().title(title).content(content).build();
  }
}
