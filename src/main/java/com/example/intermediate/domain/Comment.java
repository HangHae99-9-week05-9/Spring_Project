package com.example.intermediate.domain;

import com.example.intermediate.controller.request.CommentRequestDto;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "member_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Member author;

  @JoinColumn(name = "post_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Post post;
  @JoinColumn(name = "parent_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Comment parent;

  @Column(nullable = false)
  private String content;


  // 댓글 삭제 추가
  private boolean isRemoved = false;


  //상위댓글에 대댓글 리스트 연결, 부모댓글을 삭제해도 자식 댓글은 남아있음
  @OneToMany(mappedBy = "parent", orphanRemoval = true)
  private List<Comment> childList = new ArrayList<>();


  // 대댓글 작성할 댓글 확인
  public void confirmParent(Comment parent){
    this.parent = parent;
    parent.addChild(this);
  }

  // 대댓글 작성
  public void addChild(Comment child){
    childList.add(child);
  }


  public void update(CommentRequestDto commentRequestDto) {
    this.content = commentRequestDto.getContent();
  }

  // 댓글 삭제
  public void remove() {this.isRemoved = true;}
  public boolean validateMember(Member member) {
    return !this.author.equals(member);
  }

  public List<Comment> findRemovableList() {

    List<Comment> result = new ArrayList<>();

    Optional.ofNullable(this.parent).ifPresentOrElse(

            parentComment ->{//대댓글인 경우 (부모가 존재하는 경우)
              if( parentComment.isRemoved()&& parentComment.isAllChildRemoved()){
                result.addAll(parentComment.getChildList());
                result.add(parentComment);
              }
            },

            () -> {//댓글인 경우
              if (isAllChildRemoved()) {
                result.add(this);
                result.addAll(this.getChildList());
              }
            }
    );

    return result;
  }

  //모든 자식 댓글이 삭제되었는지 판단
  private boolean isAllChildRemoved() {
    return getChildList().stream()//
            .map(Comment::isRemoved)//지워졌는지 여부로 바꾼다
            .filter(isRemove -> !isRemove)//지워졌으면 true, 안지워졌으면 false이다. 따라서 filter에 걸러지는 것은 false인것이고, 있다면 false를 없다면 orElse를 통해 true를 반환한다.
            .findAny()//지워지지 않은게 하나라도 있다면 false를 반환
            .orElse(true);//모두 지워졌다면 true를 반환

  }
}

