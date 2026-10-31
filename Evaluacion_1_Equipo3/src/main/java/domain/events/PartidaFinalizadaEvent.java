/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain.events;

import domain.model.entity.Jugador;
import domain.model.enums.MotivoFin;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public record PartidaFinalizadaEvent(Jugador ganador, MotivoFin motivoFin, long timestamp) implements DomainEvent {

    public PartidaFinalizadaEvent(Jugador ganador, MotivoFin motivoFin) {
        this(ganador, motivoFin, System.currentTimeMillis());
    }
}
