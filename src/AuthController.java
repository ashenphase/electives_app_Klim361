import java.util.ArrayList;
import java.util.List;

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
            List<String> userRoles = new ArrayList<>();
            for (String r : user.getRoles()) {
                if (r != null) {
                    userRoles.add(r.trim().toLowerCase());
                }
            }

            // guest удалится, если есть другие роли
            if (userRoles.size() > 1) {
                userRoles.remove("guest");
            }

            if (userRoles.size() == 1) {
                String finalRole = userRoles.get(0);
                view.getLoginButton().getScene().getWindow().hide();

                if ("admin".equals(finalRole)) {
                    Electives electivesModel = new Electives();
                    AdminDashboardView dashboardView = new AdminDashboardView();
                    new AdminDashboardController(electivesModel, dashboardView);
                    dashboardView.show();
                } else if ("teacher".equals(finalRole)) {
                    Electives electivesModel = new Electives();
                    TeacherDashboardView teacherView = new TeacherDashboardView();
                    new TeacherDashboardController(electivesModel, teacherView, user.getUserId());
                    teacherView.show();
                } else if ("guest".equals(finalRole)) {
                    Electives electivesModel = new Electives();
                    GuestDashboardView guestView = new GuestDashboardView();
                    new GuestDashboardController(electivesModel, guestView);
                    guestView.show();
                }
            }
            // если admin и teacher
            else if (userRoles.size() > 1) {
                RoleSelectionView selectionView = new RoleSelectionView();
                new RoleSelectionController(selectionView, userRoles, user.getUserId());
                selectionView.show();
            }
        } else {
            view.setStatus("Неверный логин или пароль");
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
            view.setStatus("Пароль должен включать хотя бы 1 заглавную букву, 1 цифру, 1 спецсимвол и иметь длинну от 8 знаков.");
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