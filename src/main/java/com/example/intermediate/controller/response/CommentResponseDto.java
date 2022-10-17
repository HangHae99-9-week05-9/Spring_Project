package com.example.intermediate.controller.response;

import java.time.LocalDateTime;
import java.util.List;

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

  private Long id;//댓글이 달린 POST의 ID

  private Long commentId;//해당 댓글의 ID
  private String content;//내용 (삭제되었다면 "삭제된 댓글입니다 출력")
  private boolean isRemoved;//삭제되었는지?

  private String member;//댓글 작성자에 대한 정보

  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;

  private List<ReCommentResponseDto> reCommentResponseDtoList;//대댓글에 대한 정보들



}
