package coursework.model;

import coursework.model.enums.BookFormat;
import coursework.model.enums.Genre;
import coursework.model.enums.Language;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//You can generate getters and setters, here Lombok lib is used. They are generated for you
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Book extends Publication{

    //Variables should be private and their values retrieved and changed using getters and setters respectively

    private String publisher;
    private String isbn;
    @Enumerated
    private Genre genre;
    private int pageCount;
    @Enumerated
    private Language language;
    private int publicationYear;
    @Enumerated
    private BookFormat format;
    private String summary;



    public Book(String publisher, String isbn, Genre genre, int pageCount, Language language, int publicationYear, BookFormat format, String summary) {
        this.publisher = publisher;
        this.isbn = isbn;
        this.genre = genre;
        this.pageCount = pageCount;
        this.language = language;
        this.publicationYear = publicationYear;
        this.format = format;
        this.summary = summary;
    }

    public Book(String title, String author, String publisher, String isbn, Genre genre, int pageCount, Language language, int publicationYear, BookFormat format, String summary) {
        super(title, author);
        this.publisher = publisher;
        this.isbn = isbn;
        this.genre = genre;
        this.pageCount = pageCount;
        this.language = language;
        this.publicationYear = publicationYear;
        this.format = format;
        this.summary = summary;
    }
    public Book(String isbn, String publisher, String genre, String language,  int publicationYear, String format, String summary) {
        super();
        this.isbn = isbn;
        this.publisher = publisher;
        this.genre = Genre.valueOf(genre.toUpperCase());
        this.language = Language.valueOf(language.toUpperCase());
        this.publicationYear = publicationYear;
        this.format = BookFormat.valueOf(format.toUpperCase());
        this.summary = summary;
    }
}
