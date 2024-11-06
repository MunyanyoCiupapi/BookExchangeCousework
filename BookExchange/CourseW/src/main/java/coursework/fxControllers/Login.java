package coursework.fxControllers;

import coursework.StartGUI;
import coursework.hibenateControllers.CustomHibernate;
import coursework.model.User;
import coursework.utils.FxUtils;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class Login {


    public TextField userPass;
    public TextField userLogin;
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coursework");

    CustomHibernate customHibernate = new CustomHibernate(entityManagerFactory);

    public void validateUser() throws IOException {

        User user = customHibernate.getUserByCredentials(userLogin.getText(), userPass.getText());
        if(user != null)
        {
            FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("main.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            //Po sios eilutes pasieksiu controlleri
            Main main = fxmlLoader.getController();
            main.setData(entityManagerFactory, user);

            Stage stage = (Stage) userLogin.getScene().getWindow();
            stage.setTitle("Book Exchange");
            stage.setScene(scene);
            stage.show();
        }else{
            FxUtils.generateAlert(Alert.AlertType.WARNING, "User Info", "Wrong Credentials");
        }
    }

}
