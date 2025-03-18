package com.pumping.domain.comment.repository;

import com.pumping.domain.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
