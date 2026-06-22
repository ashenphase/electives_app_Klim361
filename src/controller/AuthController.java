package controller;

import model.User;
import model.Users;
import view.AuthView;

public class AuthController {
    private final Users model;
    private final AuthView view;

    public AuthController(Users model, AuthView view) {
        this.model = model;
        this.view = view;

        this.view.getLoginButton().setOnAction(e -> handleLogin());
        this.view.getRegisterButton().setOnAction(e -> handleRegister());
    }

    private void handleLogin() {
        String login = view.getLogin();
        String password = view.getPassword();

        if (login.isEmpty() || password.isEmpty()) {
            view.setStatus("Заполните логин и пароль!");
            return;
        }

        User user = model.loginUser(login, password);

        if (user != null) {
            view.setStatus("Успех! Перенаправление...");

            if ("Admin".equalsIgnoreCase(user.getRole())) {
                view.getLoginButton().getScene().getWindow().hide();

                model.Electives electivesModel = new model.Electives();
                view.AdminDashboardView dashboardView = new view.AdminDashboardView();
                new controller.AdminDashboardController(electivesModel, dashboardView);

                dashboardView.show();
            } else {
                view.setStatus("Вы вошли как " + user.getRole());
            }
        }
        else {
            view.setStatus("Неверный логин или пароль!");
        }
    }

    private void handleRegister() {
        String login = view.getLogin();
        String email = view.getEmail();
        String password = view.getPassword();

        if (login.isEmpty() || email.isEmpty() || password.isEmpty()) {
            view.setStatus("Для регистрации заполните ВСЕ три поля!");
            return;
        }

        if (!model.validatePasswordComplexity(password)) {
            view.setStatus("Пароль должен включать хотя бы 1 заглавную букву, 1 цифру, спецсимвол и иметь длинну от 8 знаков.");
            return;
        }

        boolean success = model.registerUser(login, email, password);
        if (success) {
            view.setStatus("Регистрация успешна! Теперь вы можете войти.");
        } else {
            view.setStatus("Ошибка регистрации! Возможно, логин уже занят.");
        }
    }
}