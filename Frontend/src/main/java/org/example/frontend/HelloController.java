package org.example.frontend;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.io.*;
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
    private TextArea addDisplay;

    @FXML
    private TextArea deleteDisplay;

    @FXML
    private TextField replaceID;

    @FXML
    private TextField replaceDescription;

    @FXML
    private TextArea replaceDisplay;

    @FXML
    private TextField replaceName;

    @FXML
    private TabPane tabPane;

    private String dataToSave;

    @FXML
    void addNewTask(ActionEvent event) {
        try {
            String name = inputName.getText();
            String description = inputDescription.getText();
            // Creates an object
            Task<String> myTask = new Task(name, description);
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

                addDisplay.setText("Task successfully added!\n" +
                                    "ID: " + createdTask.getId() + "\n" +
                                    "Name: " + createdTask.getName() + "\n" +
                                    "Description: " + createdTask.getDescription() + "\n");

            } else {
                addDisplay.setText("Error: Unable to add task.\n");
            }
        } catch (Exception e) {
            addDisplay.setText("Error: " + e.getMessage());
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
            deleteDisplay.appendText(response + "\n");
        }catch (Exception e){
            deleteDisplay.setText("Error " + e.getMessage());
        }
    }
    @FXML
    void handlerClickEvent(Event event){

        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();

        // Check which tab is selected and take action accordingly
        if (selectedTab != null) {
            getAllTasks();
            System.out.println("Selected Tab: " + selectedTab.getText());
        }
    }

    @FXML
    void addClear(Event event){

        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();

        // Check which tab is selected and take action accordingly
        if (selectedTab != null) {
            inputName.clear();
            inputDescription.clear();
            addDisplay.clear();
            System.out.println("Selected Tab: " + selectedTab.getText());
        }
    }
    @FXML
    void deleteClear(Event event){

        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();

        // Check which tab is selected and take action accordingly
        if (selectedTab != null) {
            inputID.clear();
            deleteDisplay.clear();
            System.out.println("Selected Tab: " + selectedTab.getText());
        }
    }
    @FXML
    void replaceClear(Event event){

        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();

        // Check which tab is selected and take action accordingly
        if (selectedTab != null) {
            replaceID.clear();
            replaceDescription.clear();
            replaceName.clear();
            replaceDisplay.clear();
            System.out.println("Selected Tab: " + selectedTab.getText());
        }
    }

    void getAllTasks() {
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
            int id = Integer.parseInt(replaceID.getText());
            String name = replaceName.getText();
            String description = replaceDescription.getText();
            if (id == 0 || name.isEmpty() || description.isEmpty()) {
                Platform.runLater(() -> replaceDisplay.setText("All fields are required."));
                return;
            }
            Task<String> myTask = new Task(name, description);

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
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Task createdTask = mapper.readValue(response, Task.class); // Assuming the server returns the created Task with ID

                replaceDisplay.setText("Task successfully changed!\n" +
                        "ID: " + createdTask.getId() + "\n" +
                        "Name: " + createdTask.getName() + "\n" +
                        "Description: " + createdTask.getDescription() + "\n");

            } else {
                replaceDisplay.setText("Error: Unable to add task.\n");
            }
            //replaceDisplay.setText(response + "\n");
        }catch (Exception e){
            replaceDisplay.setText("Error " + e.getMessage());
        }
    }
    public void initialize() {
        // Use Platform.runLater to make sure this runs after the scene has been set
        Platform.runLater(() -> {
            Stage stage = (Stage) areaTextBox.getScene().getWindow();
            stage.show();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    saveDataOnExit();
                }
            });
        });
    }
    private void saveDataOnExit() {
        dataToSave = areaTextBox.getText();  // Get data from the TextField

        // Show a confirmation alert before saving
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save your data before exiting?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    saveData();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Exiting without saving.");
            }
        });
    }
    private void saveData() throws Exception {
        System.out.println("Data to save: " + dataToSave);
        // Fetch all tasks with method fetchTasks
        ArrayList<Task> tasks = fetchTasks();
        // Create an ObjectMapper instance (Jackson's main class for JSON processing)
        ObjectMapper objectMapper = new ObjectMapper();

        // Define the file where data will be saved
        File file = new File("appData.json");
        try {
            // Serialize the ArrayList of Task objects to the file
            objectMapper.writeValue(file, tasks);
            System.out.println("Tasks saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving tasks to JSON.");
        }
    }
    @FXML
    void clearAll(ActionEvent event) {
        try{
            String urlString = "http://localhost:8100/api/task/tasks";  // Your API URL
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            if (connection.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
                throw new Exception("Failed to delete tasks: " + connection.getResponseCode());
            }
            getAllTasks();
            String response = readResponse(connection);

            // Close the connection after reading the response
            connection.disconnect();

        }catch (Exception e){

        }

    }
    @FXML
    void openReadMeFile(ActionEvent event) {
        String fileName = "README.md";

        try {
            //constructor of file class having file as argument
            File file = new File(fileName);
            if(!Desktop.isDesktopSupported())//check if Desktop is supported by Platform or not
            {
                System.out.println("not supported");
                return;
            }
            Desktop desktop = Desktop.getDesktop();
            if(file.exists())         //checks file exists or not
                desktop.open(file);              //opens the specified file
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    @FXML
    void closeApplication(ActionEvent event) {
        saveDataOnExit();
        Platform.exit();
    }
    @FXML
    void textOnly(KeyEvent event) {
        TextField sourceTextField = (TextField) event.getSource();
        String input = sourceTextField.getText();

        // Allow only alphabetic characters (a-z, A-Z)
        if (!input.matches("[a-zA-Z0-9 .]*")) {
            sourceTextField.setText(input.replaceAll("[^a-zA-Z0-9 .]", "")); // Remove non-letter characters
        }
    }
    @FXML
    void digitsOnly(KeyEvent event) {
        TextField sourceTextField = (TextField) event.getSource();
        String input = sourceTextField.getText();

        // Allow only alphabetic characters (a-z, A-Z)
        if (!input.matches("[0-9]*")) {
            sourceTextField.setText(input.replaceAll("[^0-9]", "")); // Remove non-letter characters
        }
    }

}