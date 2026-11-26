/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package events;

import dtos.NaveDTO;
import enums.ResultadoColocacion;
import java.util.List;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public record ColocacionActualizadaEvent(
        String idJugador,
        ResultadoColocacion resultado,
        Integer idNave,
        int fila,
        int columna,
        List<NaveDTO> flota,
        long timestamp,
        boolean esRemoto
        ) implements DomainEvent {

    public ColocacionActualizadaEvent(String idJugador, ResultadoColocacion resultado, Integer idNave,
            int fila, int columna, List<NaveDTO> flota) {
        this(idJugador, resultado, idNave, fila, columna,
                flota == null ? List.of() : List.copyOf(flota), System.currentTimeMillis(), false);
    }

    /**
     * Para resultados que no involucran una nave concreta (FLOTA_INCOMPLETA,
     * TABLERO_CONFIRMADO, PARTIDA_INICIADA).
     */
    public ColocacionActualizadaEvent(String idJugador, ResultadoColocacion resultado, List<NaveDTO> flota) {
        this(idJugador, resultado, null, -1, -1, flota);
    }

    @Override
    public DomainEvent comoRemoto() {
        return new ColocacionActualizadaEvent(idJugador, resultado, idNave, fila, columna, flota, timestamp, true);
    }
}
