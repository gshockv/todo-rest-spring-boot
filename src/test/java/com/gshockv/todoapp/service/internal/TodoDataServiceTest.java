package com.gshockv.todoapp.service.internal;

import java.time.LocalDateTime;

import com.gshockv.todoapp.data.TodoItem;
import com.gshockv.todoapp.data.entity.TodoEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodoDataServiceTest
{
  @Test
  public void testConvertEntity2DTO() {
    var entity = new TodoEntity();
    entity.setId(42);
    entity.setName("test entity");
    entity.setCompleted(false);

    var dto = TodoDataService.convert2DTO(entity);

    assertEquals(entity.getId(), dto.id());
    assertEquals(entity.getName(), dto.name());
    assertEquals(entity.getCompleted(), dto.completed());
    assertEquals(entity.getCreated(), dto.created());
  }

  @Test
  public void testConvertDTO2Entity() {
    var dto = new TodoItem(1, "test dto", false, LocalDateTime.now().minusDays(2));

    var entity = TodoDataService.convert2Entity(dto);

    assertEquals(dto.id(), entity.getId());
    assertEquals(dto.name(), entity.getName());
    assertEquals(dto.completed(), entity.getCompleted());
    assertEquals(dto.created(), entity.getCreated());
  }
}
