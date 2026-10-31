/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain.events;

import domain.model.entity.Jugador;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public record TurnoCambiadoEvent(Jugador nuevoJugadorTurno, String mensajeAviso, long timestamp) implements DomainEvent {

    public TurnoCambiadoEvent(Jugador nuevoJugadorTurno, String mensajeAviso) {
        this(nuevoJugadorTurno, mensajeAviso, System.currentTimeMillis());
    }
}
