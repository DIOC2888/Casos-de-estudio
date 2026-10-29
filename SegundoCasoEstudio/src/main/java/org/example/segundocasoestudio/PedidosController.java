
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

    // Componentes de la interfaz gráfica.
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

    // Cola para mostrar mensajes en la interfaz
    private ConcurrentLinkedQueue<String> mensajes;

    @FXML
    public void initialize() {
        lblEstado.setText("Listo");
        lblContador.setText("Pedidos completados: 0 / 30");
        prgBarraProgreso.setProgress(0);

        // Configurar el cierre de la ventana
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
        // Impedir que se ejecuten dos simulaciones a la vez.
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

        pedido = new Pedido();
        mensajes = new ConcurrentLinkedQueue<>();

        // Runnable define el trabajo que realizará cada agente.
        Runnable agente1 = () -> procesarPedido("Agente 1", pedido, mensajes);
        Runnable agente2 = () -> procesarPedido("Agente 2", pedido, mensajes);
        Runnable agente3 = () -> procesarPedido("Agente 3", pedido, mensajes);


        hilo1 = new Thread(agente1, "Agente-1");
        hilo2 = new Thread(agente2, "Agente-2");
        hilo3 = new Thread(agente3, "Agente-3");

        if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {

                    String mensaje;
                    while ((mensaje = mensajes.poll()) != null) {
                        System.out.println(mensaje);
                    }

                    int completados = pedido.getContadorPedidos();
                    lblContador.setText("Pedidos completados: " + completados + " / 30"
                    );
                    //El progreso de la barra se divide entre 30.0 para obtener  un valor decimal entre 0 y 1.
                    prgBarraProgreso.setProgress(completados / 30.0);

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

        // Repetir el Timeline hasta detenerlo manualmente.
        timeline.setCycleCount(Timeline.INDEFINITE);


        hilo1.start();
        hilo2.start();
        hilo3.start();

        // Comenzar las actualizaciones de la interfaz.
        timeline.play();
    }

    private void procesarPedido(
            String agente,
            Pedido pedido,
            ConcurrentLinkedQueue<String> mensajes) {

        for (int i = 1; i <= 10; i++) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            if (pedido.getContadorPedidos() >= 30) {
                return;
            }

            try {
                // Cada intento espera 200 milisegundos.
                Thread.sleep(200);

                // Comprobar nuevamente si el hilo
                // fue interrumpido durante el proceso.
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                // Registrar el pedido en el objeto compartido.
                String mensaje = pedido.realizarPedidos(agente);
                // Guardar el resultado en la cola segura.
                mensajes.add(mensaje);

            } catch (InterruptedException e) {
                // Restaurar el estado de interrupción.
                Thread.currentThread().interrupt();
                // Terminar el trabajo de este agente.
                return;

            } catch (Exception e) {
                errorDetectado = true;
                // Registrar el error para mostrarlo
                // posteriormente en la consola.
                mensajes.add(agente + ": Error al procesar el pedido: " + e.getMessage()
                );

                return;
            }
        }
    }

    @FXML
    private void cancelarProcesamiento(ActionEvent actionEvent) {

        // Evitar cancelaciones innecesarias o repetidas.
        if (!ejecutando || cancelacionSolicitada) {
            return;
        }

        // Indicar que se solicitó una cancelación.
        cancelacionSolicitada = true;

        lblEstado.setText("Cancelando");
        btnCancelar.setDisable(true);

        // Solicitar la interrupción del primer agente.
        if (hilo1 != null && hilo1.isAlive()) {
            hilo1.interrupt();
        }

        // Solicitar la interrupción del segundo agente.
        if (hilo2 != null && hilo2.isAlive()) {
            hilo2.interrupt();
        }

        // Solicitar la interrupción del tercer agente.
        if (hilo3 != null && hilo3.isAlive()) {
            hilo3.interrupt();
        }

        // El Timeline seguirá verificando los hilos.
        // El botón iniciar permanece deshabilitado
        // hasta confirmar que los tres finalizaron.
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
