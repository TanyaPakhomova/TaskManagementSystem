package com.tpakhomova.tms.persistence;

import com.tpakhomova.tms.persistence.data.TaskEntity;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findByAuthorEmail(String authorEmail);

    List<TaskEntity> findByAssigneeEmail(String assigneeEmail);
}
