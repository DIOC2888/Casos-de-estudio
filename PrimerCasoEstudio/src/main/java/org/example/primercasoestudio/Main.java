package org.example.primercasoestudio;

import org.example.primercasoestudio.model.Reserva;

public class Main {
    public static void main(String[] args) throws InterruptedException{

        Reserva reserva = new Reserva();

        Runnable agente1 = () -> procesarReserva("Agente 1", reserva);
        Runnable agente2 = () -> procesarReserva("Agente 2", reserva);
        Runnable agente3 = () -> procesarReserva("Agente 3", reserva);


        Thread hilo1 = new Thread(agente1);
        Thread hilo2 = new Thread(agente2);
        Thread hilo3 = new Thread(agente3);



        hilo1.start();
        hilo2.start();
        hilo3.start();

        hilo1.join();
        hilo2.join();
        hilo3.join();


    }
    private static void procesarReserva(String agente , Reserva reserva){
        for (int i = 1 ; i <= 30; i++){
            try {
                Thread.sleep(2000);
                reserva.registrarReserva(agente);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(agente + " fue interrumpido.");
                return;
            }
        }
    }

}
