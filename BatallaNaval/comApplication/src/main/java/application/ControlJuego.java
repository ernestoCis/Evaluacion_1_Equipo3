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
import events.DisparoProcesadoEvent;
import events.PartidaFinalizadaEvent;
import events.PartidaSincronizadaEvent;
import events.TurnoCambiadoEvent;
import model.aggregate.Partida;
import model.entity.Jugador;
import model.entity.Nave;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class ControlJuego implements IControlJuego {

    private final Partida partida;
    private final String idJugadorLocal;
    private final boolean esServidor;
    private final IEventPublisher publisher;
    private final IniciarPartidaUseCase iniciarUC;
    private final RealizarDisparoUseCase dispararUC;
    private final RendirseUseCase rendirseUC;

    public ControlJuego(Partida partida, String idJugadorLocal, boolean esServidor,
                        IEventPublisher publisher, IEventSubscriber subscriber) {
        this.partida = partida;
        this.idJugadorLocal = idJugadorLocal;
        this.esServidor = esServidor;
        this.publisher = publisher;
        this.iniciarUC = new IniciarPartidaUseCase(partida, publisher);
        this.dispararUC = new RealizarDisparoUseCase(partida, publisher);
        this.rendirseUC = new RendirseUseCase(partida, publisher);

        registrarSuscriptoresRemotos(subscriber);
    }

    public void enviarFlotaInicialPorRed() {
        Jugador local = partida.buscarJugadorPorId(idJugadorLocal);
        publisher.publicar(new PartidaSincronizadaEvent(
                DomainMapper.toJugadorDTO(local),
                DomainMapper.toFlotaDTO(local.getTableroPropio()),
                esServidor
        ));
    }

    private void registrarSuscriptoresRemotos(IEventSubscriber subscriber) {
        // Sincronización inicial de flotas al conectar por Socket
        subscriber.suscribir(PartidaSincronizadaEvent.class, event -> {
            if (!event.esRemoto()) return;

            JugadorDTO dtoRival = event.jugadorEmisor();
            if (partida.buscarJugadorPorId(dtoRival.id()) == null) {
                Jugador rival = new Jugador(dtoRival.id(), dtoRival.nombre(), dtoRival.color());
                for (NaveDTO n : event.flotaEmisor()) {
                    rival.getTableroPropio().colocarNave(new Nave(n.tipo(), n.orientacion()), n.filaInicio(), n.colInicio());
                }
                partida.agregarJugador(rival);
                if (!esServidor) {
                    // Garantizar que Jugador 1 (Servidor) sea el índice 0 en ambos equipos
                    partida.registrarJugadorComoPrimero(rival);
                }
            }

            if (esServidor && !event.esRespuestaServidor()) {
                enviarFlotaInicialPorRed();
                iniciarPartida();
            } else if (!esServidor && event.esRespuestaServidor()) {
                iniciarPartida();
            }
        });

        // Disparo ejecutado por el rival
        subscriber.suscribir(DisparoProcesadoEvent.class, event -> {
            if (!event.esRemoto()) return;
            partida.procesarDisparo(event.idJugadorDisparador(), event.fila(), event.columna());
            partida.extraerEventos(); // Descartar eventos locales duplicados porque ya vinieron de red
        });

        // Cambio de turno remoto
        subscriber.suscribir(TurnoCambiadoEvent.class, event -> {
            if (!event.esRemoto()) return;
            partida.aplicarCambioTurnoRemoto(event.idNuevoJugadorTurno());
        });

        // Fin de partida remoto (abandono o derrota)
        subscriber.suscribir(PartidaFinalizadaEvent.class, event -> {
            if (!event.esRemoto()) return;
            partida.aplicarFinRemoto(event.idGanador(), event.motivoFin());
        });
    }

    @Override
    public void iniciarPartida() {
        iniciarUC.ejecutar(esServidor);
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
