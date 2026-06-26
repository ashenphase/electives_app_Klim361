import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HexFormat;

public class Users {
    private final Connection connection;

    public Users() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка хэширования", e);
        }
    }

    public User loginUser(String login, String password) {
        String query = "SELECT u.user_id, u.login, u.e_mail, u.password_hash, r.role_name " +
                "FROM users u " +
                "LEFT JOIN users_roles ur ON u.user_id = ur.user_id " +
                "LEFT JOIN roles r ON ur.role_id = r.role_id " +
                "WHERE u.login = ?";

        User user = null;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, login);

            try (ResultSet rs = pstmt.executeQuery()) {
                String inputHash = hashPassword(password);

                while (rs.next()) {
                    String dbHash = rs.getString("password_hash");

                    if (inputHash.equalsIgnoreCase(dbHash)) {
                        if (user == null) {
                            user = new User(
                                    rs.getInt("user_id"),
                                    rs.getString("login"),
                                    rs.getString("e_mail"),
                                    dbHash
                            );
                        }

                        String roleName = rs.getString("role_name");
                        if (roleName != null) {
                            user.addRole(roleName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (user != null && user.getRoles().isEmpty()) {
            user.addRole("guest");
        }

        return user;
    }

    public boolean registerUser(String login, String email, String password) {
        String insertUser = "INSERT INTO users (login, e_mail, password_hash) VALUES (?, ?, ?)";
        String insertRole = "INSERT INTO users_roles (user_id, role_id) VALUES (?, ?)";

        try {
            connection.setAutoCommit(false);

            int userId = -1;
            try (PreparedStatement pstmt = connection.prepareStatement(insertUser, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, login);
                pstmt.setString(2, email);
                pstmt.setString(3, hashPassword(password));

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) throw new Exception("Не удалось создать пользователя.");

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                    }
                }
            }

            try (PreparedStatement pstmtRole = connection.prepareStatement(insertRole)) {
                pstmtRole.setInt(1, userId);
                pstmtRole.setInt(2, 3);
                pstmtRole.executeUpdate();
            }

            connection.commit();
            connection.setAutoCommit(true);
            System.out.println("Пользователь " + login + " успешно зарегистрирован!");
            return true;

        } catch (Exception e) {
            try { connection.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            System.err.println("Ошибка регистрации: " + e.getMessage());
            return false;
        }
    }

    public boolean validatePasswordComplexity(String password) {
        String regex = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\",./<>?]).{8,}$";
        return password.matches(regex);
    }
}