package coursework.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BookExchange implements Serializable {
    private List<User> users;
    private List<Publication> publications;

    public BookExchange() {
        this.users = new ArrayList<>();
        this.publications = new ArrayList<>();
    }
}
