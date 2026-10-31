/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.usecases;

import domain.model.aggregate.Partida;
import domain.model.entity.Jugador;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class RendirseUseCase {

    private final Partida partida;

    public RendirseUseCase(Partida partida) {
        this.partida = partida;
    }

    public void ejecutar(Jugador jugador) {
        partida.procesarRendicion(jugador);
    }
}
