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

            Task myTask = new Task(name, description);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(myTask);

            URL url = new URL("http://localhost:8100/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            }

            String response = readResponse(connection);
            areaTextBox.setText(response + "\n");
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
            URL url = new URL("http://localhost:8100/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String response = readResponse(connection);
            areaTextBox.setText(response + "\n");
        }catch (Exception e){
            areaTextBox.setText("Error " + e.getMessage());
        }


    }
    @FXML
    void getAllTasks(ActionEvent event) {
        try{
            URL url = new URL("http://localhost:8100");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            String response = readResponse(connection);
            areaTextBox.setText(response);

        } catch (Exception e) {
            areaTextBox.setText("Error " + e.getMessage());
            throw new RuntimeException(e);
        }
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

            URL url = new URL("http://localhost:8100/" + id);
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