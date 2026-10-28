
package org.example.primercasoestudio;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

import org.example.primercasoestudio.model.Reserva;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PrimerCasoEstudioController {

    @FXML
    private Label lblDisponibles;

    @FXML
    private Label lblConfirmadas;

    @FXML
    private Label lblRechazadas;

    @FXML
    private Label lblEstado;

    @FXML
    private TextArea txtLog;

    private boolean ejecutando = false;

    @FXML
    public void iniciarSimulacion(ActionEvent actionEvent) {

        if (ejecutando) return;

        ejecutando = true;

        Button boton = (Button) actionEvent.getSource();
        boton.setDisable(true);

        lblDisponibles.setText("20");
        lblConfirmadas.setText("0");
        lblRechazadas.setText("0");
        lblEstado.setText("Ejecutando...");
        txtLog.clear();

        Reserva reserva = new Reserva();
        //Se utiliza para la impresion de los mensajes de reservas.
        ConcurrentLinkedQueue<String> mensajes =
                new ConcurrentLinkedQueue<>();

        Runnable agente1 = () -> procesarReserva("Agente 1", reserva, mensajes);
        Runnable agente2 = () -> procesarReserva("Agente 2", reserva, mensajes);
        Runnable agente3 = () -> procesarReserva("Agente 3", reserva, mensajes);

        Thread hilo1 = new Thread(agente1);
        Thread hilo2 = new Thread(agente2);
        Thread hilo3 = new Thread(agente3);

        // Timeline se usa par la actualizacion de los labels en el fxml
        Timeline timeline = new Timeline();

        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(100), e -> {

                    String mensaje;

                    while ((mensaje = mensajes.poll()) != null) {
                        txtLog.appendText(mensaje + "\n");
                    }

                    lblDisponibles.setText("" + reserva.getCantDisponibles());
                    lblConfirmadas.setText("" + reserva.getCantReservadas());
                    lblRechazadas.setText("" + reserva.getCantRechazadas());
                    //Esto checkea que todos los hilos sigan activos, sino pues se acaba el proceso.
                    if (!hilo1.isAlive() &&
                            !hilo2.isAlive() &&
                            !hilo3.isAlive()) {

                        timeline.stop();
                        lblEstado.setText("Finalizada");
                        txtLog.appendText("Simulación finalizada\n");

                        ejecutando = false;
                        boton.setDisable(false);
                    }
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);

        hilo1.start();
        hilo2.start();
        hilo3.start();

        timeline.play();
    }

    private void procesarReserva(String agente, Reserva reserva, ConcurrentLinkedQueue<String> mensajes) {

        for (int i = 1; i <= 10; i++) {
            try {
                Thread.sleep(500);

                String mensaje = reserva.registrarReserva(agente);
                mensajes.add(mensaje);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
