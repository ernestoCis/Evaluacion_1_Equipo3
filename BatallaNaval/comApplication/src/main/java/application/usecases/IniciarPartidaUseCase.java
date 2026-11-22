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
public class IniciarPartidaUseCase {
    private final Partida partida;
    private final IEventPublisher publisher;

    public IniciarPartidaUseCase(Partida partida, IEventPublisher publisher) {
        this.partida = partida;
        this.publisher = publisher;
    }

    public void ejecutar(boolean esServidorAutoritativo) {
        partida.setPublicadorAsincrono(publisher::publicar);
        partida.iniciarPartida(esServidorAutoritativo);
        partida.extraerEventos().forEach(publisher::publicar);
    }
}
