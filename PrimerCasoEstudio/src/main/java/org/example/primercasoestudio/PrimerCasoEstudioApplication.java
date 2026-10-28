package org.example.primercasoestudio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PrimerCasoEstudioApplication extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(PrimerCasoEstudioApplication.class.getResource("/org/example/primercasoestudio/CasoEstudio1.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Papeleria");
        stage.setScene(scene);
        stage.show();
    }
    }

