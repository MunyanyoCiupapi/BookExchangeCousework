package coursework.model;

import coursework.model.enums.PublicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PeriodicRecord {
    private int id;
    private Client user;
    private Book book;
    private LocalDate transactionDate;
    private LocalDate returnDate;
    private PublicationStatus status;

    public PeriodicRecord(int id, Client user, Book book, LocalDate transactionDate, LocalDate returnDate, PublicationStatus status) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.transactionDate = transactionDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public PeriodicRecord(Client user, Book book, LocalDate transactionDate, LocalDate returnDate, PublicationStatus status) {
        this.user = user;
        this.book = book;
        this.transactionDate = transactionDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public PeriodicRecord(Client client, Publication publication, LocalDate now, PublicationStatus publicationStatus) {
    }
}
