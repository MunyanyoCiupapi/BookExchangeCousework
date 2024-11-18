package coursework.hibenateControllers;

import coursework.model.User;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class CustomHibernate extends GenericHibernate{

    public CustomHibernate(EntityManagerFactory entityManagerFactory){
        super(entityManagerFactory);
    }

    public User getUserByCredentials(String username, String password){

        User user = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery(User.class);
            Root root = query.from(User.class);

            query.select(root).where(cb.and(cb.equal(root.get("login"), username), cb.equal(root.get("password"), password)));
            Query q;

            q = entityManager.createQuery(query);
            user = (User) q.getSingleResult();

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return user;
    }

}