/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.mapper;

import dtos.ConteoNavesDTO;
import dtos.CoordenadaDTO;
import dtos.JugadorDTO;
import dtos.NaveDTO;
import dtos.ResumenFlotaDTO;
import dtos.TableroDTO;
import enums.EstadoCasilla;
import enums.TipoNave;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import model.entity.Jugador;
import model.entity.Nave;
import model.entity.Tablero;
import model.valueObject.Casilla;
import model.valueObject.ConteoNaves;
import model.valueObject.ResumenFlota;

/**
 * Clase para mapear las entidades normales a DTOs
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public final class DomainMapper {
    private DomainMapper() {}

    public static JugadorDTO toJugadorDTO(Jugador j) {
        if (j == null) return null;
        return new JugadorDTO(j.getId(), j.getNombre(), j.getColor(), j.getPuntajePartida());
    }

    public static TableroDTO toTableroDTO(Tablero t) {
        EstadoCasilla[][] estados = new EstadoCasilla[10][10];
        List<CoordenadaDTO> conNave = new ArrayList<>();
        for (int f = 0; f < 10; f++) {
            for (int c = 0; c < 10; c++) {
                Casilla casilla = t.obtenerCasilla(f, c);
                estados[f][c] = casilla.obtenerEstado();
                boolean tieneNave = t.getNaves().stream().anyMatch(n -> n.ocupaCasilla(casilla));
                if (tieneNave) {
                    conNave.add(new CoordenadaDTO(f, c));
                }
            }
        }
        return new TableroDTO(estados, conNave);
    }

    public static ResumenFlotaDTO toResumenFlotaDTO(ResumenFlota r) {
        Map<TipoNave, ConteoNavesDTO> map = new EnumMap<>(TipoNave.class);
        for (TipoNave tipo : TipoNave.values()) {
            ConteoNaves c = r.get(tipo);
            map.put(tipo, new ConteoNavesDTO(c.getSinDanos(), c.getDanadas(), c.getHundidas()));
        }
        return new ResumenFlotaDTO(map);
    }

    public static List<NaveDTO> toFlotaDTO(Tablero tablero) {
        List<NaveDTO> flota = new ArrayList<>();
        for (Nave n : tablero.getNaves()) {
            Casilla inicio = n.getCasillasOcupadas().get(0);
            flota.add(new NaveDTO(n.getTipo(), n.getOrientacion(), inicio.getFila(), inicio.getColumna()));
        }
        return flota;
    }
}
