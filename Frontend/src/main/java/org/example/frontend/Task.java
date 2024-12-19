package org.example.frontend;

public class Task {
    private int id;
    private static int count = 1000;
    private String name;
    private String description;

    public Task(String name, String description) {
        id = count;
        count++;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
