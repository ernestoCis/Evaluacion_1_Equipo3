/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.aggregate;

import dtos.ConteoNavesDTO;
import dtos.CoordenadaDTO;
import dtos.ResumenFlotaDTO;
import enums.EstadoPartida;
import enums.MotivoFin;
import enums.ResultadoDisparo;
import enums.TipoNave;
import events.DisparoProcesadoEvent;
import events.DomainEvent;
import events.PartidaFinalizadaEvent;
import events.TiempoTickEvent;
import events.TurnoCambiadoEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import model.entity.Jugador;
import model.entity.Tablero;
import model.valueObject.ConteoNaves;
import model.valueObject.Disparo;
import model.valueObject.ResumenFlota;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class Partida {

    private EstadoPartida estado;
    private Jugador jugadorTurno;
    private Jugador ganador;
    private MotivoFin motivoFin;
    private int tiempoRestante;
    private LocalDateTime inicioTurno;

    private final List<Jugador> jugadores;
    private final List<DomainEvent> eventosPendientes;
    private Timer timer;
    private Consumer<DomainEvent> publicadorAsincrono;

    public Partida() {
        this.estado = EstadoPartida.ESPERANDO_JUGADOR;
        this.jugadores = new ArrayList<>();
        this.eventosPendientes = new ArrayList<>();
        this.tiempoRestante = 30;
    }

    public void setPublicadorAsincrono(Consumer<DomainEvent> publicadorAsincrono) {
        this.publicadorAsincrono = publicadorAsincrono;
    }

    public void iniciarPartida(boolean esServidorAutoritativo) {
        if (jugadores.size() == 2) {
            this.estado = EstadoPartida.EN_CURSO;
            this.jugadorTurno = jugadores.get(0);
            reiniciarTiempo();
            if (esServidorAutoritativo) {
                iniciarTemporizador();
            }
        }
    }

    public void iniciarTemporizador() {
        if (timer != null) {
            timer.cancel();
        }
        reiniciarTiempo();
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (estado != EstadoPartida.EN_CURSO) {
                    cancel();
                    return;
                }
                tiempoRestante--;
                if (tiempoRestante <= 0) {
                    actualizarTurno("¡Tiempo agotado! Se pierde el turno.");
                    if (publicadorAsincrono != null) {
                        extraerEventos().forEach(publicadorAsincrono);
                    }
                } else if (publicadorAsincrono != null) {
                    publicadorAsincrono.accept(new TiempoTickEvent(tiempoRestante));
                }
            }
        }, 1000, 1000);
    }

    public void procesarDisparo(String idJugadorDisparador, int fila, int col) {
        Jugador disparador = buscarJugadorPorId(idJugadorDisparador);
        if (disparador == null || estado != EstadoPartida.EN_CURSO || !validarTurno(disparador) || !validarTiempo()) {
            return;
        }

        Jugador rival = obtenerRival(disparador);
        Tablero tableroRival = rival.getTableroPropio();

        if (!tableroRival.validarDisparo(fila, col)) {
            return;
        }

        ResultadoDisparo resultado = tableroRival.verificarImpacto(fila, col);
        disparador.realizarDisparo(new Disparo(disparador, fila, col, resultado));
        calcularPuntaje(disparador, resultado);

        List<CoordenadaDTO> casillasHundidas = (resultado == ResultadoDisparo.HUNDIDA)
                ? tableroRival.obtenerCasillasNaveEn(fila, col).stream()
                    .map(c -> new CoordenadaDTO(c.getFila(), c.getColumna()))
                    .toList()
                : Collections.emptyList();

        ResumenFlotaDTO resumenDTO = mapearResumen(tableroRival.resumirFlota());
        String aviso = (resultado == ResultadoDisparo.AGUA) ? "¡Agua! Se pierde el turno." : null;

        registrarEvento(new DisparoProcesadoEvent(
                disparador.getId(), fila, col, resultado, casillasHundidas, resumenDTO, disparador.getPuntajePartida(), aviso
        ));

        if (resultado == ResultadoDisparo.AGUA) {
            actualizarTurno(null);
        } else if (timer != null) {
            iniciarTemporizador();
        } else {
            reiniciarTiempo();
        }

        verificarFinPartida();
    }

    public void actualizarTurno(String mensajeAviso) {
        if (jugadores.size() == 2) {
            this.jugadorTurno = (this.jugadorTurno.equals(jugadores.get(0)))
                    ? jugadores.get(1)
                    : jugadores.get(0);

            if (timer != null) {
                iniciarTemporizador();
            } else {
                reiniciarTiempo();
            }
            registrarEvento(new TurnoCambiadoEvent(this.jugadorTurno.getId(), mensajeAviso));
        }
    }

    public void aplicarCambioTurnoRemoto(String idNuevoTurno) {
        Jugador nuevo = buscarJugadorPorId(idNuevoTurno);
        if (nuevo != null) {
            this.jugadorTurno = nuevo;
            reiniciarTiempo();
        }
    }

    public void procesarRendicion(String idJugadorQueSeRinde) {
        if (this.estado == EstadoPartida.FINALIZADA) return;
        detenerTemporizador();

        Jugador perdedor = buscarJugadorPorId(idJugadorQueSeRinde);
        Jugador rival = obtenerRival(perdedor);
        if (rival != null) {
            this.ganador = rival;
        }
        this.estado = EstadoPartida.FINALIZADA;
        this.motivoFin = MotivoFin.ABANDONO;
        registrarEvento(new PartidaFinalizadaEvent(ganador != null ? ganador.getId() : null, motivoFin));
    }

    public void aplicarFinRemoto(String idGanador, MotivoFin motivo) {
        detenerTemporizador();
        this.ganador = buscarJugadorPorId(idGanador);
        this.motivoFin = motivo;
        this.estado = EstadoPartida.FINALIZADA;
    }

    public void verificarFinPartida() {
        for (Jugador j : jugadores) {
            Jugador rival = obtenerRival(j);
            if (rival != null && rival.getTableroPropio().todasNavesHundidas()) {
                detenerTemporizador();
                this.ganador = j;
                this.estado = EstadoPartida.FINALIZADA;
                this.motivoFin = MotivoFin.FLOTA_HUNDIDA;
                registrarEvento(new PartidaFinalizadaEvent(ganador.getId(), motivoFin));
                break;
            }
        }
    }

    public void calcularPuntaje(Jugador jugador, ResultadoDisparo resultado) {
        if (resultado == ResultadoDisparo.HUNDIDA) {
            jugador.setPuntajePartida(jugador.getPuntajePartida() + 1);
        }
    }

    public boolean validarTurno(Jugador j) {
        return this.jugadorTurno != null && this.jugadorTurno.equals(j);
    }

    public boolean validarTiempo() {
        return this.tiempoRestante > 0;
    }

    public void reiniciarTiempo() {
        this.tiempoRestante = 30;
        this.inicioTurno = LocalDateTime.now();
    }

    public void detenerTemporizador() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void agregarJugador(Jugador j) {
        if (jugadores.size() < 2 && buscarJugadorPorId(j.getId()) == null) {
            jugadores.add(j);
            if (jugadorTurno == null) {
                jugadorTurno = j;
            }
        }
    }

    public void registrarJugadorComoPrimero(Jugador j1) {
        jugadores.remove(j1);
        jugadores.add(0, j1);
        this.jugadorTurno = j1;
    }

    public Jugador buscarJugadorPorId(String id) {
        return jugadores.stream().filter(j -> j.getId().equals(id)).findFirst().orElse(null);
    }

    public Jugador obtenerRival(Jugador jugador) {
        return jugadores.stream().filter(j -> !j.equals(jugador)).findFirst().orElse(null);
    }

    private void registrarEvento(DomainEvent event) {
        eventosPendientes.add(event);
    }

    public synchronized List<DomainEvent> extraerEventos() {
        List<DomainEvent> copia = new ArrayList<>(eventosPendientes);
        eventosPendientes.clear();
        return copia;
    }

    private ResumenFlotaDTO mapearResumen(ResumenFlota resumen) {
        Map<TipoNave, ConteoNavesDTO> map = new EnumMap<>(TipoNave.class);
        for (TipoNave t : TipoNave.values()) {
            ConteoNaves c = resumen.get(t);
            map.put(t, new ConteoNavesDTO(c.getSinDanos(), c.getDanadas(), c.getHundidas()));
        }
        return new ResumenFlotaDTO(map);
    }

    public EstadoPartida getEstado() { return estado; }
    public Jugador getJugadorTurno() { return jugadorTurno; }
    public Jugador getGanador() { return ganador; }
    public int getTiempoRestante() { return tiempoRestante; }
    public List<Jugador> getJugadores() { return jugadores; }
}
