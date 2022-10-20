package com.example.intermediate.controller.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.NestedConvertHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Data
@Getter
public class CommentResponseDto {
  private final static String DEFAULT_DELETE_MESSAGE = "삭제된 댓글입니다";

  private Long id;//댓글의 id
  private String content;//내용
  private String member;//댓글 작성자에 대한 정보
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
  private List<CommentResponseDto> children;


  public static List<CommentResponseDto> toDtoList(List<Comment> comments) {
    NestedConvertHelper helper = NestedConvertHelper.newInstance(
            comments,
            c -> new CommentResponseDto(c.getId(), c.isRemoved() ? null : c.getContent(), c.isRemoved() ? null : c.getMember().getNickname(), c.getCreatedAt(), c.getModifiedAt(),new ArrayList<>()),
            Comment::getParent,
            Comment::getId,
            CommentResponseDto::getChildren);
    return helper.convert();


  }
}
