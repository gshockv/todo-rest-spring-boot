package com.gshockv.todoapp.service;

import java.util.List;

import com.gshockv.todoapp.data.ResourceNotFoundException;
import com.gshockv.todoapp.data.TodoItem;

public interface TodoService
{
  List<TodoItem> findAll();
  TodoItem findById(Integer id) throws ResourceNotFoundException ;
  TodoItem create(TodoItem item);
  TodoItem update(TodoItem item) throws ResourceNotFoundException;
  void delete(Integer id) throws ResourceNotFoundException;
  void deleteAll();
}
