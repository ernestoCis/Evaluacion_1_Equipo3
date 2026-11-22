/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application;

import application.mapper.DomainMapper;
import application.usecases.IniciarPartidaUseCase;
import application.usecases.RealizarDisparoUseCase;
import application.usecases.RendirseUseCase;
import dtos.JugadorDTO;
import dtos.NaveDTO;
import dtos.ResumenFlotaDTO;
import dtos.TableroDTO;
import eventbus.IEventPublisher;
import eventbus.IEventSubscriber;
import events.PartidaSincronizadaEvent;
import events.SolicitudAbandonarEvent;
import events.SolicitudDisparoEvent;
import java.util.Collections;
import model.aggregate.Partida;
import model.entity.Jugador;
import model.entity.Nave;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class ControlJuegoServidor implements IControlJuego {

    private final Partida partida;
    private final String idJugadorLocal;
    private final IEventPublisher publisher;
    private final IniciarPartidaUseCase iniciarUC;
    private final RealizarDisparoUseCase dispararUC;
    private final RendirseUseCase rendirseUC;

    public ControlJuegoServidor(Partida partida, String idJugadorLocal,
                                IEventPublisher publisher, IEventSubscriber subscriber) {
        this.partida = partida;
        this.idJugadorLocal = idJugadorLocal;
        this.publisher = publisher;
        this.iniciarUC = new IniciarPartidaUseCase(partida, publisher);
        this.dispararUC = new RealizarDisparoUseCase(partida, publisher);
        this.rendirseUC = new RendirseUseCase(partida, publisher);

        registrarSuscriptoresCliente(subscriber);
    }

    private void registrarSuscriptoresCliente(IEventSubscriber subscriber) {
        // El Cliente (PC 2) se conecta y envía su flota al Servidor
        subscriber.suscribir(PartidaSincronizadaEvent.class, event -> {
            if (!event.esRemoto() || event.esRespuestaServidor()) return;

            JugadorDTO dtoRival = event.jugadorEmisor();
            if (partida.buscarJugadorPorId(dtoRival.id()) == null) {
                Jugador rival = new Jugador(dtoRival.id(), dtoRival.nombre(), dtoRival.color());
                for (NaveDTO n : event.flotaEmisor()) {
                    rival.getTableroPropio().colocarNave(new Nave(n.tipo(), n.orientacion()), n.filaInicio(), n.colInicio());
                }
                partida.agregarJugador(rival);
            }

            // Responder al Cliente con los datos del Servidor (sin enviar coordenadas de barcos del Servidor)
            Jugador local = partida.buscarJugadorPorId(idJugadorLocal);
            ResumenFlotaDTO resumenServidor = DomainMapper.toResumenFlotaDTO(local.getTableroPropio().resumirFlota());
            publisher.publicar(new PartidaSincronizadaEvent(
                    DomainMapper.toJugadorDTO(local),
                    Collections.emptyList(),
                    resumenServidor,
                    true
            ));

            // Iniciar la partida autoritativa y el temporizador
            iniciarPartida();
        });

        // El Cliente solicita disparar a una casilla
        subscriber.suscribir(SolicitudDisparoEvent.class, event -> {
            if (!event.esRemoto()) return;
            dispararUC.ejecutar(event.idJugador(), event.fila(), event.columna());
        });

        // El Cliente solicita abandonar la partida
        subscriber.suscribir(SolicitudAbandonarEvent.class, event -> {
            if (!event.esRemoto()) return;
            rendirseUC.ejecutar(event.idJugador());
        });
    }

    @Override
    public void iniciarPartida() {
        iniciarUC.ejecutar(true);
    }

    @Override
    public void disparar(int fila, int col) {
        dispararUC.ejecutar(idJugadorLocal, fila, col);
    }

    @Override
    public void abandonarPartida() {
        rendirseUC.ejecutar(idJugadorLocal);
    }

    @Override
    public JugadorDTO getJugadorLocalDTO() {
        return DomainMapper.toJugadorDTO(partida.buscarJugadorPorId(idJugadorLocal));
    }

    @Override
    public JugadorDTO getJugadorRivalDTO() {
        Jugador local = partida.buscarJugadorPorId(idJugadorLocal);
        return DomainMapper.toJugadorDTO(partida.obtenerRival(local));
    }

    @Override
    public TableroDTO getTableroPropioDTO() {
        Jugador local = partida.buscarJugadorPorId(idJugadorLocal);
        return DomainMapper.toTableroDTO(local.getTableroPropio());
    }

    @Override
    public ResumenFlotaDTO getResumenFlotaRivalDTO() {
        Jugador local = partida.buscarJugadorPorId(idJugadorLocal);
        Jugador rival = partida.obtenerRival(local);
        return rival != null
                ? DomainMapper.toResumenFlotaDTO(rival.getTableroPropio().resumirFlota())
                : ResumenFlotaDTO.vacio();
    }
}
