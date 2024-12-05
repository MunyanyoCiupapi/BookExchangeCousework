package coursework.fxControllers;

import coursework.hibenateControllers.GenericHibernate;
import coursework.model.Book;
import coursework.model.Manga;
import coursework.model.Periodical;
import coursework.model.Publication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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
    private GenericHibernate hibernate;


    public void setPublicationOverview(Publication publication, GenericHibernate hibernate) {
        this.publicationToUpdate = publication;
        this.hibernate = hibernate;

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
}

