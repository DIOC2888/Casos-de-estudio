package org.example.tercercasoestudio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CajasVentasApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CajasVentasApplication.class.getResource("cajaventas.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 320);
        stage.setTitle("Bienvenido a la caja de ventas");
        stage.setScene(scene);
        stage.show();
    }
}
