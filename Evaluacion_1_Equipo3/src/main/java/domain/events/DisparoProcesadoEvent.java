/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain.events;

import domain.model.entity.Jugador;
import domain.model.enums.ResultadoDisparo;
import domain.model.valueObject.ResumenFlota;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public record DisparoProcesadoEvent(
    Jugador jugadorDisparador,
    int fila,
    int columna,
    ResultadoDisparo resultado,
    ResumenFlota resumenFlotaRival,
    String mensajeAviso,
    long timestamp
) implements DomainEvent {
    public DisparoProcesadoEvent(Jugador j, int f, int c, ResultadoDisparo r, ResumenFlota resumen, String msg) {
        this(j, f, c, r, resumen, msg, System.currentTimeMillis());
    }
}
