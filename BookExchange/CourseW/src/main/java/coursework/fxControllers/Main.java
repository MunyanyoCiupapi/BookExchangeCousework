package coursework.fxControllers;

import coursework.hibenateControllers.GenericHibernate;
import coursework.model.Admin;
import coursework.model.Client;
import coursework.model.User;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Main implements Initializable {
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
    public RadioButton chkClient;
    @FXML
    public RadioButton chkAdmin;
    @FXML
    public TextField phoneNumField;

    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coursework");

    GenericHibernate hibernate = new GenericHibernate(entityManagerFactory);

    User selectedUser = null;

    public void createNewUser() {
        if(chkClient.isSelected()) {
            Client client = new Client(loginField.getText(), pswField.getText(), nameField.getText(), surnameField.getText(), addressField.getText(), bDate.getValue());
            hibernate.create(client);
        }else{
            Admin admin = new Admin(loginField.getText(), pswField.getText(), nameField.getText(), surnameField.getText(), addressField.getText());
            hibernate.create(admin);
        }

        fillUserList();


    }
    private void fillUserList() {
        userListField.getItems().clear();
        List<User> userList = hibernate.getAllRecords(User.class);
        userListField.getItems().addAll(userList);
    }
    public void initialize(URL location, ResourceBundle resources) {
        fillUserList();
    }

    public void loadUserData(MouseEvent mouseEvent) {
        selectedUser = userListField.getSelectionModel().getSelectedItem();
        User latestUser = hibernate.getEntityById(User.class, selectedUser.getId());

        nameField.setText(latestUser.getName());
        surnameField.setText(latestUser.getSurname());

        if(latestUser instanceof Client) {
            Client client = (Client) latestUser;
            addressField.setText(client.getAddress());
        }else
        {
            Admin admin = (Admin) latestUser;
        }
        disalbeFields();
    }
    public void disalbeFields(){
        if (chkClient.isSelected()) {

            addressField.setDisable(false);
            bDate.setDisable(false);
            phoneNumField.setDisable(true);
        }
        else {
            addressField.setDisable(true);
            bDate.setDisable(true);
            phoneNumField.setDisable(false);
        }
    }
    public void updateUser(){
        User latestUser = hibernate.getEntityById(User.class, selectedUser.getId());
        latestUser.setName(nameField.getText());
        hibernate.update(latestUser);

        fillUserList();
    }

    public void deleteUser(ActionEvent event) {
        hibernate.delete(User.class, selectedUser.getId());
        fillUserList();
    }
}
