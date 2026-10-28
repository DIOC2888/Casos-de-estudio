package org.example.segundocasoestudio.model;

public class Pedido {
    private int contadorPedidos;
    private String estado;


    public synchronized String realizarPedidos(String agente) {
        if (contadorPedidos >= 30) {
            estado = "Completado";
            return agente + ": " + estado
                    + " (" + contadorPedidos + "/30)";
        }

        estado = "En ejecución";
        contadorPedidos++;

        if (contadorPedidos == 30) {
            estado = "Completado";
        }

        System.out.println(agente + ": " + estado
                + " (" + contadorPedidos + "/30)");

        return agente + ": " + estado
                + " (" + contadorPedidos + "/30)";


    }

    public int getContadorPedidos() {
        return contadorPedidos;
    }

    public String getEstado() {
        return estado;
    }
}