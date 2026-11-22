/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package events;

import enums.MotivoFin;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public record PartidaFinalizadaEvent(
        String idGanador,
        MotivoFin motivoFin,
        long timestamp,
        boolean esRemoto
) implements DomainEvent {

    public PartidaFinalizadaEvent(String idGanador, MotivoFin motivoFin) {
        this(idGanador, motivoFin, System.currentTimeMillis(), false);
    }

    @Override
    public DomainEvent comoRemoto() {
        return new PartidaFinalizadaEvent(idGanador, motivoFin, timestamp, true);
    }
}