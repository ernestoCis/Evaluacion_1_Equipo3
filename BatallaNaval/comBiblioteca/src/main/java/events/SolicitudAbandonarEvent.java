/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package events;

/**
 * Clase de solicitud de abandonar para que el cliente la use
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public record SolicitudAbandonarEvent(
        String idJugador,
        long timestamp,
        boolean esRemoto
) implements DomainEvent {

    public SolicitudAbandonarEvent(String idJugador) {
        this(idJugador, System.currentTimeMillis(), false);
    }

    @Override
    public DomainEvent comoRemoto() {
        return new SolicitudAbandonarEvent(idJugador, timestamp, true);
    }
}
