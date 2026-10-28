package org.example.segundocasoestudio.model;

public class Pedido {
    private int contadorPedidos;
    private String estado;


    public synchronized String realizarPedidos(String agente) {
        estado = "Listo";
        if (contadorPedidos < 30){
            estado = "En ejecución";
            contadorPedidos++;

        }

        else {
            estado = "Completado";
        }

        System.out.println(estado);
        return estado;


    }

    public int getContadorPedidos() {
        return contadorPedidos;
    }

    public String getEstado() {
        return estado;
    }
}