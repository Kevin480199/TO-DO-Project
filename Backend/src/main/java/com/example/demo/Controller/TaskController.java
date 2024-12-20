package com.example.demo.Controller;

import com.example.demo.Models.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/task")
public class TaskController implements TaskService {
    private ArrayList<Task> tasks = new ArrayList<>();
    int counter = 1;

    @GetMapping
    public ArrayList<Task> getAllTasks(){
        return tasks;
    }

    @PostMapping
    public ResponseEntity<Task> addTask(@RequestBody Task task){
        if(task != null){
            if (tasks.isEmpty()){
                counter = 1;
                task.setId(counter);
                tasks.add(task);
            }else {
                for (int i = 0; i < tasks.size(); i++){
                    if (tasks.get(i).getId() == counter) {
                        counter++;
                        task.setId(counter);
                        tasks.add(task);
                        break;
                    }
                }
            }
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
            return ResponseEntity.ok("Task with ID " + id + " has been deleted");
        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task with ID " + id + " Was not found");
        }
    }

}
