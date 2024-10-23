package coursework.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Client extends User implements Comparable<Client> {

    private String address;
    //birthDate prideta demonstracijai kaip dirbt su LocalDate
    private LocalDate birthDate;
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Comment> commentList;
    @Transient
    private List<Publication> ownedPublications;
    @Transient
    private List<Publication> borrowedPublications;

    public Client(String login, String password, String name, String surname, String address) {
        super(login, password, name, surname);
        this.address = address;
    }

    public Client(String login, String password, String name, String surname, String address, LocalDate birthDate) {
        super(login, password, name, surname);
        this.address = address;
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return name + " " + surname;
    }

    @Override
    public int compareTo(Client o) {

        return 0;
    }
}
