
package org.example.primercasoestudio.model;

public class Reserva {

    private int cantRechazadas = 0;
    private int cantReservadas = 0;
    private int cantDisponibles = 20;

    public synchronized String registrarReserva(String agente) {

        String mensaje;

        if (cantDisponibles > 0) {
            cantReservadas++;
            cantDisponibles--;
            mensaje = agente + " vendió una entrada";

        } else {
            cantRechazadas++;
            mensaje = agente + " rechazó una reserva";
        }

        System.out.println(mensaje);
        return mensaje;
    }

    public synchronized int getCantDisponibles() {
        return cantDisponibles;
    }

    public synchronized int getCantReservadas() {
        return cantReservadas;
    }

    public synchronized int getCantRechazadas() {
        return cantRechazadas;
    }
}
