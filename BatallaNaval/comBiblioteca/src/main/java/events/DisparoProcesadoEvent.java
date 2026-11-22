/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package events;

import dtos.CoordenadaDTO;
import dtos.ResumenFlotaDTO;
import enums.ResultadoDisparo;
import java.util.List;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public record DisparoProcesadoEvent(
        String idJugadorDisparador,
        int fila,
        int columna,
        ResultadoDisparo resultado,
        List<CoordenadaDTO> casillasHundidas,
        ResumenFlotaDTO resumenFlotaRival,
        int puntajeDisparador,
        String mensajeAviso,
        long timestamp,
        boolean esRemoto
) implements DomainEvent {

    public DisparoProcesadoEvent(String idJugadorDisparador, int fila, int columna, ResultadoDisparo resultado,
                                 List<CoordenadaDTO> casillasHundidas, ResumenFlotaDTO resumenFlotaRival,
                                 int puntajeDisparador, String mensajeAviso) {
        this(idJugadorDisparador, fila, columna, resultado, casillasHundidas,
             resumenFlotaRival, puntajeDisparador, mensajeAviso, System.currentTimeMillis(), false);
    }

    @Override
    public DomainEvent comoRemoto() {
        return new DisparoProcesadoEvent(idJugadorDisparador, fila, columna, resultado,
                casillasHundidas, resumenFlotaRival, puntajeDisparador, mensajeAviso, timestamp, true);
    }
}