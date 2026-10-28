module org.example.tercercasoestudio {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.tercercasoestudio to javafx.fxml;
    exports org.example.tercercasoestudio;
}