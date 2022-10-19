package com.example.intermediate.domain;

import com.example.intermediate.controller.request.PostRequestDto;
import java.util.List;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  // 연관관계의 주인 설정 , 게시글 삭제시 댓글도 함께 삭제
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments;

  @JoinColumn(name = "member_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  @Convert(converter = PostCategoryConverter.class)
  private PostCategory postCategory;


  public void update(PostRequestDto postRequestDto) {
    this.title = postRequestDto.getTitle();
    this.content = postRequestDto.getContent();
  }

  public boolean validateMember(Member member) {
    return !this.member.equals(member);
  }

}
