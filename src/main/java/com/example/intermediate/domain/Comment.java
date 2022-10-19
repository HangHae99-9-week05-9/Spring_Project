package com.example.intermediate.domain;

import com.example.intermediate.controller.request.CommentRequestDto;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Slf4j
public class Comment extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "member_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  @JoinColumn(name = "post_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Post post;

  @Column(nullable = false)
  private String content;

  // 댓글 삭제 추가
  private boolean isRemoved;

  @JoinColumn(name = "parent_id")
  @ManyToOne(fetch = FetchType.LAZY)
  //OnDelete는 JPA에서는 단일한 DELETE 쿼리만 전송하여 참조하는 레코드들을 연쇄적으로 제거해줌
  //CascadeTypa.REMOVE 방식은 JPA에서 외래 키를 통해 참조하는 레코드들을 제거하기 위해 그 개수만큼 DELETE 쿼리 전송해야함
  // 참고: https://kukekyakya.tistory.com/m/546
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Comment parent;

  @Builder.Default
  // 각 댓글의 하위 댓글을 참조 가능하도록 연관관계 맺음
  @OneToMany(mappedBy = "parent")
  private List<Comment> children = new ArrayList<>();

  public Comment(String content, Member member, Post post, Comment parent) {
    this.content = content;
    this.member = member;
    this.post = post;
    this.parent = parent;
    this.isRemoved = false;
  }

  // 현재 댓글 기준으로 실제로 삭제 가능한 댓글을 찾아줌
  // 이 메소드의 결과로 찾아낸 댓글이 없다면 실제 데이터를 제거하는 것이 아니라 remove 메소드로 삭제 표시만 해줘야함
  public Optional<Comment> findDeletableComment() {
    return hasChildren() ? Optional.empty() : Optional.of(findDeletableCommentByParent());
  }

  private Comment findDeletableCommentByParent() {
    if (isRemovedParent()) {
      Comment deletableParent = getParent().findDeletableCommentByParent();
      if(getParent().getChildren().size() == 1) return deletableParent;
    }
    return this;
  }

  private boolean hasChildren() {
    return getChildren().size() != 0;
  }

  //현재 댓글이 삭제 가능한지 판별
  private boolean isRemovedParent() {
    return getParent() != null && getParent().isRemoved();
  }

  public void update(CommentRequestDto commentRequestDto) {
    this.content = commentRequestDto.getContent();
  }

  // 댓글 삭제
  public void remove() {this.isRemoved = true;}
  public boolean validateMember(Member member) {
    return !this.member.equals(member);
  }

}

