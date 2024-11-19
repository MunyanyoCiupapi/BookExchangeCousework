package coursework.fxControllers;

import coursework.hibenateControllers.GenericHibernate;
import coursework.model.Manga;
import coursework.model.Periodical;
import coursework.model.enums.Frequency;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class PeriodicalController {

    @FXML
    public TextField issueNumberField;
    @FXML
    public TextField editorField;
    @FXML
    public TextField publisherField;
    @FXML
    public DatePicker publicationDatePicker;
    @FXML
    ComboBox<Frequency> frequencyCmbx;


    public Periodical periodicalToUpdate;
    public GenericHibernate hibernate;

    public void setPeriodical(Periodical periodical, GenericHibernate hibernate) {
        this.periodicalToUpdate = periodical;
        this.hibernate = hibernate;
        populateFields();
        populateComboBoxes();
    }

    private void populateComboBoxes() {
        frequencyCmbx.setItems(FXCollections.observableArrayList(Frequency.values()));
    }

    private void populateFields() {
        if (periodicalToUpdate != null) {
            issueNumberField.setText(String.valueOf(periodicalToUpdate.getIssueNumber()));
            editorField.setText(periodicalToUpdate.getEditor());
            publisherField.setText(periodicalToUpdate.getPublisher());
            publicationDatePicker.setValue(periodicalToUpdate.getPublicationDate());
            frequencyCmbx.setValue(periodicalToUpdate.getFrequency());
        }
    }



    @FXML
    public void saveChanges() {
        if (periodicalToUpdate != null) {
            try {
                periodicalToUpdate.setIssueNumber(Integer.parseInt(issueNumberField.getText()));
                periodicalToUpdate.setEditor(editorField.getText());
                periodicalToUpdate.setPublisher(publisherField.getText());
                periodicalToUpdate.setPublicationDate(publicationDatePicker.getValue());
                periodicalToUpdate.setFrequency(frequencyCmbx.getValue());

                hibernate.update(periodicalToUpdate);

                showAlert(Alert.AlertType.INFORMATION, "Success", null, "Periodical updated successfully.");

                Stage stage = (Stage) issueNumberField.getScene().getWindow();
                stage.close();

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Error", null, "Issue number must be numeric.");
            }
        }
    }

    @FXML
    public void createPeriodical() {
        if (issueNumberField.getText().isEmpty() || editorField.getText().isEmpty() ||
                publisherField.getText().isEmpty() || publicationDatePicker.getValue() == null ||
                frequencyCmbx.getValue() == null) {

            showAlert(Alert.AlertType.ERROR, "Error", null, "Please fill all required fields.");
            return;
        }

        try {
            int issueNumber = Integer.parseInt(issueNumberField.getText());
            LocalDate publicationDate = publicationDatePicker.getValue();

            Periodical newPeriodical = new Periodical(
                    issueNumber,
                    publicationDate,
                    editorField.getText(),
                    frequencyCmbx.getValue(),
                    publisherField.getText()
            );

            hibernate.create(newPeriodical);

            showAlert(Alert.AlertType.INFORMATION, "Success", null, "Periodical created successfully.");

            Stage stage = (Stage) issueNumberField.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Issue number must be numeric.");
        }
    }


    private void showAlert(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    @FXML
    public void closeForm() {
        Stage stage = (Stage) issueNumberField.getScene().getWindow();
        stage.close();
    }


}
