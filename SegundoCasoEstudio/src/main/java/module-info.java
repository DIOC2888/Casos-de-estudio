module org.example.segundocasoestudio {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.segundocasoestudio to javafx.fxml;
    exports org.example.segundocasoestudio;
}