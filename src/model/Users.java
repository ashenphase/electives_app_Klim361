package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Users extends Observable {
    private final Connection connection;
    private final List<User> userList;

    public Users() {
        this.connection = DBConnection.getInstance().getConnection();
        this.userList = new ArrayList<>();
    }
    public void selectAll() {
        String query = "SELECT user_id, login, e_mail, password_hash FROM users";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {

            userList.clear();

            while (result.next()) {
                int id = result.getInt("user_id");
                String login = result.getString("login");
                String email = result.getString("e_mail");
                String hash = result.getString("password_hash");

                userList.add(new User(id, login, email, hash));
            }

            System.out.println("Данные пользователей успешно загружены из БД. Количество: " + userList.size());

        } catch (Exception e) {
            System.err.println("Ошибка при выполнении selectAll в Users: " + e.getMessage());
            e.printStackTrace();
        }
        this.setChanged();
        this.notifyObservers();
    }

    public List<User> getAll() {
        return userList;
    }

    public User loginUser(String login, String password) {
        String query = "SELECT user_id, login, e_mail, password_hash FROM users WHERE login = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, login);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashFromDb = rs.getString("password_hash");
                    if (password.equals(hashFromDb)) {
                        return new User(
                                rs.getInt("user_id"),
                                rs.getString("login"),
                                rs.getString("e_mail"),
                                hashFromDb
                        );
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при авторизации: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean validatePasswordComplexity(String password) {
        String regex = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\",./<>?]).{8,}$";
        return password.matches(regex);
    }
}