package org.example.tercercasoestudio;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CajaVentasController {

    private static final int TOTAL_VENDEDORES = 3;
    private static final int VENTAS_POR_VENDEDOR = 5;
    private static final int MONTO_VENTA = 100;

    @FXML private Button btnIniciar;
    @FXML private Button btnCancelar;

    @FXML private ProgressBar barraVendedor1;
    @FXML private ProgressBar barraVendedor2;
    @FXML private ProgressBar barraVendedor3;

    @FXML private Label lblVendedor1;
    @FXML private Label lblVendedor2;
    @FXML private Label lblVendedor3;

    @FXML private Label lblTotalVentas;
    @FXML private Label lblRecaudacion;
    @FXML private Label lblEstado;

    @FXML private TextArea txtRegistro;

    private Caja caja;
    private ExecutorService ejecutor;
    private Timeline monitor;

    private final AtomicInteger[] progresos = {
            new AtomicInteger(0),
            new AtomicInteger(0),
            new AtomicInteger(0)
    };

    private boolean ejecutando = false;
    private boolean cancelando = false;
    private boolean cerrando = false;
    private long identificadorSimulacion = 0;

    @FXML
    public void initialize() {
        restablecerInterfaz();

        // Permite cerrar la aplicación de forma segura
        Platform.runLater(() -> {
            if (txtRegistro.getScene() == null) {
                return;
            }

            Window ventana = txtRegistro.getScene().getWindow();

            ventana.addEventHandler(
                    WindowEvent.WINDOW_CLOSE_REQUEST,
                    evento -> cerrarAplicacion()
            );
        });
    }

    @FXML
    public void iniciarVentas(ActionEvent event) {

        // No iniciar otra simulación si ya hay una en curso o se está cancelando
        if (ejecutando || cancelando || cerrando) {
            return;
        }

        identificadorSimulacion++;

        final long idActual = identificadorSimulacion;

        detenerMonitor();

        caja = new Caja();

        for (AtomicInteger progreso : progresos) {
            progreso.set(0);
        }

        actualizarProgresos();
        actualizarResumen(caja.obtenerResumen());

        txtRegistro.clear();
        txtRegistro.appendText(
                "Nueva simulación iniciada.\n"
        );

        ejecutando = true;
        cancelando = false;

        btnIniciar.setDisable(true);
        btnCancelar.setDisable(false);
        lblEstado.setText("En ejecución");

        // Hilos independientes y con nombres identificables.
        ThreadFactory fabrica = new ThreadFactory() {
            private final AtomicInteger numero = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable tarea) {
                Thread hilo = new Thread(
                        tarea, "Vendedor-" + numero.getAndIncrement()
                );

                // Salvaguarda adicional al cerrar la aplicación.
                hilo.setDaemon(true);

                return hilo;
            }
        };

        ejecutor = Executors.newFixedThreadPool(
                TOTAL_VENDEDORES, fabrica
        );

        for (int i = 0; i < TOTAL_VENDEDORES; i++) {

            final int indice = i;
            final String nombre = "Vendedor " + (i + 1);

            Vendedor vendedor = new Vendedor(
                    nombre,
                    caja,
                    ventasCompletadas -> Platform.runLater(() -> {

                        // Ignora actualizaciones de simulaciones anteriores.
                        if (idActual != identificadorSimulacion
                                || cerrando) {
                            return;
                        }

                        progresos[indice].set(ventasCompletadas);
                        actualizarProgresos();

                        Caja.ResumenVenta resumen =
                                caja.obtenerResumen();

                        actualizarResumen(resumen);

                        txtRegistro.appendText(
                                nombre + " confirmó venta "
                                        + ventasCompletadas + " de "
                                        + VENTAS_POR_VENDEDOR + ".\n"
                        );
                    })
            );

            ejecutor.submit(vendedor);
        }

        // El monitor nunca espera bloqueando el hilo gráfico.
        monitor = new Timeline(
                new KeyFrame(
                        Duration.millis(100),
                        e -> comprobarTerminacion(idActual)
                )
        );

        monitor.setCycleCount(Timeline.INDEFINITE);
        monitor.play();
    }

    @FXML
    public void cancelarVentas(ActionEvent event) {

        if (!ejecutando || cancelando || ejecutor == null) {
            return;
        }

        cancelando = true;

        lblEstado.setText("Cancelando vendedores...");
        btnCancelar.setDisable(true);
        btnIniciar.setDisable(true);

        txtRegistro.appendText(
                "Solicitud de cancelación enviada.\n"
        );

        // Solicita interrupción a las tareas y no bloquea JavaFX.
        ejecutor.shutdownNow();

        // El monitor verificará cuándo terminaron realmente
        // los hilos del ejecutor.
    }

    private void comprobarTerminacion(long idActual) {

        if (idActual != identificadorSimulacion
                || ejecutor == null) {
            return;
        }

        if (!ejecutor.isTerminated()) {
            return;
        }

        detenerMonitor();

        Caja.ResumenVenta resumen = caja.obtenerResumen();

        actualizarProgresos();
        actualizarResumen(resumen);

        ejecutando = false;
        cancelando = false;

        if (cerrando) {
            return;
        }

        btnCancelar.setDisable(true);
        btnIniciar.setDisable(false);

        if (resumen.getCantidadVentas()
                == TOTAL_VENDEDORES * VENTAS_POR_VENDEDOR) {

            lblEstado.setText("Completada");

            txtRegistro.appendText(
                    "Simulación completada: 15 ventas y C$1,500.\n"
            );

        } else {
            lblEstado.setText("Cancelada");

            txtRegistro.appendText(
                    "Todos los vendedores terminaron. "
                            + "Ventas confirmadas: "
                            + resumen.getCantidadVentas()
                            + ".\n"
            );
        }

        // Ya terminó el ejecutor anterior.
        ejecutor = null;
    }

    private void actualizarProgresos() {

        ProgressBar[] barras = {
                barraVendedor1,
                barraVendedor2,
                barraVendedor3
        };

        Label[] etiquetas = {
                lblVendedor1,
                lblVendedor2,
                lblVendedor3
        };

        for (int i = 0; i < TOTAL_VENDEDORES; i++) {

            int cantidad = progresos[i].get();

            barras[i].setProgress(
                    (double) cantidad / VENTAS_POR_VENDEDOR
            );

            etiquetas[i].setText(
                    cantidad + " / " + VENTAS_POR_VENDEDOR
            );
        }
    }

    private void actualizarResumen(Caja.ResumenVenta resumen) {

        lblTotalVentas.setText(
                String.valueOf(resumen.getCantidadVentas())
        );

        lblRecaudacion.setText(
                String.format(
                        java.util.Locale.forLanguageTag("es-NI"),
                        "C$ %,d",
                        resumen.getTotalRecaudado()
                )
        );
    }

    private void restablecerInterfaz() {

        btnIniciar.setDisable(false);
        btnCancelar.setDisable(true);

        for (AtomicInteger progreso : progresos) {
            progreso.set(0);
        }

        actualizarProgresos();

        lblTotalVentas.setText("0");
        lblRecaudacion.setText("C$ 0");
        lblEstado.setText("Sin iniciar");
    }

    private void detenerMonitor() {

        if (monitor != null) {
            monitor.stop();
            monitor = null;
        }
    }

    public void cerrarAplicacion() {

        if (cerrando) {
            return;
        }

        cerrando = true;
        identificadorSimulacion++;

        detenerMonitor();

        if (ejecutor != null && !ejecutor.isTerminated()) {
            ejecutor.shutdownNow();
        }
    }
}
