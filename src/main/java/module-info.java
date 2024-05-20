module com.example.pingpongp3 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.example.pingpongp3 to javafx.fxml;
    exports com.example.pingpongp3;
}