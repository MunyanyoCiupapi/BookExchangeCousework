package coursework.fxControllers;

import coursework.StartGUI;
import coursework.hibenateControllers.CustomHibernate;
import coursework.hibenateControllers.GenericHibernate;
import coursework.model.*;
import jakarta.persistence.EntityManagerFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class PublicationOverviewController {


    @FXML
    public Text publicationTitle;
    @FXML
    public Text publicationAuthor;


    // Book Section
    @FXML
    private GridPane bookSection;
    @FXML
    private Text bookPublisher;
    @FXML
    private Text bookIsbn;
    @FXML
    private Text bookGenre;
    @FXML
    private Text bookPageCount;
    @FXML
    private Text bookLanguage;
    @FXML
    private Text bookPublicationYear;
    @FXML
    private Text bookFormat;
    @FXML
    private Text bookSummary;

    // Manga Section
    @FXML
    private GridPane mangaSection;
    @FXML
    private Text mangaIllustrator;
    @FXML
    private Text mangaLanguage;
    @FXML
    private Text mangaVolumeNumber;
    @FXML
    private Text mangaDemographic;
    @FXML
    private Text mangaIsColor;

    // Periodical Section
    @FXML
    private GridPane periodicalSection;
    @FXML
    private Text periodicalIssueNumber;
    @FXML
    private Text periodicalPublicationDate;
    @FXML
    private Text periodicalEditor;
    @FXML
    private Text periodicalFrequency;
    @FXML
    private Text periodicalPublisher;


    private Publication publicationToUpdate;
    EntityManagerFactory entityManagerFactory;
    private GenericHibernate hibernate;
    private User currentUser;
    private Client targetUser;



    private void showAlert(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public void setPublicationOverview(Publication publication, GenericHibernate hibernate, User currentUser, Client client) {
        this.publicationToUpdate = publication;
        this.hibernate = hibernate;
        this.currentUser = currentUser;
        this.targetUser = client;
        initializeTitle(publication);

    }


    private void initializeTitle(Publication publication) {

        String type = "";
        if (publication instanceof Book) {
            type = "Book";
        } else if (publication instanceof Manga) {
            type = "Manga";
        } else if (publication instanceof Periodical) {
            type = "Periodical";
        }

        bookSection.setVisible(false);
        mangaSection.setVisible(false);
        periodicalSection.setVisible(false);

        if (publication instanceof Book) {

            this.publicationTitle.setText(type + " " + publication.getTitle());
            this.publicationAuthor.setText("By: " + publication.getAuthor());
            publicationTitle.setStyle("-fx-alignment: center; -fx-font-size: 20px; -fx-font-weight: bold;");
            publicationAuthor.setStyle("-fx-alignment: center; -fx-font-size: 16px;");
            Book book = (Book) publication;
            bookSection.setVisible(true);
            bookPublisher.setText(book.getPublisher());
            bookIsbn.setText(book.getIsbn());
            bookGenre.setText(book.getGenre().toString());
            bookPageCount.setText(String.valueOf(book.getPageCount()));
            bookLanguage.setText(book.getLanguage().toString());
            bookPublicationYear.setText(String.valueOf(book.getPublicationYear()));
            bookFormat.setText(book.getFormat().toString());
            bookSummary.setText(book.getSummary());


        } else if (publication instanceof Manga) {

            this.publicationTitle.setText(type + " " + publication.getTitle());
            this.publicationAuthor.setText("By: " + publication.getAuthor());
            publicationTitle.setStyle("-fx-alignment: center; -fx-font-size: 20px; -fx-font-weight: bold;");
            publicationAuthor.setStyle("-fx-alignment: center; -fx-font-size: 16px;");
            Manga manga = (Manga) publication;
            mangaSection.setVisible(true);
            mangaIllustrator.setText(manga.getIllustrator());
            mangaLanguage.setText(manga.getOriginalLanguage());
            mangaVolumeNumber.setText(String.valueOf(manga.getVolumeNumber()));
            mangaDemographic.setText(manga.getDemographic().toString());
            mangaIsColor.setText(manga.isColor() ? "Yes" : "No");

        } else if (publication instanceof Periodical) {

            this.publicationTitle.setText(type + " " + ((Periodical) publication).getPublisher());
            Periodical periodical = (Periodical) publication;
            periodicalSection.setVisible(true);
            periodicalIssueNumber.setText(String.valueOf(periodical.getIssueNumber()));
            periodicalPublicationDate.setText(periodical.getPublicationDate().toString());
            periodicalEditor.setText(periodical.getEditor());
            periodicalFrequency.setText(periodical.getFrequency().toString());

        }

    }

    public void loadReviewWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("userReview.fxml"));
        Parent parent = fxmlLoader.load();
        UserReview userReview = fxmlLoader.getController();
        userReview.setData(entityManagerFactory, currentUser, targetUser);
        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        stage.setTitle("Book Exchange Test");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}

