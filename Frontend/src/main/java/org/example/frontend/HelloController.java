package org.example.frontend;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HelloController {
    @FXML
    private TextArea areaTextBox;

    @FXML
    private TextField inputID;

    @FXML
    private TextField inputDescription;

    @FXML
    private TextField inputName;

    @FXML
    void addNewTask(ActionEvent event) {
        try {
            String name = inputName.getText();
            String description = inputDescription.getText();
            // Creates an object
            Task myTask = new Task(name, description);
            // Convert object to a json
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(myTask);

            URL url = new URL("http://localhost:8100/api/task");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            // Sends json data
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            }

            String response = readResponse(connection);
            // Check for a successful response
            if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                Task createdTask = mapper.readValue(response, Task.class); // Assuming the server returns the created Task with ID

                areaTextBox.appendText("Task successfully added!\n");
                areaTextBox.appendText("ID: " + createdTask.getId() + "\n");
                areaTextBox.appendText("Name: " + createdTask.getName() + "\n");
                areaTextBox.appendText("Description: " + createdTask.getDescription() + "\n");

            } else {
                areaTextBox.appendText("Error: Unable to add task.\n");
            }
        } catch (Exception e) {
            areaTextBox.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private String readResponse(HttpURLConnection connection) throws IOException {
        BufferedReader reader;
        if(connection.getResponseCode() >= 200 && connection.getResponseCode() < 300){
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }
    @FXML
    void removeTask(ActionEvent event) {
        try{
            int id = Integer.parseInt(inputID.getText());
            URL url = new URL("http://localhost:8100/api/task/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String response = readResponse(connection);
            areaTextBox.appendText(response + "\n");
        }catch (Exception e){
            areaTextBox.setText("Error " + e.getMessage());
        }


    }
    @FXML
    void getAllTasks(ActionEvent event) {
        try{

            try {
                ArrayList<Task> tasks = fetchTasks();
                // Display tasks in TextArea
                displayTasksInTextArea(tasks);
            } catch (Exception e) {
                areaTextBox.setText("Error: " + e.getMessage());
            }

        } catch (Exception e) {
            areaTextBox.setText("Error " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public ArrayList<Task> fetchTasks() throws Exception {
        String urlString = "http://localhost:8100/api/task";  // Your API URL
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Check if the request is successful
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new Exception("Failed to fetch tasks: " + connection.getResponseCode());
        }

        // Read the response
        String response = readResponse(connection);

        // Close the connection after reading the response
        connection.disconnect();

        // Parse the JSON response into a list of Task objects
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, Task.class));
    }
    private void displayTasksInTextArea(ArrayList<Task> tasks) {
        StringBuilder displayText = new StringBuilder();
        for (Task task : tasks) {
            displayText.append("ID: ").append(task.getId()).append("\n")
                    .append("Name: ").append(task.getName()).append("\n")
                    .append("Description: ").append(task.getDescription()).append("\n\n");
        }
        areaTextBox.setText(displayText.toString());
    }
    @FXML
    void replaceTask(ActionEvent event) {
        try{
            int id = Integer.parseInt(inputID.getText());
            String name = inputName.getText();
            String description = inputDescription.getText();
            if (id == 0 || name.isEmpty() || description.isEmpty()) {
                Platform.runLater(() -> areaTextBox.setText("All fields are required."));
                return;
            }
            Task myTask = new Task(name, description);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(myTask);

            URL url = new URL("http://localhost:8100/api/task/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);


            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            }

            String response = readResponse(connection);
            areaTextBox.setText(response + "\n");
        }catch (Exception e){
            areaTextBox.setText("Error " + e.getMessage());
        }
    }
}