package com.example.intermediate.repository;

import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  // 댓글의 id로 조회하면서 자신의 부모와 fetch join 된 결과를 반환함
  @Query("select c from Comment c left join fetch c.parent where c.id = :id")
  Optional<Comment> findWithParentById(@Param("id") Long id);
  // 부모의 아이디로 오름차순 정렬화되 null을 우선적으로 하고 그다음 자신의 아이디로 오름차순 정렬하여 조회함
  @Query("select c from Comment c join fetch c.member left join fetch c.parent where c.post.id = :postId order by c.parent.id asc nulls first, c.id asc")
  List<Comment> findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc(@Param("postId") Long postId);
  List<Comment> findAllByPost(Post post);
  List<Comment> findAllByMemberId(long id);
}
