package coursework.hibenateControllers;

import coursework.model.Admin;
import coursework.model.Book;
import coursework.model.Client;
import coursework.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.ArrayList;
import java.util.List;

public class GenericHibernate {

    EntityManagerFactory entityManagerFactory;
    EntityManager entityManager;

    public GenericHibernate(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }


    public <T> void create(T entity) {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(entity);
            entityManager.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }

    public <T> void update(T entity) {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.merge(entity);
            entityManager.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }

    public <T> List<T> getAllRecords(Class<T> entityClass) {

        List<T> list = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaQuery query = entityManager.getCriteriaBuilder().createQuery();
            query.select(query.from(entityClass));
            Query q = entityManager.createQuery(query);
            list = q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return list;

    }


    public <T> T getEntityById(Class<T> entityClass, int id) {
        T result = null;
        try{
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            result = entityManager.find(entityClass, id);
            entityManager.getTransaction().commit();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (entityManager != null) entityManager.close();
        }
        return result;
    }

    public <T> void delete(Class<T> entityClass, int id) {
        try{
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            T object = entityManager.find(entityClass, id);
            entityManager.remove(object);
            entityManager.getTransaction().commit();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (entityManager != null) entityManager.close();
        }
    }
    public <T> boolean isTaken(Class<T> entityClass, String fieldName, Object fieldValue) {
        boolean isTaken = false;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            Query query = entityManager.createQuery("SELECT COUNT(e) FROM "+entityClass.getSimpleName()+" e WHERE e." + fieldName + " = :fieldValue");
            query.setParameter("fieldValue", fieldValue);
            long count = (Long) query.getSingleResult();
            isTaken = count > 0;
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (entityManager != null) entityManager.close();
        }
        return isTaken;
    }

}
