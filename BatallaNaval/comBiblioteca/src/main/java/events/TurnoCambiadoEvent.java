/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package events;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public record TurnoCambiadoEvent(
        String idNuevoJugadorTurno,
        String mensajeAviso,
        long timestamp,
        boolean esRemoto
) implements DomainEvent {

    public TurnoCambiadoEvent(String idNuevoJugadorTurno, String mensajeAviso) {
        this(idNuevoJugadorTurno, mensajeAviso, System.currentTimeMillis(), false);
    }

    @Override
    public DomainEvent comoRemoto() {
        return new TurnoCambiadoEvent(idNuevoJugadorTurno, mensajeAviso, timestamp, true);
    }
}
