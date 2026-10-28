package org.example.segundocasoestudio;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Window;
import javafx.util.Duration;
import org.example.segundocasoestudio.model.Pedido;

import java.util.concurrent.ConcurrentLinkedQueue;

public class PedidosController {

    @FXML
    private Button btnIniciar;

    @FXML
    private Button btnCancelar;

    @FXML
    private Label lblEstado;

    @FXML
    private Label lblContador;

    @FXML
    private ProgressBar prgBarraProgreso;
    private Thread hilo1;
    private Thread hilo2;
    private Thread hilo3;
    private Timeline timeline;
    private boolean errorDetectado;
    private boolean cancelacionSolicitada;
    private boolean ejecutando = false;
    private Pedido pedido;
    private ConcurrentLinkedQueue<String> mensajes;

    @FXML
    public void initialize() {

        lblEstado.setText("Listo");
        lblContador.setText("Pedidos completados: 0 / 30");
        prgBarraProgreso.setProgress(0);



        // Cancelar los trabajadores si se cierra la ventana.
        Platform.runLater(() -> {
            if (btnIniciar.getScene() != null) {
                Window ventana = btnIniciar.getScene().getWindow();

                ventana.setOnCloseRequest(event -> {
                    cancelarAlCerrar();
                });
            }
        });
    }

    @FXML
    private void iniciarProcesamiento(ActionEvent actionEvent) {

        if (ejecutando) {
            return;
        }

        ejecutando = true;
        cancelacionSolicitada = false;
        errorDetectado = false;

        btnIniciar.setDisable(true);
        btnCancelar.setDisable(false);

        prgBarraProgreso.setProgress(0);
        lblEstado.setText("En ejecución");
        lblContador.setText("Pedidos completados: 0 / 30");

        // Crear una instancia nueva para cada ejecución.
        pedido = new Pedido();
        mensajes = new ConcurrentLinkedQueue<>();

        // Crear los tres agentes.
        Runnable agente1 =
                () -> procesarPedido("Agente 1", pedido, mensajes);

        Runnable agente2 =
                () -> procesarPedido("Agente 2", pedido, mensajes);

        Runnable agente3 =
                () -> procesarPedido("Agente 3", pedido, mensajes);

        // Crear los tres hilos.
        hilo1 = new Thread(agente1, "Agente-1");
        hilo2 = new Thread(agente2, "Agente-2");
        hilo3 = new Thread(agente3, "Agente-3");

        // Preparar el Timeline antes de iniciarlo.
        if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline(
                new KeyFrame(Duration.millis(100), e -> {

                    String mensaje;

                    // Mostrar los mensajes recibidos.
                    while ((mensaje = mensajes.poll()) != null) {
                        System.out.println(mensaje);
                    }

                    int completados = pedido.getContadorPedidos();

                    lblContador.setText(
                            "Pedidos completados: "
                                    + completados + " / 30"
                    );

                    prgBarraProgreso.setProgress(
                            completados / 30.0
                    );

                    // Comprobar que los tres hilos terminaron realmente.
                    if (!hilo1.isAlive()
                            && !hilo2.isAlive()
                            && !hilo3.isAlive()) {

                        timeline.stop();

                        if (cancelacionSolicitada) {
                            lblEstado.setText("Cancelando");
                            lblEstado.setText("Cancelado");

                        } else if (errorDetectado) {
                            lblEstado.setText("Error");

                        } else if (completados == 30) {
                            prgBarraProgreso.setProgress(1.0);
                            lblEstado.setText("Completado");

                        } else {
                            lblEstado.setText("Error");
                        }

                        ejecutando = false;
                        btnIniciar.setDisable(false);
                        btnCancelar.setDisable(true);
                    }
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);

        // Cada hilo se inicia una sola vez.
        hilo1.start();
        hilo2.start();
        hilo3.start();

        timeline.play();
    }

    private void procesarPedido(
            String agente,
            Pedido pedido,
            ConcurrentLinkedQueue<String> mensajes) {

        // Cada agente puede procesar hasta 10 pedidos.
        for (int i = 1; i <= 10; i++) {

            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            // No iniciar otra espera si ya se completaron los 30.
            if (pedido.getContadorPedidos() >= 30) {
                return;
            }

            try {
                // Simular el procesamiento durante 200 ms.
                Thread.sleep(200);

                // Si se interrumpió la espera, no contar el pedido.
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }

                String mensaje = pedido.realizarPedidos(agente);

                mensajes.add(mensaje);

            } catch (InterruptedException e) {

                // Registrar que se interrumpió la espera.
                Thread.currentThread().interrupt();
                return;

            } catch (Exception e) {

                errorDetectado = true;

                mensajes.add(
                        agente + ": Error al procesar el pedido: "
                                + e.getMessage()
                );

                return;
            }
        }
    }

    @FXML
    private void cancelarProcesamiento(ActionEvent actionEvent) {

        if (!ejecutando || cancelacionSolicitada) {
            return;
        }

        cancelacionSolicitada = true;

        lblEstado.setText("Cancelando");
        btnCancelar.setDisable(true);

        // Solicitar que los tres agentes se detengan.
        if (hilo1 != null && hilo1.isAlive()) {
            hilo1.interrupt();
        }

        if (hilo2 != null && hilo2.isAlive()) {
            hilo2.interrupt();
        }

        if (hilo3 != null && hilo3.isAlive()) {
            hilo3.interrupt();
        }

        // El Timeline esperará a que los tres terminen.
        // Iniciar permanece deshabilitado durante ese tiempo.
    }

    private void cancelarAlCerrar() {

        cancelacionSolicitada = true;

        if (timeline != null) {
            timeline.stop();
        }

        if (hilo1 != null && hilo1.isAlive()) {
            hilo1.interrupt();
        }

        if (hilo2 != null && hilo2.isAlive()) {
            hilo2.interrupt();
        }

        if (hilo3 != null && hilo3.isAlive()) {
            hilo3.interrupt();
        }
    }

}

