package com.gshockv.todoapp.data.repo;

import java.util.List;

import com.gshockv.todoapp.data.entity.TodoEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for {@link TodoRepository}
 */
@DataJpaTest
public class TodoRepositoryTest
{
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private TodoRepository underTest;

  @Test
  public void whenFindAll_thenReturnTodoList() {
    var todos = listOfTestEntities();
    prepareTestEntities(todos);

    var all = underTest.findAll();
    assertThat(all).hasSameSizeAs(todos);
  }

  @Test
  public void whenFindByIdFound_thenReturnTodo() {
    var todos = listOfTestEntities();
    prepareTestEntities(todos);

    var first = underTest.findById(todos.getFirst().getId());
    assertThat(first).isPresent();
    assertEquals(todos.getFirst().getId(), first.get().getId());

    var last = underTest.findById(todos.getLast().getId());
    assertThat(last).isPresent();
    assertEquals(todos.getLast().getId(), last.get().getId());
  }

  @Test
  public void whenFindByIdNotFound_thenReturnEmpty() {
    var todos = listOfTestEntities();
    prepareTestEntities(todos);

    var oneHundred = underTest.findById(100);
    assertThat(oneHundred).isEmpty();
  }

  @Test
  public void whenCreate_thenTodoCreated() {
    var todo = TodoEntity.create("created todo", true);
    var created = underTest.save(todo);

    assertNotNull(created);
    assertEquals(todo, created);
    assertEquals(todo.getName(), created.getName());
    assertTrue(created.getCompleted());
  }

  @Test
  public void whenUpdate_thenTodoUpdated() {
    var todo = TodoEntity.create("created todo", false);
    var created = underTest.save(todo);

    assertEquals(todo, created);

    created.setCompleted(true);
    created.setName("updated todo");
    underTest.save(created);

    var updated = underTest.findById(created.getId());
    assertThat(updated).isPresent();
    assertEquals("updated todo", updated.get().getName());
    assertTrue(updated.get().getCompleted());
    assertEquals(created, updated.get());
  }

  @Test
  public void whenDeleteAll_thenReturnEmpty() {
    var todos = listOfTestEntities();
    prepareTestEntities(todos);

    assertThat(underTest.findAll()).hasSameSizeAs(todos);

    underTest.deleteAll();

    assertThat(underTest.findAll()).isEmpty();
  }

  @Test
  public void whenDeleteById_thenTodoNotFound() {
    var todos = listOfTestEntities();
    prepareTestEntities(todos);

    var first = underTest.findById(todos.getFirst().getId());
    assertThat(first).isPresent();

    underTest.deleteById(todos.getFirst().getId());
    assertThat(underTest.findById(todos.getFirst().getId())).isEmpty();
  }

  private List<TodoEntity> listOfTestEntities() {
    return List.of(
        TodoEntity.create("test todo 1", false),
        TodoEntity.create("test todo 2", false),
        TodoEntity.create("test todo 3", true),
        TodoEntity.create("test todo 4", false),
        TodoEntity.create("test todo 5", true)
    );
  }

  private void prepareTestEntities(final List<TodoEntity> todos) {
      todos.forEach(todo -> entityManager.persistAndFlush(todo));
  }
}
