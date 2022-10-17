package com.example.intermediate.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReCommentResponseDto {

    private final static String DEFAULT_DELETE_MESSAGE = "삭제된 댓글입니다";

    private Long postId;
    private Long parentId;
    private Long reCommentId;
    private String author;
    private String content;
    private boolean isRemoved;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    /*public RecommentResponseDto(Comment reComment) {
        this.postId = reComment.getPost().getId();
        this.parentId = reComment.getParent().getId();
        this.reCommentId = getReCommentId();
        this.author = getAuthor();
        this.content = getContent();
        this.createdAt = getCreatedAt();
        this.modifiedAt = getModifiedAt();
        if(reComment.isRemoved()){
            this.content = DEFAULT_DELETE_MESSAGE;
        }
        this.isRemoved = reComment.isRemoved();
    }*/
}
