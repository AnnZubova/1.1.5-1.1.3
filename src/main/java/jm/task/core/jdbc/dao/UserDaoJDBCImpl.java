package jm.task.core.jdbc.dao;


import jm.task.core.jdbc.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static jm.task.core.jdbc.util.Util.getConnection;

public class UserDaoJDBCImpl implements UserDao {
    private final Connection connection = getConnection();



    public UserDaoJDBCImpl() {

    }
    // Создание таблицы для User(ов) – не должно приводить к исключению, если такая таблица уже существует
    public void createUsersTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE users (\n" +
                    "                       id SERIAL PRIMARY KEY,\n" +
                    "                       name VARCHAR,\n" +
                    "                       lastName VARCHAR,\n" +
                    "                       age int2\n" +
                    ");");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
// Удаление таблицы User(ов) – не должно приводить к исключению, если таблицы не существует
    public void dropUsersTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS users");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    // Добавление User в таблицу
    public void saveUser(String name, String lastName, byte age) {
        try (PreparedStatement stat = connection.prepareStatement("INSERT INTO users (name, lastName, age) VALUES (?,?,?)")) {
            stat.setString(1, name);
            stat.setString(2, lastName);
            stat.setByte(3, age);
            stat.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
// Удаление User из таблицы ( по id )
    public void removeUserById(long id) {
        try (PreparedStatement pstm = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
            pstm.setLong(1, id);
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    // Получение всех User(ов) из таблицы
    public List<User> getAllUsers() {

        List<User> users = new ArrayList<>();

        try (ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM users")) {
            while(resultSet.next()) {
                User user = new User(resultSet.getString("name"),
                        resultSet.getString("lastName"), resultSet.getByte("age"));
                user.setId(resultSet.getLong("id"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }
    // Очистка содержания таблицы
    public void cleanUsersTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("TRUNCATE TABLE users");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
