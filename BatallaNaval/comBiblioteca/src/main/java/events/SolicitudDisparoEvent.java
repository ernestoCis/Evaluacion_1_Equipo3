/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package events;

/**
 * Clase de solicitud de disparo para que el cliente la use
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public record SolicitudDisparoEvent(
        String idJugador,
        int fila,
        int columna,
        long timestamp,
        boolean esRemoto
) implements DomainEvent {

    public SolicitudDisparoEvent(String idJugador, int fila, int columna) {
        this(idJugador, fila, columna, System.currentTimeMillis(), false);
    }

    @Override
    public DomainEvent comoRemoto() {
        return new SolicitudDisparoEvent(idJugador, fila, columna, timestamp, true);
    }
}
