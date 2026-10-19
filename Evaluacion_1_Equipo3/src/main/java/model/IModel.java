/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package model;

import model.enums.EstadoPartida;
import model.enums.MotivoFin;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public interface IModel {
    ResumenFlota getResumenFlotaRival(Jugador observador);
    EstadoPartida getEstado();
    Jugador getJugadorTurno();
    Tablero getTablero(Jugador jugador);
    Jugador getGanador();
    MotivoFin getMotivoFin();
    int getTiempoRestante();
}
