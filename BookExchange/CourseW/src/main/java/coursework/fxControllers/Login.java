package coursework.fxControllers;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Login {

    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coursework");

}
