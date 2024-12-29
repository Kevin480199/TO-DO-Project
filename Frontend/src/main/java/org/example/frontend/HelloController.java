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
            // Takes input from texfield
            String name = inputName.getText();
            // Takes input from textfield
            String description = inputDescription.getText();
            // Creates an object
            Task<String> myTask = new Task(name, description);
            // Convert object to a json
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(myTask);
            // Creates a url from local machnine with endpoint /api/task
            URL url = new URL("http://localhost:8100/api/task");
            // Opens up connection from url with subclass HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Sets the request method to POST
            connection.setRequestMethod("POST");
            // This sets the data begin sent is JSON format
            connection.setRequestProperty("Content-Type", "application/json");
            // Enables data to be sent
            connection.setDoOutput(true);
            // Sends json data
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            }
            // Reads response with this method
            String response = readResponse(connection);
            // Check for a successful response
            if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                Task createdTask = mapper.readValue(response, Task.class); // Assuming the server returns the created Task with ID
                // Sets text area to display following text
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
        // If connection is OK
        if(connection.getResponseCode() >= 200 && connection.getResponseCode() < 300){
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            // Else if connection is error
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }
        // Initilize Stringbuilder to build a String
        StringBuilder response = new StringBuilder();
        // Define a String called line
        String line;
        // Loop each line in reader and append it to stringbuilder
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        // Close reader
        reader.close();
        // Return String
        return response.toString();
    }
    @FXML
    void removeTask(ActionEvent event) {
        try{
            // Take input from textfield
            int id = Integer.parseInt(inputID.getText());
            // Create the URL object for the API endpoint
            URL url = new URL("http://localhost:8100/api/task/" + id);
            // Opens up connection with subclass HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Set request method to DELETE
            connection.setRequestMethod("DELETE");
            // Set send data type to JSON
            connection.setRequestProperty("Content-Type", "application/json");
            // Enables sent data
            connection.setDoOutput(true);
            // Read response with method readResponse
            String response = readResponse(connection);
            // Append text to textarea
            deleteDisplay.appendText(response + "\n");
        }catch (Exception e){
            // Error if an exception occurred
            deleteDisplay.setText("Error " + e.getMessage());
        }
    }
    @FXML
    void handlerClickEvent(Event event){
        // Check which tab in tabPane was selected
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();

        // Check which tab is selected and take action accordingly in system out
        if (selectedTab != null) {
            getAllTasks();
            System.out.println("Selected Tab: " + selectedTab.getText());
        }
    }

    @FXML
    void addClear(Event event){
        // Check which tab was selected in tabPane
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();

        // Check which tab is selected and take action accordingly
        if (selectedTab != null) {
            // Clears following textfields
            inputName.clear();
            inputDescription.clear();
            addDisplay.clear();
            System.out.println("Selected Tab: " + selectedTab.getText());
        }
    }
    @FXML
    void deleteClear(Event event){
        // Check which tab in tabPane was selected
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();

        // Check which tab is selected and take action accordingly
        if (selectedTab != null) {
            // Clears floowing textfields
            inputID.clear();
            deleteDisplay.clear();
            System.out.println("Selected Tab: " + selectedTab.getText());
        }
    }
    @FXML
    void replaceClear(Event event){
        // check which tab was selected
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();

        // Check which tab is selected and take action accordingly
        if (selectedTab != null) {
            // Clear following textfields and textarea
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
                // load all tasks with method fetchTasks
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
        // Defines an url with endpoint in local machine
        String urlString = "http://localhost:8100/api/task";
        // Creates an url with previous string
        URL url = new URL(urlString);
        // Opens up a connection with subclass HttpURLConnection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // Set request type to GET
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
        // Initilize a stringbuilder
        StringBuilder displayText = new StringBuilder();
        // for each task in tasks append to stringbuilder
        for (Task task : tasks) {
            displayText.append("ID: ").append(task.getId()).append("\n")
                    .append("Name: ").append(task.getName()).append("\n")
                    .append("Description: ").append(task.getDescription()).append("\n\n");
        }
        // Display text in textarea
        areaTextBox.setText(displayText.toString());
    }
    @FXML
    void replaceTask(ActionEvent event) {
        try{
            // Take input from textfields
            int id = Integer.parseInt(replaceID.getText());
            String name = replaceName.getText();
            String description = replaceDescription.getText();
            // If name or description is empty display following text
            if (id == 0 || name.isEmpty() || description.isEmpty()) {
                Platform.runLater(() -> replaceDisplay.setText("All fields are required."));
                return;
            }
            // Initiate generic object
            Task<String> myTask = new Task(name, description);
            // Convert into JSON
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(myTask);
            // Initialize url in local machine with endpoint
            URL url = new URL("http://localhost:8100/api/task/" + id);
            // Open up connection with subclass HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Set request type to PUT
            connection.setRequestMethod("PUT");
            // Set data to be sent to JSON
            connection.setRequestProperty("Content-Type", "application/json");
            // Enables data transfer
            connection.setDoOutput(true);
            // Send JSON object
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            }
            // Reads response with method readResponse
            String response = readResponse(connection);
            // If connection is OK do following
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Retrieve created task from mapper
                Task createdTask = mapper.readValue(response, Task.class); // Assuming the server returns the created Task with ID
                // Display following text from the created task
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
        // Wait for response YES or NO
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
            // Define url in local machine with endpoint
            String urlString = "http://localhost:8100/api/task/tasks";  // Your API URL
            // Initialize url with previous string
            URL url = new URL(urlString);
            // Open connection with subclass HttpURLConection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Sets the request to DELETE
            connection.setRequestMethod("DELETE");
            // Check if response is set to 204 NO CONTENT
            if (connection.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
                throw new Exception("Failed to delete tasks: " + connection.getResponseCode());
            }
            // Display all tasks which should be empty
            getAllTasks();
            String response = readResponse(connection);

            // Close the connection after reading the response
            connection.disconnect();

        }catch (Exception e){

        }

    }
    @FXML
    void openReadMeFile(ActionEvent event) {
        // Define path
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
        // Save data from array to file
        saveDataOnExit();
        Platform.exit();
    }
    @FXML
    void textOnly(KeyEvent event) {
        // Check which textfield that triggers event
        TextField sourceTextField = (TextField) event.getSource();
        // Get the text
        String input = sourceTextField.getText();

        // Allow only alphabetic characters, numbers, space and dot
        if (!input.matches("[a-zA-Z0-9 .]*")) {
            //Replace all characters that doesnt match with ""
            sourceTextField.setText(input.replaceAll("[^a-zA-Z0-9 .]", "")); // Remove non-letter characters
        }
    }
    @FXML
    void digitsOnly(KeyEvent event) {
        // check which textfield that triggers event
        TextField sourceTextField = (TextField) event.getSource();
        // Get text from textfield
        String input = sourceTextField.getText();

        // Allow only numbers
        if (!input.matches("[0-9]*")) {
            // Replace all non numbers with ""
            sourceTextField.setText(input.replaceAll("[^0-9]", "")); // Remove non-letter characters
        }
    }

}