package com.example.intermediate.domain;

import com.example.intermediate.controller.request.ReCommentRequestDto;
import lombok.*;

import javax.persistence.*;
@Setter
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReComment extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "member_id", nullable = false)//테이블
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "comment_id", nullable = false)  //테이블끼리 매핑시켜줌
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;

    @Column(nullable = false)
    private String reComment; // 대댓글

    public void update(ReCommentRequestDto recommentRequestDto) {
        this.reComment = recommentRequestDto.getContent();
    }

    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }


}
