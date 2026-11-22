/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.usecases;

import eventbus.IEventPublisher;
import model.aggregate.Partida;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class RendirseUseCase {
    private final Partida partida;
    private final IEventPublisher publisher;

    public RendirseUseCase(Partida partida, IEventPublisher publisher) {
        this.partida = partida;
        this.publisher = publisher;
    }

    public void ejecutar(String idJugador) {
        partida.procesarRendicion(idJugador);
        partida.extraerEventos().forEach(publisher::publicar);
    }
}
