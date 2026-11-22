/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package application;

import dtos.JugadorDTO;
import dtos.ResumenFlotaDTO;
import dtos.TableroDTO;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public interface IControlJuego {
    void iniciarPartida();
    void disparar(int fila, int col);
    void abandonarPartida();
    JugadorDTO getJugadorLocalDTO();
    JugadorDTO getJugadorRivalDTO();
    TableroDTO getTableroPropioDTO();
    ResumenFlotaDTO getResumenFlotaRivalDTO();
}
