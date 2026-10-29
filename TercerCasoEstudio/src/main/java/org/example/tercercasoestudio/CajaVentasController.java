
package org.example.tercercasoestudio;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CajaVentasController {

    @FXML
    private Button btnIniciar, btnCancelar;

    @FXML
    private ProgressBar barraVendedor1, barraVendedor2, barraVendedor3;

    @FXML
    private Label lblVendedor1, lblVendedor2, lblVendedor3;

    @FXML
    private Label lblTotalVentas, lblRecaudacion, lblEstado;

    @FXML
    private TextArea txtRegistro;


    private Caja caja;
    private ConcurrentLinkedQueue<String> mensajes;
    private int[] ventasVendedores;

    private Thread hilo1, hilo2, hilo3;

    private Timeline timeline;

    private boolean ejecutando = false;
    private boolean cancelacionSolicitada = false;
    private boolean cerrarPendiente = false;

    @FXML
    public void initialize() {

        btnCancelar.setDisable(true);
        lblEstado.setText("Listo");


        Platform.runLater(() -> {
            if (btnIniciar.getScene() != null && btnIniciar.getScene().getWindow() != null) {
                btnIniciar.getScene().getWindow().setOnCloseRequest(event -> {
                            if (ejecutando) {
                                event.consume();
                                cerrarPendiente = true;
                                solicitarCancelacion();
                                lblEstado.setText("Cerrando...");
                            }
                        });
            }
        });
    }

    @FXML
    private void iniciarVentas() {

        if (ejecutando) {
            return;
        }

        ejecutando = true;
        cancelacionSolicitada = false;
        cerrarPendiente = false;

        btnIniciar.setDisable(true);
        btnCancelar.setDisable(false);

        caja = new Caja();
        mensajes = new ConcurrentLinkedQueue<>();
        ventasVendedores = new int[3];


        barraVendedor1.setProgress(0);
        barraVendedor2.setProgress(0);
        barraVendedor3.setProgress(0);

        lblVendedor1.setText("0 / 5");
        lblVendedor2.setText("0 / 5");
        lblVendedor3.setText("0 / 5");

        lblTotalVentas.setText("0");
        lblRecaudacion.setText("C$ 0");
        lblEstado.setText("En ejecución");
        txtRegistro.clear();

        // Crear los tres vendedores.
        Runnable vendedor1 = () -> procesarVentas(1);
        Runnable vendedor2 = () -> procesarVentas(2);
        Runnable vendedor3 = () -> procesarVentas(3);

        // Crear un hilo para cada vendedor.
        hilo1 = new Thread(vendedor1, "Vendedor-1");
        hilo2 = new Thread(vendedor2, "Vendedor-2");
        hilo3 = new Thread(vendedor3, "Vendedor-3");

        // Detener un Timeline anterior.
        if (timeline != null) {
            timeline.stop();
        }


        timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
                    Caja.ResumenVenta resumen;
                    // Leer todos los datos de forma protegida.
                    synchronized (caja) {
                        String mensaje;
                        while ((mensaje = mensajes.poll()) != null) {
                            txtRegistro.appendText(mensaje + "\n");
                        }

                        resumen = caja.obtenerResumen();

                        lblVendedor1.setText(ventasVendedores[0] + " / 5");
                        lblVendedor2.setText(ventasVendedores[1] + " / 5");
                        lblVendedor3.setText(ventasVendedores[2] + " / 5");

                        barraVendedor1.setProgress(ventasVendedores[0] / 5.0);
                        barraVendedor2.setProgress(ventasVendedores[1] / 5.0);
                        barraVendedor3.setProgress(ventasVendedores[2] / 5.0);

                        lblTotalVentas.setText("" + resumen.getCantidadVentas());

                        lblRecaudacion.setText("C$ " + resumen.getTotalRecaudado());
                    }

                    if (!hilo1.isAlive()
                            && !hilo2.isAlive()
                            && !hilo3.isAlive()) {
                        timeline.stop();
                        if (cancelacionSolicitada) {
                            lblEstado.setText("Cancelado");
                        } else if (resumen.getCantidadVentas() == 15) {
                            lblEstado.setText("Completado");
                        } else {
                            lblEstado.setText("Error");
                        }

                        ejecutando = false;

                        btnIniciar.setDisable(false);
                        btnCancelar.setDisable(true);

                        if (cerrarPendiente) {
                            btnIniciar.getScene().getWindow().hide();
                        }
                    }
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);


        hilo1.start();
        hilo2.start();
        hilo3.start();

        timeline.play();
    }


    private void procesarVentas(int vendedor) {

        for (int i = 1; i <= 5; i++) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            try {
                Thread.sleep(500);
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                synchronized (caja) {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    Caja.ResumenVenta resumen = caja.registrarVenta("Vendedor " + vendedor, 100);
                    ventasVendedores[vendedor - 1]++;
                    mensajes.add(
                            "Vendedor " + vendedor
                                    + " realizó una venta de C$100"
                                    + " | Venta #" + resumen.getCantidadVentas()
                                    + " | Total C$" + resumen.getTotalRecaudado()
                    );
                }

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    @FXML
    private void cancelarVentas() {
        solicitarCancelacion();
    }

    private void solicitarCancelacion() {

        if (!ejecutando || cancelacionSolicitada) {
            return;
        }

        cancelacionSolicitada = true;

        lblEstado.setText("Cancelando...");
        btnCancelar.setDisable(true);

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
