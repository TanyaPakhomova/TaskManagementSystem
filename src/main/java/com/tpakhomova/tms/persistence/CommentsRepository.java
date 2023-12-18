package com.tpakhomova.tms.persistence;

import com.tpakhomova.tms.persistence.data.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByTaskId(Long taskId);
}
