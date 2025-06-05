module com.bomberman {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.bomberman.controller to javafx.fxml;
    opens com.bomberman.model to javafx.fxml;
    opens com.bomberman.view to javafx.fxml;
    exports com.bomberman;
    exports com.bomberman.controller;
    exports com.bomberman.model;
    exports com.bomberman.view;
}