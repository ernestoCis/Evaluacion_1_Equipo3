/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package application;

import dtos.JugadorDTO;
import dtos.NaveDTO;
import dtos.ResumenFlotaDTO;
import dtos.TableroDTO;
import enums.Orientacion;
import java.util.List;

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
    
    //  CU Colocar Naves 
    void colocarNave(int idNave, int fila, int col, Orientacion orientacion);
    void retirarNave(int idNave);
    void confirmarTablero();
    /** Flota del jugador local (las 11 naves, colocadas o no). Se usa para el pintado inicial. */
    List<NaveDTO> getFlotaLocalDTO();

}
