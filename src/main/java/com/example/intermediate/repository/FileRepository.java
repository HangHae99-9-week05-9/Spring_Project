package com.example.intermediate.repository;

import com.example.intermediate.domain.File;
import com.example.intermediate.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findAllByMemberId(Long id);
}