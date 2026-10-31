/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.usecases;

import domain.model.aggregate.Partida;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class IniciarPartidaUseCase {

    private final Partida partida;

    public IniciarPartidaUseCase(Partida partida) {
        this.partida = partida;
    }

    public void ejecutar() {
        partida.iniciarTemporizador();
    }
}
