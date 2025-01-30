package com.gshockv.todoapp.api;

import java.util.List;
import java.util.Objects;

import com.gshockv.todoapp.data.ResourceNotFoundException;
import com.gshockv.todoapp.data.TodoItem;
import com.gshockv.todoapp.service.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/todos")
public class TodoRestController
{
  private final TodoService todoService;

  public TodoRestController(final TodoService todoService) {
    this.todoService = Objects.requireNonNull(todoService);
  }

  @GetMapping
  public List<TodoItem> findAll() {
    return todoService.findAll();
  }

  @GetMapping("/{id}")
  public TodoItem findById(@PathVariable("id") final Integer id) {
    try {
      return todoService.findById(id);
    } catch (ResourceNotFoundException e) {
      log.error(e.getMessage());
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
    }
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TodoItem create(@RequestBody final TodoItem item) {
    return todoService.create(item);
  }

  @PutMapping
  @ResponseStatus(HttpStatus.OK)
  public TodoItem update(@RequestBody final TodoItem item) {
    try {
      return todoService.update(item);
    } catch (ResourceNotFoundException e) {
      log.error(e.getMessage());
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
    }
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable("id") final Integer id) {
    try {
      todoService.delete(id);
    } catch (ResourceNotFoundException e) {
      log.error(e.getMessage());
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
    }
  }

  @DeleteMapping("/deleteAll")
  @ResponseStatus(HttpStatus.OK)
  public void deleteAll() {
    todoService.deleteAll();
  }
}
