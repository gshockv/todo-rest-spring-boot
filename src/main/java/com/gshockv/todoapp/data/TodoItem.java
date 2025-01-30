package com.gshockv.todoapp.data;

import java.time.LocalDateTime;

public record TodoItem(Integer id, String name, Boolean completed, LocalDateTime created) {
}
