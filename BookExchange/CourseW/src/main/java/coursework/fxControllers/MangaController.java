package coursework.fxControllers;

import coursework.model.Manga;
import coursework.hibenateControllers.GenericHibernate;
import coursework.model.enums.Demographic;
import coursework.model.enums.Language;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

public class MangaController implements Initializable {

    @FXML
    public TextField mangaTitleField;
    @FXML
    public TextField mangaAuthorField;
    @FXML
    public TextField mangaIllustratorField;
    @FXML
    public ComboBox<Demographic> mangaDemographicCmbx;
    @FXML
    public TextField mangaVolumeNumberField;
    @FXML
    public ComboBox<Language> mangaLanguageCmbx;
    @FXML
    public CheckBox mangaIsColorField;

    public Manga mangaToUpdate;

    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coursework");
    GenericHibernate hibernate = new GenericHibernate(entityManagerFactory);



    @Override
    public void initialize(URL location, ResourceBundle resources) {
            mangaDemographicCmbx.setItems(FXCollections.observableArrayList(Demographic.values()));
            mangaLanguageCmbx.setItems(FXCollections.observableArrayList(Language.values()));
        populateFields();
    }


    public void populateFields() {
        if (mangaToUpdate != null) {
            mangaTitleField.setText(mangaToUpdate.getTitle());
            mangaAuthorField.setText(mangaToUpdate.getAuthor());
            mangaIllustratorField.setText(mangaToUpdate.getIllustrator());
            mangaLanguageCmbx.setValue(Language.valueOf(mangaToUpdate.getOriginalLanguage()));
            mangaVolumeNumberField.setText(String.valueOf(mangaToUpdate.getVolumeNumber()));
            mangaDemographicCmbx.setValue(mangaToUpdate.getDemographic());
            mangaIsColorField.setSelected(mangaToUpdate.isColor());
        }
    }

    @FXML
    public void createManga() {
        if (mangaTitleField.getText().isEmpty() || mangaAuthorField.getText().isEmpty() ||
                mangaIllustratorField.getText().isEmpty() || mangaVolumeNumberField.getText().isEmpty() ||
                mangaDemographicCmbx.getValue() == null || mangaLanguageCmbx.getValue() == null) {

            showAlert(Alert.AlertType.ERROR, "Error", null, "Please fill all required fields.");
            return;
        }

        try {
            int volumeNumber = Integer.parseInt(mangaVolumeNumberField.getText());


            Manga newManga = new Manga(
                    mangaTitleField.getText(),
                    mangaAuthorField.getText(),
                    mangaIllustratorField.getText(),
                    mangaLanguageCmbx.getValue().toString(),
                    volumeNumber,
                    mangaDemographicCmbx.getValue(),
                    mangaIsColorField.isSelected()
            );

            hibernate.create(newManga);


            showAlert(Alert.AlertType.INFORMATION, "Success", null, "Manga has been created successfully.");

            Stage stage = (Stage) mangaTitleField.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Volume number must be numeric.");
        }
    }


    @FXML
    public void saveChanges() {
        if (mangaToUpdate != null) {
            mangaToUpdate.setTitle(mangaTitleField.getText());
            mangaToUpdate.setAuthor(mangaAuthorField.getText());
            mangaToUpdate.setIllustrator(mangaIllustratorField.getText());
            mangaToUpdate.setOriginalLanguage(String.valueOf(mangaLanguageCmbx.getValue()));
            mangaToUpdate.setVolumeNumber(Integer.parseInt(mangaVolumeNumberField.getText()));
            mangaToUpdate.setDemographic(mangaDemographicCmbx.getValue());
            mangaToUpdate.setColor(mangaIsColorField.isSelected());

            hibernate.update(mangaToUpdate);

            Stage stage = (Stage) mangaTitleField.getScene().getWindow();
            stage.close();
        }
    }

    public void showAlert(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    @FXML
    public void closeForm() {
        Stage stage = (Stage) mangaTitleField.getScene().getWindow();
        stage.close();
    }
}
