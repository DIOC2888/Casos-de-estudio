package org.example.primercasoestudio.model;

public class Reserva {
    private int cantRechazadas = 0;
    private int cantReservadas = 0;
    private int cantDisponibles = 0;




    public int getCantDisponibles() {
        return cantDisponibles;
    }

    public int getCantReservadas() {
        return cantReservadas;
    }

    public int getCantRechazadas() {
        return cantRechazadas;
    }
}
