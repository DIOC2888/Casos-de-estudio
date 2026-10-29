module org.example.tercercasoestudio {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.sctp;
    requires java.desktop;


    opens org.example.tercercasoestudio to javafx.fxml;
    exports org.example.tercercasoestudio;
}