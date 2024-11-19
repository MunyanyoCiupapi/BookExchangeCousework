package coursework.fxControllers;

import coursework.model.Book;
import coursework.hibenateControllers.GenericHibernate;
import coursework.model.Format;
import coursework.model.enums.BookFormat;
import coursework.model.enums.Genre;
import coursework.model.enums.Language;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateBookController implements Initializable {

    @FXML
    public TextField bookTitleField;
    @FXML
    public TextField bookAuthorField;
    @FXML
    public TextField bookPublisherField;

    @FXML
    public TextField bookPublicationYearField;
    @FXML
    public TextField bookIsbnField;
    @FXML
    public TextField bookPageCountField;
    @FXML
    public ComboBox<BookFormat> bookFormatField;
    @FXML
    public ComboBox<Genre> bookGenreField;
    @FXML
    public ComboBox<Language> bookLanguageField;
    @FXML
    public TextArea bookSummaryField;


    public Book bookToUpdate;
    public GenericHibernate hibernate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateComboBoxes();
    }
    public void populateComboBoxes() {
        bookGenreField.setItems(FXCollections.observableArrayList(Genre.values()));
        bookLanguageField.setItems(FXCollections.observableArrayList(Language.values()));
        bookFormatField.setItems(FXCollections.observableArrayList(BookFormat.values()));
    }

    public void setBook(Book book, GenericHibernate hibernate) {
        this.bookToUpdate = book;
        this.hibernate = hibernate;
        populateFields();
        populateComboBoxes();
    }

    public void populateFields() {
        if (bookToUpdate != null) {
            bookTitleField.setText(bookToUpdate.getTitle());
            bookAuthorField.setText(bookToUpdate.getAuthor());
            bookPublisherField.setText(bookToUpdate.getPublisher());
            bookIsbnField.setText(bookToUpdate.getIsbn());
            bookPageCountField.setText(String.valueOf(bookToUpdate.getPageCount()));
            bookGenreField.setValue(bookToUpdate.getGenre());
            bookPublicationYearField.setText(String.valueOf(bookToUpdate.getPublicationYear()));
            bookFormatField.setValue(bookToUpdate.getFormat());
            bookLanguageField.setValue(bookToUpdate.getLanguage());
            bookSummaryField.setText(bookToUpdate.getSummary());
        }
    }

    @FXML
    public void saveChanges() {
        if (bookToUpdate != null) {

            bookToUpdate.setTitle(bookTitleField.getText());
            bookToUpdate.setAuthor(bookAuthorField.getText());
            bookToUpdate.setPublisher(bookPublisherField.getText());
            bookToUpdate.setIsbn(bookIsbnField.getText());
            bookToUpdate.setPublicationYear(Integer.parseInt(bookPublicationYearField.getText()));
            bookToUpdate.setPageCount(Integer.parseInt(bookPageCountField.getText()));
            bookToUpdate.setFormat(BookFormat.valueOf(bookFormatField.getValue().toString()));
            bookToUpdate.setGenre(bookGenreField.getValue());
            bookToUpdate.setLanguage(bookLanguageField.getValue());
            bookToUpdate.setSummary(bookSummaryField.getText());


            hibernate.update(bookToUpdate);

            showAlert(Alert.AlertType.INFORMATION, "Success", null, "Book publication updated successfully.");

            Stage stage = (Stage) bookTitleField.getScene().getWindow();
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

    public void closeForm()
    {
        Stage stage = (Stage) bookTitleField.getScene().getWindow();
        stage.close();
    }

}
