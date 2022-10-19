package com.example.intermediate.repository;

import com.example.intermediate.domain.Post;
import java.util.List;
import java.util.Optional;

import com.example.intermediate.domain.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

  List<Post> findAllByMemberId(Long id);
  List<Post> findByPostCategory(PostCategory category);

}
