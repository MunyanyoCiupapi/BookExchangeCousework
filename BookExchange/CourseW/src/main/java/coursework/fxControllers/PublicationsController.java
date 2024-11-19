package coursework.fxControllers;

import coursework.StartGUI;
import coursework.hibenateControllers.GenericHibernate;
import coursework.model.Book;
import coursework.model.Manga;
import coursework.model.Periodical;
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
    public TableColumn<Publication, String> colType;
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
    public TableColumn<Publication,String>  colYear;

    public Button btnDeletePublication;


    private Publication selectedPublication = null;

    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coursework");

    GenericHibernate hibernate = new GenericHibernate(entityManagerFactory);


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (bookGenreField != null) populateComboBoxes();
        if (publicationTableView != null) {
            initializeTableColumns();
            fillPublicationTable();
        }
    }

    @FXML
    private void initializeTableColumns() {
        colTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        colAuthor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        colPublisher.setCellValueFactory(cellData -> {
            Publication publication = cellData.getValue();
            if (publication instanceof Book) {
                return new SimpleStringProperty(((Book) publication).getPublisher());
            } else if (publication instanceof Manga) {
                return new SimpleStringProperty(""); // If no publisher for Manga
            } else if (publication instanceof Periodical) {
                return new SimpleStringProperty(((Periodical) publication).getPublisher()); // Handle Periodical publisher
            }
            return null;
        });

        colIsbn.setCellValueFactory(cellData -> {
            Publication publication = cellData.getValue();
            if (publication instanceof Book) {
                return new SimpleStringProperty(((Book) publication).getIsbn());
            } else if (publication instanceof Manga) {
                return new SimpleStringProperty("");
            } else if (publication instanceof Periodical) {
                return new SimpleStringProperty("");
            }
            return null;
        });

        colYear.setCellValueFactory(cellData -> {
            Publication publication = cellData.getValue();
            if (publication instanceof Book) {
                return new SimpleObjectProperty<>(((Book) publication).getPublicationYear()).asString();
            } else if (publication instanceof Periodical) {
                return new SimpleObjectProperty<>(((Periodical) publication).getPublicationDate().getYear() + "/" +((Periodical) publication).getPublicationDate().getMonth() + "/" + ((Periodical) publication).getPublicationDate().getDayOfMonth());
            } else {
                return new SimpleObjectProperty<>(null);
            }
        });

        colType.setCellValueFactory(cellData -> {
            Publication publication = cellData.getValue();
            if (publication instanceof Book) {
                return new SimpleStringProperty("Book");
            } else if (publication instanceof Manga) {
                return new SimpleStringProperty("Manga");
            } else if (publication instanceof Periodical) {
                return new SimpleStringProperty("Periodical");
            }
            return null;
        });
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

        List<Publication> publicationList = hibernate.getAllRecords(Publication.class);

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
        selectedPublication = publicationTableView.getSelectionModel().getSelectedItem();

       if(selectedPublication == null) {showAlert(Alert.AlertType.INFORMATION, "Select Publication!", null, "Please select publication from the list!");}
         else {
           if (selectedPublication instanceof Book) {

               openBookUpdateForm();
           }
           else if(selectedPublication instanceof Manga)
           {
               openMangaUpdateForm();
           } else if (selectedPublication instanceof Periodical) {
               openUpdatePeriodicalForm();
           }
       }


    }

    @FXML
    private void openCreateNewPeriodical() throws IOException {
        FXMLLoader loader = new FXMLLoader(StartGUI.class.getResource("create_periodical.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());

        PeriodicalController periodicalController = loader.getController();

        periodicalController.setPeriodical(null, hibernate);

        stage.setTitle("Create New Periodical");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);

        stage.setOnHidden(event -> fillPublicationTable());

        stage.showAndWait();
    }

    @FXML
    private void openUpdatePeriodicalForm() throws IOException {
        selectedPublication = publicationTableView.getSelectionModel().getSelectedItem();

        if (selectedPublication == null || !(selectedPublication instanceof Periodical)) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Please select a periodical to update.");
            return;
        }

        Periodical latestSelectedPeriodical = hibernate.getEntityById(Periodical.class, selectedPublication.getId());

        FXMLLoader loader = new FXMLLoader(StartGUI.class.getResource("update_periodical.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());

        PeriodicalController periodicalController = loader.getController();

        periodicalController.setPeriodical(latestSelectedPeriodical, hibernate);

        stage.setTitle("Update Periodical");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);

        stage.setOnHidden(event -> fillPublicationTable());

        stage.showAndWait();
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
    public void openCreateNewMangaForm() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("create_manga.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Create New Manga");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setOnHidden(event -> fillPublicationTable());
        stage.showAndWait();
    }
    private void openMangaUpdateForm() throws IOException {
        selectedPublication = publicationTableView.getSelectionModel().getSelectedItem();
        if (selectedPublication == null || !(selectedPublication instanceof Manga)) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Please select a manga to update.");
            return;
        }

        Manga latestSelectedManga = hibernate.getEntityById(Manga.class, selectedPublication.getId());

        FXMLLoader loader = new FXMLLoader(StartGUI.class.getResource("update_manga.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());

        MangaController updateManga = loader.getController();

        updateManga.setManga(latestSelectedManga, hibernate);

        stage.setTitle("Update Manga");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setOnHidden(event -> fillPublicationTable());
        stage.showAndWait();
    }



}
