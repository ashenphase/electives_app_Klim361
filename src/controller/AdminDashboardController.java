package controller;

import model.Electives;
import view.AdminDashboardView;

public class AdminDashboardController {
    private final Electives model;
    private final AdminDashboardView view;

    public AdminDashboardController(Electives model, AdminDashboardView view) {
        this.model = model;
        this.view = view;

        this.view.getTable().setItems(this.model.getTargetList());

        this.view.getRefreshButton().setOnAction(e -> this.model.loadFromDb());

        this.model.loadFromDb();
    }
}