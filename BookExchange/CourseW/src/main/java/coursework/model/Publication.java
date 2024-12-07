package coursework.model;

import coursework.model.enums.PublicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Publication implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    protected String title;
    protected String author;
    @ManyToOne
    protected Client owner;
    @ManyToOne
    protected Client client;
    @Enumerated
    protected PublicationStatus publicationStatus;
    protected LocalDate requestDate;

    public Publication(String title, String author) {
        this.title = title;
        this.author = author;
    }
}
