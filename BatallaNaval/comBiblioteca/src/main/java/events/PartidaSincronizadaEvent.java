/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package events;

import dtos.JugadorDTO;
import dtos.NaveDTO;
import dtos.ResumenFlotaDTO;
import java.util.List;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public record PartidaSincronizadaEvent(
        JugadorDTO jugadorEmisor,
        List<NaveDTO> flotaEmisor,
        ResumenFlotaDTO resumenFlotaEmisor,
        boolean esRespuestaServidor,
        long timestamp,
        boolean esRemoto
) implements DomainEvent {

    public PartidaSincronizadaEvent(JugadorDTO jugadorEmisor, List<NaveDTO> flotaEmisor,
                                    ResumenFlotaDTO resumenFlotaEmisor, boolean esRespuestaServidor) {
        this(jugadorEmisor, flotaEmisor, resumenFlotaEmisor, esRespuestaServidor, System.currentTimeMillis(), false);
    }

    @Override
    public DomainEvent comoRemoto() {
        return new PartidaSincronizadaEvent(jugadorEmisor, flotaEmisor, resumenFlotaEmisor,
                esRespuestaServidor, timestamp, true);
    }
}
