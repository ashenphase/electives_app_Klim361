import javafx.scene.control.Button;
import java.util.List;

public class RoleSelectionController {
    private final RoleSelectionView view;
    private final List<String> roles;
    private final int userId;

    public RoleSelectionController(RoleSelectionView view, java.util.List<String> roles, int userId) {
        this.view = view;
        this.userId = userId;
        this.roles = roles;

        for (String role : roles) {
            Button btn = view.createRoleButton(role);

            btn.setOnAction(e -> {
                view.close();
                openDashboardForRole(role);
            });
        }
    }

    private void openDashboardForRole(String role) {
        view.close();

        if ("admin".equals(role)) {
            Electives electivesModel = new Electives();
            AdminDashboardView dashboardView = new AdminDashboardView();
            new AdminDashboardController(electivesModel, dashboardView);
            dashboardView.show();

        } else if ("teacher".equals(role)) {
            Electives electivesModel = new Electives();
            TeacherDashboardView teacherView = new TeacherDashboardView();

            new TeacherDashboardController(electivesModel, teacherView, this.userId);
            teacherView.show();

        } else if ("guest".equals(role)) {
            Electives electivesModel = new Electives();
            GuestDashboardView guestView = new GuestDashboardView();
            new GuestDashboardController(electivesModel, guestView);
            guestView.show();
        }
    }
}