package org.example.primercasoestudio.model;

public class Reserva {
    private int cantRechazadas = 0;
    private int cantReservadas = 0;
    private int cantDisponibles = 20;



    public synchronized void registrarReserva(String agente){

        if(cantReservadas>=cantDisponibles){
            cantReservadas ++;
            cantDisponibles --;
            System.out.println(agente + " vendio una entrada ");
        }
       else if (cantReservadas > 20) {
                cantRechazadas++;
            System.out.println(agente + " rechazo una entrada por entradas no disponible." );
            }

    }
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
