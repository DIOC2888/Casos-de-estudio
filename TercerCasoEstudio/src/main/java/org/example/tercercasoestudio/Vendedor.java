package org.example.tercercasoestudio;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntConsumer;

public class Vendedor implements Runnable{

    private static final int MAX_VENTAS = 5;
    private static final int MONTO_VENTA = 100;

    private final String nombre;
    private final Caja caja;
    private final IntConsumer alActualizarProgreso;

    public Vendedor(
            String nombre,
            Caja caja,
            IntConsumer alActualizarProgreso) {

        this.nombre = nombre;
        this.caja = caja;
        this.alActualizarProgreso = alActualizarProgreso;
    }

    @Override
    public void run() {

        for (int venta = 1; venta <= MAX_VENTAS; venta++) {

            // No iniciar otra venta si se solicitó cancelar
            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            try {
                Thread.sleep(
                        ThreadLocalRandom.current()
                                .nextInt(500, 1201)
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            // Comprueba de nuevo antes de confirmar la venta
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            caja.registrarVenta(nombre, MONTO_VENTA);
            alActualizarProgreso.accept(venta);
        }
    }
}
