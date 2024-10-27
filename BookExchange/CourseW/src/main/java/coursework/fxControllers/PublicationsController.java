package coursework.fxControllers;

import coursework.StartGUI;
import coursework.hibenateControllers.GenericHibernate;
import coursework.model.Book;
import coursework.model.Publication;
import coursework.model.enums.BookFormat;
import coursework.model.enums.Genre;
import coursework.model.enums.Language;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class PublicationsController implements Initializable {

    @FXML
    public ListView<Publication> publicationListField;
    @FXML
    public TextField bookTitleField;
    @FXML
    public TextField bookAuthorField;
    @FXML
    public TextField bookPublisherField;
    @FXML
    public TextField bookIsbnField;
    @FXML
    public TextField bookPageCountField;
    @FXML
    public ComboBox<Genre> bookGenreField;
    @FXML
    public ComboBox<Language> bookLanguageField;
    @FXML
    public TextField bookPublicationYearField;
    @FXML
    public ComboBox<BookFormat> bookFormatField;
    @FXML
    public TextArea bookSummaryField;

    @FXML
    public TableView<Publication> publicationTableView;
    @FXML
    public TableColumn<Publication, String> colTitle;
    @FXML
    public TableColumn<Publication,String> colAuthor;
    @FXML
    public TableColumn<Publication,String>  colPublisher;
    @FXML
    public TableColumn<Publication,String>  colIsbn;
    @FXML
    public TableColumn<Publication,Integer>  colYear;

    public Button btnDeletePublication;

    private Publication selectedPublication = null;

    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coursework");

    GenericHibernate hibernate = new GenericHibernate(entityManagerFactory);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(bookGenreField!= null) populateComboBoxes();
        if(publicationTableView != null) {
            fillPublicationTable();
            colTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
            colAuthor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
            colPublisher.setCellValueFactory(cellData -> {
                return new SimpleStringProperty(((Book) cellData.getValue()).getPublisher());
            });

            colIsbn.setCellValueFactory(cellData -> {
                return new SimpleStringProperty(((Book) cellData.getValue()).getIsbn());
            });

            colYear.setCellValueFactory(cellData -> {
                return new SimpleObjectProperty<>(((Book) cellData.getValue()).getPublicationYear());
            });
        }
    }
    @FXML
    private void populateComboBoxes() {
        bookGenreField.setItems(FXCollections.observableArrayList(Genre.values()));
        bookLanguageField.setItems(FXCollections.observableArrayList(Language.values()));
        bookFormatField.setItems(FXCollections.observableArrayList(BookFormat.values()));
    }
    private void showAlert(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    @FXML
    public void fillPublicationTable() {
        publicationTableView.getItems().clear();

        // Fetch all publication records
        List<Publication> publicationList = hibernate.getAllRecords(Publication.class);

        // Add the list to the TableView
        ObservableList<Publication> publicationObservableList = FXCollections.observableArrayList(publicationList);
        publicationTableView.setItems(publicationObservableList);
    }

    @FXML
    public void createBook()
    {
        if (bookTitleField.getText().isEmpty() || bookAuthorField.getText().isEmpty() ||
                bookPublisherField.getText().isEmpty() || bookIsbnField.getText().isEmpty() ||
                bookGenreField.getValue() == null || bookPageCountField.getText().isEmpty() ||
                bookLanguageField.getValue() == null || bookPublicationYearField.getText().isEmpty() ||
                bookFormatField.getValue() == null || bookSummaryField.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Error", null, "Please fill all required fields.");
            return;
        }

        try {
            int pageCount = Integer.parseInt(bookPageCountField.getText());
            int publicationYear = Integer.parseInt(bookPublicationYearField.getText());

            Book newBook = new Book(
                    bookTitleField.getText(),
                    bookAuthorField.getText(),
                    bookPublisherField.getText(),
                    bookIsbnField.getText(),
                    bookGenreField.getValue(),
                    pageCount,
                    bookLanguageField.getValue(),
                    publicationYear,
                    bookFormatField.getValue(),
                    bookSummaryField.getText()
            );
            hibernate.create(newBook);
            showAlert(Alert.AlertType.INFORMATION, "Success", null, "Book has been created successfully.");

            Stage stage = (Stage) bookTitleField.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Page count and publication year must be numeric.");
        }
    }


    @FXML
    private void openCreateNewBookForm() throws IOException
    {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("create_book.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Create New Book");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setOnHidden(event -> fillPublicationTable());
        stage.showAndWait();

    }

    @FXML
    public void deletePublication() {
        selectedPublication = publicationTableView.getSelectionModel().getSelectedItem();

        if (selectedPublication == null) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Please select a publication to delete.");
            return;
        }
        hibernate.delete(Publication.class, selectedPublication.getId());

        publicationTableView.getItems().remove(selectedPublication);

        showAlert(Alert.AlertType.INFORMATION, "Success", null, "Publication deleted successfully.");
    }

    @FXML
    public void updatePublication() throws IOException {

        if(selectedPublication instanceof Book book) {

            book.setTitle(bookTitleField.getText());
            book.setAuthor(bookAuthorField.getText());
            book.setPublisher(bookPublisherField.getText());
            book.setIsbn(bookIsbnField.getText());
            book.setPageCount(Integer.parseInt(bookPageCountField.getText()));
            book.setGenre(bookGenreField.getValue());
            book.setLanguage(bookLanguageField.getValue());
            book.setPublicationYear(Integer.parseInt(bookPublicationYearField.getText()));
            book.setFormat(bookFormatField.getValue());
            book.setSummary(bookSummaryField.getText());
        }

        showAlert(Alert.AlertType.INFORMATION, "Success", null, "Publication updated successfully.");
    }

    public void openUpdateForm(String path) throws IOException {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource(path));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Update");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setOnHidden(event -> fillPublicationTable());
            stage.showAndWait();

    }
    @FXML
    private void openBookUpdateForm() throws IOException {
        selectedPublication = publicationTableView.getSelectionModel().getSelectedItem();
        if (selectedPublication == null || !(selectedPublication instanceof Book)) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Please select a book to update.");
            return;
        }

        Book latestSelectedBook = hibernate.getEntityById(Book.class, selectedPublication.getId());

        FXMLLoader loader = new FXMLLoader(StartGUI.class.getResource("update_book.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());

        UpdateBookController updateBookController = loader.getController();

        updateBookController.setBook(latestSelectedBook, hibernate);

        stage.setTitle("Update Book");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setOnHidden(event -> fillPublicationTable());
        stage.showAndWait();
    }

    @FXML
    private void openMangaUpdateForm() throws IOException {
        openUpdateForm("update_manga.fxml");
    }

    @FXML
    private void openPeriodicalUpdateForm() throws IOException {
        openUpdateForm("update_periodical.fxml");
    }

}
