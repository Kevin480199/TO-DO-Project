package com.example.demo.Controller;

import com.example.demo.Models.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class TaskController {
    private ArrayList<Task> tasks = new ArrayList<>();

    @GetMapping
    public ArrayList<Task> getAllBooks(){
        return tasks;
    }

    @PostMapping
    public ResponseEntity<Task> addTask(@RequestBody Task task){
        if(task != null &&  task.getId() >= 1000){
            tasks.add(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(task);
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable int id, @RequestBody Task updateTask){
        for (Task task : tasks) {
            if (task.getId() == id) {
                task.setName(updateTask.getName());
                task.setDescription(updateTask.getDescription());
                return ResponseEntity.ok(task);
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable int id){
        boolean found = false;
        for (int i = 0; i < tasks.size(); i++) {
            if(tasks.get(i).getId() == id){
                tasks.remove(i);
                found = true;
            }
        }
        if (found) {
            return ResponseEntity.ok("Book with ID " + id + " has been deleted");
        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Books with ID " + id + " Was not found");
        }
    }

}
