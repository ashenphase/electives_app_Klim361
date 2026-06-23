public class GuestDashboardController {
    private final Electives model;
    private final GuestDashboardView view;

    public GuestDashboardController(Electives model, GuestDashboardView view) {
        this.model = model;
        this.view = view;

        // кнопка обновления
        this.view.getTable().setItems(this.model.getTargetList());
        this.view.getRefreshButton().setOnAction(e -> this.model.loadFromDb());

        // отображение распределения часов
        this.view.getTable().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                int courseId = newSelection.getSemesterCourseId();
                String activityData = model.loadActivitiesForCourse(courseId);
                view.getActivityInfoArea().setText(activityData);
            }
        });

        this.model.loadFromDb();
        this.view.getTeachersTable().setItems(model.loadTeachersFromDb());
        this.view.getStudentsTable().setItems(model.loadStudentsFromDb());
    }
}