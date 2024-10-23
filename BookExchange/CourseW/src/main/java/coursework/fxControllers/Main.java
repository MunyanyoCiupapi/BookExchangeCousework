package coursework.fxControllers;

import coursework.StartGUI;
import coursework.hibenateControllers.GenericHibernate;
import coursework.model.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;


import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class Main implements Initializable {

    //region UserFields
    @FXML
    public ListView<User> userListField;
    @FXML
    public TextField loginField;
    @FXML
    public TextField nameField;
    @FXML
    public PasswordField pswField;
    @FXML
    public TextField surnameField;
    @FXML
    public TextField addressField;
    @FXML
    public DatePicker bDate;
    @FXML
    public TextField phoneNumField;

    @FXML
    public TextField newLoginField;
    @FXML
    public TextField newNameField;
    @FXML
    public PasswordField newPswField;
    @FXML
    public TextField newSurnameField;
    @FXML
    public TextField newAddressField;
    @FXML
    public TextField newPhoneNumField;
    @FXML
    public DatePicker newBDate;
    @FXML
    public RadioButton newChkClient;
    @FXML
    public RadioButton newChkAdmin;
    //endregion



    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coursework");

    GenericHibernate hibernate = new GenericHibernate(entityManagerFactory);

    User selectedUser = null;

    private ToggleGroup userTypeGroup;
    @FXML
    private TabPane tabPane;


    public void initialize(URL location, ResourceBundle resources) {
        if(userListField != null)fillUserList();

        if(newChkClient != null && newChkAdmin != null) {
            userTypeGroup = new ToggleGroup();
            userTypeGroup.getToggles().addAll(newChkClient, newChkAdmin);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    //region User
    public void createNewUser() {
        if (newLoginField.getText().isEmpty() || newPswField.getText().isEmpty() ||
                newNameField.getText().isEmpty() || newSurnameField.getText().isEmpty() ||
                (newChkClient.isSelected() && (newAddressField.getText().isEmpty() || newBDate.getValue() == null)) ||
                (newChkAdmin.isSelected() && newPhoneNumField.getText().isEmpty())) {

            showAlert(Alert.AlertType.ERROR, "Error", null, "Please fill all required fields.");
            return;
        }

        if(hibernate.isTaken(User.class, "login", newLoginField.getText() )) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "This login is already taken. Please choose a different login.");
            return;
        }

        if (newChkAdmin.isSelected() && !newPhoneNumField.getText().matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Phone number must be numeric.");
            return;
        }
        try {
            if (newChkClient.isSelected()) {
                Client client = new Client(newLoginField.getText(), newPswField.getText(), newNameField.getText(), newSurnameField.getText(), newAddressField.getText(), newBDate.getValue());
                hibernate.create(client);
                showAlert(Alert.AlertType.INFORMATION, "Success", null, "Client user has been created successfully.");
            } else {
                Admin admin = new Admin(newLoginField.getText(), newPswField.getText(), newNameField.getText(), newSurnameField.getText(), newAddressField.getText());
                hibernate.create(admin);
                showAlert(Alert.AlertType.INFORMATION, "Success", null, "Admin user has been created successfully.");
            }
            Stage stage = (Stage) newLoginField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "There was an error while creating the user: " + e.getMessage());
        }
    }

    private void fillUserList() {

        userListField.getItems().clear();

        List<User> userList = hibernate.getAllRecords(User.class);
        userListField.getItems().addAll(userList);
    }

    public void loadUserData(MouseEvent mouseEvent) {
        selectedUser = userListField.getSelectionModel().getSelectedItem();
        User latestUser = hibernate.getEntityById(User.class, selectedUser.getId());

        nameField.setText(latestUser.getName());
        surnameField.setText(latestUser.getSurname());

        if(latestUser instanceof Client) {
            Client client = (Client) latestUser;
            addressField.setDisable(false);
            bDate.setDisable(false);
            phoneNumField.setDisable(true);
            addressField.setText(client.getAddress());
            bDate.setValue(client.getBirthDate());
        }else
        {
            Admin admin = (Admin) latestUser;
            addressField.setDisable(true);
            bDate.setDisable(true);
            phoneNumField.setDisable(false);
            phoneNumField.setText(admin.getPhoneNum());
        }
    }

    public void disalbeFields() {
        if (newChkClient.isSelected()) {
            newAddressField.setDisable(false);
            newBDate.setDisable(false);
            newPhoneNumField.setDisable(true);

            newPhoneNumField.clear();
        } else if (newChkAdmin.isSelected()) {
            newAddressField.setDisable(true);
            newBDate.setDisable(true);
            newPhoneNumField.setDisable(false);

            newAddressField.clear();
            newBDate.setValue(null);
        }
    }


    public void updateUser(){
        User latestUser = hibernate.getEntityById(User.class, selectedUser.getId());
        if (latestUser instanceof Client) {
            Client client = (Client) latestUser;

            client.setName(nameField.getText());
            client.setSurname(surnameField.getText());
            client.setAddress(addressField.getText());
            client.setBirthDate(bDate.getValue());

            hibernate.update(client);

            showAlert(Alert.AlertType.INFORMATION, "Success", null, "Client user has been updated successfully.");
        } else {
            Admin admin = (Admin) latestUser;
            admin.setName(nameField.getText());
            admin.setSurname(surnameField.getText());
            admin.setPhoneNum(phoneNumField.getText());

            hibernate.update(admin);

            showAlert(Alert.AlertType.INFORMATION, "Success", null, "Admin user has been updated successfully.");
        }
        fillUserList();
    }

    public void deleteUser(ActionEvent event) {
        hibernate.delete(User.class, selectedUser.getId());
        fillUserList();
    }

    @FXML
    private void openCreateNewUserForm() throws IOException
    {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("create_user.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Create New User");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setOnHidden(event -> fillUserList());
        stage.showAndWait();
    }
    //endregion








}
