package org.example.tercercasoestudio;

public class Caja {
    private int cantidadVentas = 0;
    private int totalRecaudado = 0;

    // Registra una venta completa de forma sincronizada
    public synchronized ResumenVenta registrarVenta(
            String vendedor, int monto) {

        cantidadVentas++;
        totalRecaudado += monto;

        System.out.println(
                vendedor + " registró una venta de C$" + monto
                        + " | Ventas: " + cantidadVentas
                        + " | Total: C$" + totalRecaudado
        );

        return new ResumenVenta(cantidadVentas, totalRecaudado);
    }

    public synchronized ResumenVenta obtenerResumen() {
        return new ResumenVenta(cantidadVentas, totalRecaudado);
    }

    public synchronized int getCantidadVentas() {
        return cantidadVentas;
    }

    public synchronized int getTotalRecaudado() {
        return totalRecaudado;
    }

    // Datos de resumen de ventas
    public static class ResumenVenta {
        private final int cantidadVentas;
        private final int totalRecaudado;

        public ResumenVenta(int cantidadVentas, int totalRecaudado) {
            this.cantidadVentas = cantidadVentas;
            this.totalRecaudado = totalRecaudado;
        }

        public int getCantidadVentas() {
            return cantidadVentas;
        }

        public int getTotalRecaudado() {
            return totalRecaudado;
        }
    }

}
