package com.gshockv.todoapp.service.internal;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.gshockv.todoapp.data.ResourceNotFoundException;
import com.gshockv.todoapp.data.TodoItem;
import com.gshockv.todoapp.data.entity.TodoEntity;
import com.gshockv.todoapp.data.repo.TodoRepository;
import com.gshockv.todoapp.service.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TodoDataService
    implements TodoService
{
  private static final String RES_NOT_FOUND_PLACEHOLDER = "Todo (%d) is not found.";

  @Autowired
  private TodoRepository todoRepository;

  @Override
  @Transactional(readOnly = true)
  public List<TodoItem> findAll() {
    return todoRepository.findAll().stream()
        .map(TodoDataService::convert2DTO)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public TodoItem findById(final Integer id) throws ResourceNotFoundException {
    return todoRepository.findById(id)
        .map(TodoDataService::convert2DTO)
        .orElseThrow(() -> new ResourceNotFoundException(String.format(RES_NOT_FOUND_PLACEHOLDER, id)));
  }

  @Override
  @Transactional
  public TodoItem create(final TodoItem item) {
    TodoEntity created = todoRepository.saveAndFlush(convert2Entity(item));
    return convert2DTO(created);
  }

  @Override
  @Transactional
  public TodoItem update(final TodoItem item) throws ResourceNotFoundException {
    TodoEntity updated = todoRepository.findById(item.id())
        .map(e ->
            todoRepository.saveAndFlush(convert2Entity(item)))
        .orElseThrow(() -> new ResourceNotFoundException(String.format(RES_NOT_FOUND_PLACEHOLDER, item.id())));
    return convert2DTO(updated);
  }

  @Override
  @Transactional
  public void delete(final Integer id) throws ResourceNotFoundException {
    Optional<TodoEntity> found = todoRepository.findById(id);
    if (found.isPresent()) {
      todoRepository.deleteById(id);
    } else {
      throw new ResourceNotFoundException(String.format(RES_NOT_FOUND_PLACEHOLDER, id));
    }
  }

  @Override
  @Transactional
  public void deleteAll() {
    todoRepository.deleteAll();
  }

  public static TodoEntity convert2Entity(final TodoItem dto) {
    var entity = new TodoEntity();
    entity.setId(dto.id());
    entity.setName(dto.name());
    entity.setCompleted(dto.completed());
    entity.setCreated(dto.created());
    return entity;
  }

  public static TodoItem convert2DTO(final TodoEntity entity) {
    return new TodoItem(entity.getId(), entity.getName(), entity.getCompleted(), entity.getCreated());
  }
}
