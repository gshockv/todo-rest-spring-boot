package com.gshockv.todoapp.data.repo;

import com.gshockv.todoapp.data.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link TodoEntity}.
 */
@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Integer>
{
}
