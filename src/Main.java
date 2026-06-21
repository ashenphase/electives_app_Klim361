import model.DBConnection;
import model.User;
import model.Users;

public class Main {
    public static void main(String[] args) {
        Users usersModel = new Users();

        String simplePass = "123";
        String complexPass = "Admin123!";

        System.out.println("Проверка пароля '" + simplePass + "': " + usersModel.validatePasswordComplexity(simplePass)); // Должно быть false
        System.out.println("Проверка пароля '" + complexPass + "': " + usersModel.validatePasswordComplexity(complexPass)); // Должно быть true

        System.out.println("\nПопытка авторизации");

        User loggedUser = usersModel.loginUser("Admin", "Admin123!");

        if (loggedUser != null) {
            System.out.println("Добро пожаловать, " + loggedUser.getLogin() + " (Email: " + loggedUser.getEmail() + ")");
        } else {
            System.err.println("Неверный логин или пароль, либо пользователя нет в БД.");
        }
        DBConnection.getInstance().closeConnection();
    }
}
