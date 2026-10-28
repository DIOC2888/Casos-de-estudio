module org.example.primercasoestudio {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.primercasoestudio to javafx.fxml;
    exports org.example.primercasoestudio;
}