package com.gshockv.todoapp.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gshockv.todoapp.data.TodoItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TodoRestControllerTest
{
  private static final String API_TODOS = "/api/todos";

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @AfterEach
  public void clearTodos() throws Exception {
    mvc.perform(delete(String.format("%s/%s", API_TODOS, "deleteAll")))
        .andExpect(status().isOk());
  }

  @Test
  public void whenGetEmptyTodos_thenEmptyResponse() throws Exception {
    mvc.perform(get(API_TODOS))
        .andExpect(status().isOk());
  }

  @Test
  public void whenGetTodos_theResponseHasList() throws Exception {
    final int expectedCount = 15;

    var preparedList = saveItemsList(expectedCount);

    MvcResult result = mvc.perform(get(API_TODOS)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    String json = result.getResponse().getContentAsString();

    List<TodoItem> fetchedList = objectMapper.readValue(json, new TypeReference<>() { });

    assertThat(preparedList).hasSameSizeAs(fetchedList);

    for (int i = 0; i < expectedCount; i++) {
      TodoItem item = fetchedList.get(i);
      assertThat(item.name()).contains(String.format("todo-item_%d", (i + 1)));
    }
  }

  @Test
  public void whenGetByIdFound_thenReturnItemResponse() throws Exception {
    var todo = saveSingleItem();

    mvc.perform(get(String.format("%s/%d", API_TODOS, todo.id())).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(todo.id())))
        .andExpect(jsonPath("$.name", is(todo.name())));
  }

  @Test
  public void whenGetByIdNotFound_thenResponseNotFound() throws Exception {
    mvc.perform(get(String.format("%s/%d", API_TODOS, -42)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void whenCreateTodo_thenResponseCreated() throws Exception {
    var item = prepareSingleTestItem();
    var itemJson = objectMapper.writeValueAsString(item);

    mvc.perform(post(API_TODOS)
            .contentType(MediaType.APPLICATION_JSON)
            .content(itemJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name", is(item.name())))
        .andExpect(jsonPath("$.completed", is(item.completed())))
        .andExpect(jsonPath("$.created", is(item.created().toString())));
  }

  @Test
  public void whenUpdateTodo_thenResponseIsOk() throws Exception {
    var createdItem = saveSingleItem();

    mvc.perform(get(String.format("%s/%d", API_TODOS, createdItem.id()))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    var preparedForUpdate = new TodoItem(createdItem.id(), "Updated TOD Item", true, createdItem.created());

    var updatedContent = objectMapper.writeValueAsString(preparedForUpdate);

    MvcResult mvcResult = mvc.perform(put(API_TODOS)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatedContent))
        .andExpect(status().isOk())
        .andReturn();

    var response = mvcResult.getResponse().getContentAsString();
    TodoItem updateItem = objectMapper.readValue(response, TodoItem.class);

    assertEquals(preparedForUpdate.id(), updateItem.id());
    assertEquals(preparedForUpdate.name(), updateItem.name());
    assertEquals(preparedForUpdate.completed(), updateItem.completed());
    assertEquals(preparedForUpdate.created(), updateItem.created());
  }

  @Test
  public void whenUpdateFakeTodo_thenResponseIsNotFound() throws Exception {
    var fake = new TodoItem(-25, "Fake Item", false, LocalDateTime.now());
    var content = objectMapper.writeValueAsString(fake);

    mvc.perform(put(API_TODOS)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
        .andExpect(status().isNotFound());
  }

  @Test
  public void whenDeleteById_thenResponseIsOk() throws Exception {
    var toDelete = saveSingleItem();

    mvc.perform(delete(String.format("%s/%d", API_TODOS, toDelete.id())))
        .andExpect(status().isOk());
  }

  @Test
  public void whenDeleteByIdNotFound_thenResponseIsNotFound() throws Exception {
    mvc.perform(delete(String.format("%s/%d", API_TODOS, -42)))
        .andExpect(status().isNotFound());
  }

  private TodoItem prepareSingleTestItem() {
    return new TodoItem(null, "Test Item", false, LocalDateTime.now());
  }

  private List<TodoItem> saveItemsList(final int expectedCount) throws Exception {
    var createdItems = new ArrayList<TodoItem>();
    for (int i = 0; i < expectedCount; i++) {
      var item = new TodoItem(null, String.format("todo-item_%d", (i + 1)), false, LocalDateTime.now());
      createdItems.add(saveSingleItem(item));
    }
    return createdItems;
  }

  private TodoItem saveSingleItem() throws Exception {
    return saveSingleItem(new TodoItem(null, "test item", false, LocalDateTime.now()));
  }

  private TodoItem saveSingleItem(final TodoItem item) throws Exception {
    MvcResult mvcResult = mvc.perform(post(API_TODOS)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(item)))
        .andExpect(status().isCreated()).andReturn();

    String response = mvcResult.getResponse().getContentAsString();
    return objectMapper.readValue(response, TodoItem.class);
  }
}
