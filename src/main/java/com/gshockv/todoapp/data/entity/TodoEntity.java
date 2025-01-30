package com.gshockv.todoapp.data.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity describing item for TODOs list.
 */
@Getter @Setter
@ToString
@NoArgsConstructor
@Entity(name = "todos")
public class TodoEntity
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;

  private Boolean completed;

  private LocalDateTime created = LocalDateTime.now();

  public static TodoEntity create(final String name, final Boolean completed) {
    var entity = new TodoEntity();
    entity.setName(name);
    entity.setCompleted(completed);
    return entity;
  }
}
