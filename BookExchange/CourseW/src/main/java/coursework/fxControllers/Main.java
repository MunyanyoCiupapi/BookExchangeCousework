package coursework.fxControllers;

import coursework.StartGUI;
import coursework.hibenateControllers.CustomHibernate;
import coursework.hibenateControllers.GenericHibernate;
import coursework.model.*;
import coursework.model.enums.BookFormat;
import coursework.model.enums.Genre;
import coursework.model.enums.Language;
import coursework.model.enums.PublicationStatus;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;


import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
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

    @FXML
    public TableColumn columnId;
    @FXML
    public TableColumn columnName;
    @FXML
    public TableColumn columnSurname;
    @FXML
    public TableColumn columnUsername;
    @FXML
    public TableColumn columnPassword;
    //endregion
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


    //region My Books Tab

    @FXML
    public TableView<BookTableParameters> myBookList;
    @FXML
    public TableColumn<BookTableParameters, String> colBookTitle;
    @FXML
    public TableColumn<BookTableParameters, String> colRequestUser;
    @FXML
    public TableColumn colBookStatus;
    @FXML
    public TableColumn<BookTableParameters, String> colRequestDate;
    @FXML
    public TableColumn<BookTableParameters, Integer> collBookId;
    @FXML
    public TableColumn colHistory;
    //endregion

    @FXML
    public Tab myBooksTab;
    @FXML
    public Tab publicationsTab;
    @FXML
    public Tab usersTab;
    @FXML
    public Tab exchangeTab;


    @FXML
    public ListView<Publication> availableBookList;
    @FXML
    public Button leaveReviewButton;
    public TextArea aboutBook;

    private Publication selectedPublication = null;


    EntityManagerFactory entityManagerFactory; //= Persistence.createEntityManagerFactory("coursework");

    CustomHibernate hibernate; //= new GenericHibernate(entityManagerFactory);

    User selectedUser = null;

    private ToggleGroup userTypeGroup;
    @FXML
    private TabPane allTabs;
    User currentUser = null;



    private void showAlert(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public void setData(EntityManagerFactory entityManagerFactory, User user) {
        this.entityManagerFactory = entityManagerFactory;
        this.currentUser = user;
        this.hibernate = new CustomHibernate(entityManagerFactory);

        if(userListField != null) fillUserList();
        if(allTabs != null) enableVisibility();
        if(bookGenreField != null) populateComboBoxes();

    }

    public void loadData() {
        if (usersTab.isSelected()) {
            fillUserList();
        } else if (publicationsTab.isSelected()) {
         populatePublicationsTab();
        } else if (myBooksTab.isSelected()) {
            fillBookList();
        } else if (exchangeTab.isSelected()) {
            availableBookList.getItems().clear();
            availableBookList.getItems().addAll(hibernate.getAvailablePublications(currentUser));
        }
    }

    private void populatePublicationsTab() {
        if (publicationTableView != null) {
            initializeTableColumns();
            fillPublicationTable();
        }

        publicationTableView.setRowFactory(tv -> {
            TableRow<Publication> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    try {
                        openPublicationOverview();
                    } catch (IOException e) {
                        showAlert(Alert.AlertType.ERROR, "Error", null, "Unable to open publication overview.");
                    }
                }
            });
            return row;
        });
    }


    private void enableVisibility() {
        if (currentUser instanceof Client) {
            allTabs.getTabs().remove(publicationsTab);
            allTabs.getTabs().remove(usersTab);
        } else {
            allTabs.getTabs().remove(myBooksTab);
        }
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
                Admin admin = new Admin(newLoginField.getText(), newPswField.getText(), newNameField.getText(), newSurnameField.getText(), newPhoneNumField.getText());
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

    public void loadUserData() {
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
        showAlert(Alert.AlertType.INFORMATION, "Success", null, "User has been deleted successfully.");
        fillUserList();
    }

    @FXML
    private void openCreateNewUserForm() throws IOException
    {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("create_user.fxml"));
        Parent parent = fxmlLoader.load();
        Main main = fxmlLoader.getController();
        main.setData(entityManagerFactory, currentUser);
        Scene scene = new Scene(parent);
        stage.setTitle("Create New User");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setOnHidden(event -> fillUserList());
        stage.showAndWait();
    }
    //endregion


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
            if(currentUser instanceof Client) newBook.setOwner((Client) currentUser);
            newBook.setPublicationStatus(PublicationStatus.AVAILABLE);
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

        periodicalController.setPeriodical(null, hibernate, currentUser);

        stage.setTitle("Create New Periodical");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);

        stage.setOnHidden(event -> fillPublicationTable());
        stage.setOnHidden(event -> fillBookList());

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

        periodicalController.setPeriodical(latestSelectedPeriodical, hibernate, currentUser);

        stage.setTitle("Update Periodical");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);

        stage.setOnHidden(event -> fillPublicationTable());
        stage.setOnHidden(event -> fillBookList());

        stage.showAndWait();
    }


    @FXML
    private void openCreateNewBookForm() throws IOException
    {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("create_book.fxml"));
        Parent parent = fxmlLoader.load();
        Main main = fxmlLoader.getController();
        main.setData(entityManagerFactory, currentUser);
        Scene scene = new Scene(parent);
        stage.setTitle("Create New Book");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setOnHidden(event -> fillPublicationTable());
        stage.setOnHidden(event -> fillBookList());
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
        stage.setOnHidden(event -> fillBookList());
        stage.showAndWait();
    }

    @FXML
    public void openCreateNewMangaForm() throws IOException {
        FXMLLoader loader = new FXMLLoader(StartGUI.class.getResource("create_manga.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());

        MangaController updateManga = loader.getController();
        updateManga.setManga(null, hibernate, currentUser);
        stage.setTitle("Create New Manga");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setOnHidden(event -> fillPublicationTable());
        stage.setOnHidden(event -> fillBookList());
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

        updateManga.setManga(latestSelectedManga, hibernate, currentUser);

        stage.setTitle("Update Manga");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setOnHidden(event -> fillPublicationTable());
        stage.setOnHidden(event -> fillBookList());
        stage.showAndWait();
    }

    @FXML
    public void openPublicationOverview() throws IOException {
        selectedPublication = publicationTableView.getSelectionModel().getSelectedItem();

        if (selectedPublication == null) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Please select a publication to view.");
            return;
        }

        Publication latestSelectedPublication = hibernate.getEntityById(Publication.class, selectedPublication.getId());

        Client targetClient = selectedPublication.getOwner();


        FXMLLoader loader = new FXMLLoader(StartGUI.class.getResource("publication_overview.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());

        PublicationOverviewController publicationOverview = loader.getController();
        publicationOverview.setPublicationOverview(latestSelectedPublication, hibernate, currentUser, targetClient);

        stage.setTitle("Publication Overview");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setOnHidden(event -> fillPublicationTable());
        stage.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if(myBookList!=null)
        {
            availableBookList.setEditable(true);

            collBookId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colBookTitle.setCellValueFactory(new PropertyValueFactory<>("publicationTitle"));
            colRequestUser.setCellValueFactory(new PropertyValueFactory<>("publicationUser"));
            Callback<TableColumn<BookTableParameters, Void>, TableCell<BookTableParameters, Void>> callbackBookStatus = param -> {
                final TableCell<BookTableParameters, Void> cell = new TableCell<>() {

                    private final ChoiceBox<PublicationStatus> bookStatus = new ChoiceBox<>();

                    {
                        bookStatus.getItems().addAll(PublicationStatus.values());
                        bookStatus.setOnAction(event -> {
                            BookTableParameters rowData = getTableRow().getItem();
                            if (rowData != null) {
                                rowData.setPublicationStatus(bookStatus.getValue());

                                Publication publication = hibernate.getEntityById(Publication.class, rowData.getId());
                                publication.setPublicationStatus(bookStatus.getValue());
                                hibernate.update(publication);

                                insertPublicationRecord(publication);
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            BookTableParameters rowData = getTableRow().getItem();
                            bookStatus.setValue(rowData.getPublicationStatus());
                            setGraphic(bookStatus);
                        }
                    }
                };
                return cell;
            };

            colBookStatus.setCellFactory(callbackBookStatus);
        }

    }

    private void insertPublicationRecord(Publication publication) {
        PeriodicRecord periodicRecord = new PeriodicRecord(publication.getClient(), publication, LocalDate.now(), publication.getPublicationStatus());
        hibernate.create(periodicRecord);
    }
    private void fillBookList() {
        myBookList.getItems().clear();
        List<Publication> publications = hibernate.getOwnPublications(currentUser);
        for (Publication p : publications) {
            BookTableParameters bookTableParameters = new BookTableParameters();
            bookTableParameters.setId(p.getId());
            bookTableParameters.setPublicationTitle(p.getTitle());
            if (p.getClient() != null) {
                bookTableParameters.setPublicationUser(p.getClient().getName() + " " + p.getClient().getSurname());
            }
            bookTableParameters.setPublicationStatus(p.getPublicationStatus());

            myBookList.getItems().add(bookTableParameters);
        }
    }

    public void loadReviewWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("userReview.fxml"));
        Parent parent = fxmlLoader.load();
        UserReview userReview = fxmlLoader.getController();
        userReview.setData(entityManagerFactory, currentUser, availableBookList.getSelectionModel().getSelectedItem().getOwner());
        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        stage.setTitle("Book Exchange Test");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }


    public void loadPublicationInfo() {
        Publication publication = availableBookList.getSelectionModel().getSelectedItem();
        Publication publicationFromDb = hibernate.getEntityById(Publication.class, publication.getId());

        if (publicationFromDb instanceof Book book)
            aboutBook.setText("BOOK"+ "\n" +
                    "Title :" + book.getTitle() + "\n"
                    + "Author:" + book.getAuthor() + "\n" + "Year:" + book.getPublicationYear() + "\n"
                    + "Summary: " + book.getSummary());
        else if (publicationFromDb instanceof  Manga manga)
            aboutBook.setText("MANGA"+ "\n" +
                    "Title :" + manga.getTitle() + "\n"
                    + "Author:" + manga.getAuthor() + "\n" +
                    "Demographic:" + manga.getDemographic() + "\n"
                    + "Is color: " + manga.isColor());
         else if (publicationFromDb instanceof Periodical periodical)
             aboutBook.setText("Periodical"+ "\n" +
                     "Publisher :" + periodical.getPublisher() + "\n"
                + "Editor:" + periodical.getEditor() + "\n");


            


    }

    public void reserveBook() {

        Publication publication = availableBookList.getSelectionModel().getSelectedItem();
        Publication publicationFromDb = hibernate.getEntityById(Publication.class, publication.getId());
        publicationFromDb.setPublicationStatus(PublicationStatus.REQUESTED);
        publicationFromDb.setClient((Client) currentUser);
        hibernate.update(publicationFromDb);

        PeriodicRecord periodicRecord = new PeriodicRecord((Client) currentUser, publicationFromDb, LocalDate.now(), PublicationStatus.REQUESTED);
        hibernate.create(periodicRecord);

    }
}
