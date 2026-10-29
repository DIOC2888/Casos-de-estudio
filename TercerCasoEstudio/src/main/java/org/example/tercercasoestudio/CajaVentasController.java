package org.example.tercercasoestudio;

import com.sun.nio.sctp.AbstractNotificationHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.awt.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CajaVentasController {
    @FXML
    private Button btnIniciar;

    @FXML
    public void initalize(){

        // Cancelar los trabajadores si se cierra la ventana.
        Platform.runLater(() -> {
            if (btnIniciar.getScene() != null) {
                Window ventana = btnIniciar.getScene().getWindow();

                ventana.setOnCloseRequest(event -> {


    public void iniciarVentas(ActionEvent actionEvent) {





    }

    public void cancelarVentas(ActionEvent actionEvent) {
    }




    private static void procesarVentas(String vendedor, Caja caja ConcurrentLinkedQueue<String> mensajes) {


        for(int i=1; i <= 5; i++){

            if(Thread.currentThread().isInterrupted()){
                return;
            }

            if(caja.getCantidadVentas()>15){
                return;
            }
            try{
                Thread.sleep(1000);
                caja.registrarVentas(vendedor, 100);
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
                System.out.println(vendedor + " fue interrumpido.");
                return;
            }
        }
    }

}
