/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package controller;

import model.Jugador;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public interface IControlJuego {
    void disparar(Jugador jugador, int fila, int col);
    void iniciarPartida();
    void rendirse(Jugador jugador);
}
