package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class UserDaoHibernateImpl implements UserDao {
    private final SessionFactory sessionFactory = Util.getSessionFactory();

    public UserDaoHibernateImpl() {
    }

    @Override
    public void createUsersTable() {

        String pstsql = "CREATE TABLE IF NOT EXISTS users (\n" +
                "                  ID SERIAL,\n" +
                "                  NAME VARCHAR(45) NOT NULL,\n" +
                "                  LASTNAME VARCHAR(45) NOT NULL,\n" +
                "    AGE INT NOT NULL)";
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Query query = session.createSQLQuery(pstsql).addEntity(User.class);
            query.executeUpdate();
            session.getTransaction().commit();
            System.out.println("Успех создания");
        } catch (Exception e) {
            e.printStackTrace();
            sessionFactory.getCurrentSession().getTransaction().rollback();
        }
    }

    @Override
    public void dropUsersTable() {
        String pstsql = "DROP TABLE IF EXISTS users";
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Query query = session.createSQLQuery(pstsql).addEntity(User.class);
            query.executeUpdate();
            session.getTransaction().commit();
            System.out.println("Таблица удалена");
        } catch (HibernateException e) {
            e.printStackTrace();
            sessionFactory.getCurrentSession().getTransaction().rollback();
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.save(new User(name, lastName, age));
            session.getTransaction().commit();
            System.out.println("Пользователь " + name + " добавлен в базу данных");
        } catch (HibernateException e) {
            System.out.println("Ошибка сохранения пользователя");
            e.printStackTrace();
            sessionFactory.getCurrentSession().getTransaction().rollback();
        }
    }

    @Override
    public void removeUserById(long id) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.delete(session.get(User.class, id));
            session.getTransaction().commit();;
            System.out.println("Удален пользователь");
        } catch (HibernateException e) {
            e.printStackTrace();
            sessionFactory.getCurrentSession().getTransaction().rollback();
        }
    }

    @Override
    public List<User> getAllUsers() {
        String pstsql = "SELECT * FROM users";
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            List<User> list = session.createSQLQuery(pstsql).addEntity(User.class).list();
            session.getTransaction().commit();
            return list;
        } catch (HibernateException e) {
            e.printStackTrace();
            sessionFactory.getCurrentSession().getTransaction().rollback();
            return null;
        }
    }

    @Override
    public void cleanUsersTable() {
        String psql = "TRUNCATE TABLE users";
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Query query = session.createSQLQuery(psql).addEntity(User.class);
            query.executeUpdate();
            session.getTransaction().commit();
            System.out.println("Успех очищения");
        } catch (HibernateException e) {
            System.out.println("Ошибка очищения таблицы");
            e.printStackTrace();
            sessionFactory.getCurrentSession().getTransaction().rollback();
        }
    }
}
