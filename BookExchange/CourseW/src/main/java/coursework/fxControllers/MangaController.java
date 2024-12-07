package coursework.fxControllers;

import coursework.model.Book;
import coursework.model.Client;
import coursework.model.Manga;
import coursework.hibenateControllers.GenericHibernate;
import coursework.model.User;
import coursework.model.enums.Demographic;
import coursework.model.enums.Language;
import coursework.model.enums.PublicationStatus;
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

public class MangaController {

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
    public GenericHibernate hibernate;
    public User currentUser;


    public void setManga(Manga manga, GenericHibernate hibernate, User currentUser) {
        this.mangaToUpdate = manga;
        this.hibernate = hibernate;
        this.currentUser = currentUser;
        populateComboBoxes();
    }

    private void populateComboBoxes() {

        mangaDemographicCmbx.setItems(FXCollections.observableArrayList(Demographic.values()));
        mangaLanguageCmbx.setItems(FXCollections.observableArrayList(Language.values()));
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

            if(currentUser instanceof Client) newManga.setOwner((Client) currentUser);
            newManga.setPublicationStatus(PublicationStatus.AVAILABLE);
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

            showAlert(Alert.AlertType.INFORMATION, "Success", null, "Manga publication updated successfully.");

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
