package com.example.demo.Controller;

import com.example.demo.Models.Task;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

public interface TaskService {
    ArrayList<Task> getAllTasks();
    ResponseEntity<Task> addTask(Task task);
    ResponseEntity<Task> updateTask(int id, Task updateTask);
    ResponseEntity<String> deleteTask(int id);
}
