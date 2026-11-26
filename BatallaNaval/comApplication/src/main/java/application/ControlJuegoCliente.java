/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application;

import dtos.CoordenadaDTO;
import dtos.JugadorDTO;
import dtos.NaveDTO;
import dtos.ResumenFlotaDTO;
import dtos.TableroDTO;
import enums.EstadoCasilla;
import enums.Orientacion;
import enums.ResultadoDisparo;
import eventbus.IEventPublisher;
import eventbus.IEventSubscriber;
import events.DisparoProcesadoEvent;
import events.PartidaSincronizadaEvent;
import events.SolicitudAbandonarEvent;
import events.SolicitudDisparoEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class ControlJuegoCliente implements IControlJuego {

    private JugadorDTO jugadorLocal;
    private JugadorDTO jugadorRival;
    private final List<NaveDTO> flotaLocal;
    private final TableroDTO tableroPropio;
    private ResumenFlotaDTO resumenFlotaRival;
    private final IEventPublisher publisher;

    public ControlJuegoCliente(JugadorDTO jugadorLocal, List<NaveDTO> flotaLocal,
            IEventPublisher publisher, IEventSubscriber subscriber) {
        this.jugadorLocal = jugadorLocal;
        this.flotaLocal = List.copyOf(flotaLocal);
        this.tableroPropio = construirTableroInicial(flotaLocal);
        this.resumenFlotaRival = ResumenFlotaDTO.vacio();
        this.publisher = publisher;

        registrarSuscriptoresServidor(subscriber);
    }

    public void enviarFlotaInicialAlServidor() {
        publisher.publicar(new PartidaSincronizadaEvent(
                jugadorLocal,
                flotaLocal,
                ResumenFlotaDTO.vacio(),
                false
        ));
    }

    private void registrarSuscriptoresServidor(IEventSubscriber subscriber) {
        // Respuesta inicial del Servidor con datos del Jugador 1
        subscriber.suscribir(PartidaSincronizadaEvent.class, event -> {
            if (!event.esRemoto() || !event.esRespuestaServidor()) {
                return;
            }
            this.jugadorRival = event.jugadorEmisor();
            if (event.resumenFlotaEmisor() != null) {
                this.resumenFlotaRival = event.resumenFlotaEmisor();
            }
        });

        // Resultado de cualquier disparo resuelto por el Servidor
        subscriber.suscribir(DisparoProcesadoEvent.class, event -> {
            if (!event.esRemoto()) {
                return;
            }

            if (event.idJugadorDisparador().equals(jugadorLocal.id())) {
                this.jugadorLocal = new JugadorDTO(
                        jugadorLocal.id(), jugadorLocal.nombre(), jugadorLocal.color(), event.puntajeDisparador()
                );
                this.resumenFlotaRival = event.resumenFlotaRival();
            } else {
                // El Servidor disparó sobre nuestro tablero: actualizar estado visual del minimapa
                EstadoCasilla[][] estados = tableroPropio.estados();
                if (event.resultado() == ResultadoDisparo.AGUA) {
                    estados[event.fila()][event.columna()] = EstadoCasilla.AGUA;
                } else if (event.resultado() == ResultadoDisparo.HUNDIDA && event.casillasHundidas() != null) {
                    for (CoordenadaDTO c : event.casillasHundidas()) {
                        estados[c.fila()][c.columna()] = EstadoCasilla.HUNDIDA;
                    }
                } else {
                    estados[event.fila()][event.columna()] = EstadoCasilla.IMPACTADA;
                }
            }
        });
    }

    private TableroDTO construirTableroInicial(List<NaveDTO> flota) {
        EstadoCasilla[][] estados = new EstadoCasilla[10][10];
        for (int f = 0; f < 10; f++) {
            for (int c = 0; c < 10; c++) {
                estados[f][c] = EstadoCasilla.SIN_DISPARAR;
            }
        }
        List<CoordenadaDTO> casillasConNave = new ArrayList<>();
        for (NaveDTO n : flota) {
            int tamano = n.tipo().getTamano();
            for (int i = 0; i < tamano; i++) {
                int f = (n.orientacion() == Orientacion.VERTICAL) ? n.filaInicio() + i : n.filaInicio();
                int c = (n.orientacion() == Orientacion.HORIZONTAL) ? n.colInicio() + i : n.colInicio();
                casillasConNave.add(new CoordenadaDTO(f, c));
            }
        }
        return new TableroDTO(estados, casillasConNave);
    }

    // CU Colocar Naves PENDD
    @Override
    public void colocarNave(int idNave, int fila, int col, Orientacion orientacion) {
        throw new UnsupportedOperationException("CU Colocar Naves: pendiente de implementar en ControlJuegoCliente");
    }

    @Override
    public void retirarNave(int idNave) {
        throw new UnsupportedOperationException("CU Colocar Naves: pendiente de implementar en ControlJuegoCliente");
    }

    @Override
    public void confirmarTablero() {
        throw new UnsupportedOperationException("CU Colocar Naves: pendiente de implementar en ControlJuegoCliente");
    }

    @Override
    public List<NaveDTO> getFlotaLocalDTO() {
        return flotaLocal;
    }

    @Override
    public void iniciarPartida() {
        // El Servidor controla el inicio y el reloj autoritativo
    }

    @Override
    public void disparar(int fila, int col) {
        publisher.publicar(new SolicitudDisparoEvent(jugadorLocal.id(), fila, col));
    }

    @Override
    public void abandonarPartida() {
        publisher.publicar(new SolicitudAbandonarEvent(jugadorLocal.id()));
    }

    @Override
    public JugadorDTO getJugadorLocalDTO() {
        return jugadorLocal;
    }

    @Override
    public JugadorDTO getJugadorRivalDTO() {
        return jugadorRival;
    }

    @Override
    public TableroDTO getTableroPropioDTO() {
        return tableroPropio;
    }

    @Override
    public ResumenFlotaDTO getResumenFlotaRivalDTO() {
        return resumenFlotaRival;
    }
}
