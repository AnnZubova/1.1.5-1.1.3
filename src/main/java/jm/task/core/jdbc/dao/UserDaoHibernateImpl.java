package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaQuery;


import java.util.List;

public class UserDaoHibernateImpl implements UserDao {
    private final SessionFactory sessionFactory = Util.getSessionFactory();


    public UserDaoHibernateImpl() {

    }


    @Override
    public void createUsersTable() {

        String mysql = "CREATE TABLE IF NOT EXISTS users (\n" +
                "                  ID SERIAL,\n" +
                "                  NAME VARCHAR(45) NOT NULL,\n" +
                "                  LASTNAME VARCHAR(45) NOT NULL,\n" +
                "    AGE INT NOT NULL)";
        try (Session session = Util.getSessionFactory().getCurrentSession()){
            session.beginTransaction();
            Query query = session.createSQLQuery(mysql).addEntity(User.class);
            query.executeUpdate();
            session.getTransaction().commit();
            System.out.println("Успех создания");
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void dropUsersTable() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.createNativeQuery("DROP TABLE IF EXISTS users").executeUpdate();
            transaction.commit();
            System.out.println("Таблица удалена");
        } catch (HibernateException e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            User user = new User(name, lastName, age);
            session.save(user);
            transaction.commit();
            System.out.println("Пользователь " + name + " добавлен в базу данных");
        } catch (HibernateException e) {
            System.out.println("Ошибка сохранения пользователя");
            e.printStackTrace();
        }

    }


    @Override
    public void removeUserById(long id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.delete(session.get(User.class, id));
            transaction.commit();
            System.out.println("Удален пользователь");
        } catch (HibernateException e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }
    }

    @Override
    public List<User> getAllUsers() {
        Session session = sessionFactory.openSession();
        CriteriaQuery<User> criteriaQuery = session.getCriteriaBuilder().createQuery(User.class);
        criteriaQuery.from(User.class);
        Transaction transaction = session.beginTransaction();
        List<User> userList = session.createQuery(criteriaQuery).getResultList();
        try {
            transaction.commit();
            return userList;
        } catch (HibernateException e) {
            e.printStackTrace();
            transaction.rollback();
        } finally {
            session.close();
        }
        return userList;
    }

    @Override
    public void cleanUsersTable() {

        String psql = "TRUNCATE TABLE users";
        try (Session session = Util.getSessionFactory().getCurrentSession()){
            session.beginTransaction();
            Query query = session.createSQLQuery(psql).addEntity(User.class);
            query.executeUpdate();
            session.getTransaction().commit();
            System.out.println("Успех очищения");
        } catch (HibernateException e) {
            System.out.println("Ошибка очищения таблицы");
            e.printStackTrace();
        }

    }
}
