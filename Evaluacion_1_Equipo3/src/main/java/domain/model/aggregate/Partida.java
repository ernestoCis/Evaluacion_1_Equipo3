/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain.model.aggregate;

import domain.events.DisparoProcesadoEvent;
import domain.events.PartidaFinalizadaEvent;
import domain.events.TiempoTickEvent;
import domain.events.TurnoCambiadoEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import domain.model.valueObject.Disparo;
import domian.model.IModel;
import domain.model.entity.Jugador;
import domain.model.valueObject.ResumenFlota;
import domain.model.entity.Tablero;
import domain.model.enums.EstadoPartida;
import domain.model.enums.MotivoFin;
import domain.model.enums.ResultadoDisparo;
import infrastructure.eventbus.EventBus;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class Partida implements IModel {

    private EstadoPartida estado;
    private Jugador jugadorTurno;
    private Jugador ganador;
    private MotivoFin motivoFin;
    private int tiempoRestante;
    private LocalDateTime inicioTurno;

    private List<Jugador> jugadores;

    private Timer timer;

    public Partida() {
        this.estado = EstadoPartida.ESPERANDO_JUGADOR;
        this.jugadores = new ArrayList<>();
        this.tiempoRestante = 30;
    }

    public void iniciarTemporizador() {
        if (timer != null) {
            timer.cancel();
        }

        reiniciarTiempo();
        timer = new Timer();

        // Tarea que se repetirá cada 1 segundo el scheduleAtFixedRate
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                tiempoRestante--;

                if (tiempoRestante <= 0) {
                    actualizarTurno("¡Tiempo agotado! Se pierde el turno."); // Si llega a 0, cambia el turno
                } else {
                    // emitir evento
                    EventBus.publish(new TiempoTickEvent(tiempoRestante));
                }
            }
        }, 1000, 1000);
    }

    public void calcularPuntaje(Jugador jugador, ResultadoDisparo resultado) {
        if (resultado == ResultadoDisparo.HUNDIDA) {
            jugador.setPuntajePartida(jugador.getPuntajePartida() + 1);
        }
    }

    public boolean validarTurno(Jugador j) {
        return this.jugadorTurno.equals(j);
    }

    public boolean validarTiempo() {
        return this.tiempoRestante > 0;
    }

    public void procesarDisparo(Jugador jugadorDisparador, int fila, int col) {
        if (estado == EstadoPartida.FINALIZADA || !validarTurno(jugadorDisparador) || !validarTiempo()) {
            return;
        }

        Jugador rival = obtenerRival(jugadorDisparador);
        Tablero tableroRival = rival.getTableroPropio();

        if (!tableroRival.validarDisparo(fila, col)) {
            return;
        }

        ResultadoDisparo resultado = tableroRival.verificarImpacto(fila, col);
        Disparo disparo = new Disparo(jugadorDisparador, fila, col, resultado);
        jugadorDisparador.realizarDisparo(disparo);

        calcularPuntaje(jugadorDisparador, resultado);
        ResumenFlota resumenRival = tableroRival.resumirFlota();

        String aviso = (resultado == ResultadoDisparo.AGUA) ? "¡Agua! Se pierde el turno." : null;

        // Publicar Evento de Disparo
        EventBus.publish(new DisparoProcesadoEvent(jugadorDisparador, fila, col, resultado, resumenRival, aviso));

        if (resultado == ResultadoDisparo.AGUA) {
            actualizarTurno(null);
        } else {
            iniciarTemporizador();
        }

        verificarFinPartida();
    }

    public void actualizarTurno(String mensajeAviso) {
        if (jugadores.size() == 2) {
            this.jugadorTurno = jugadores.get(0); // Lógica de mock fija o alternada
            iniciarTemporizador();
            EventBus.publish(new TurnoCambiadoEvent(this.jugadorTurno, mensajeAviso));
        }
    }

    public void reiniciarTiempo() {
        this.tiempoRestante = 30;
        this.inicioTurno = LocalDateTime.now();
    }

    public void verificarFinPartida() {
        for (Jugador j : jugadores) {
            Jugador rival = obtenerRival(j);
            if (rival.getTableroPropio().todasNavesHundidas()) {
                if (timer != null) {
                    timer.cancel();
                }
                this.ganador = j;
                this.estado = EstadoPartida.FINALIZADA;
                this.motivoFin = MotivoFin.FLOTA_HUNDIDA;
                EventBus.publish(new PartidaFinalizadaEvent(ganador, motivoFin));
                break;
            }
        }
    }

    @Override
    public Jugador obtenerRival(Jugador jugador) {
        return jugadores.stream().filter(j -> !j.equals(jugador)).findFirst().orElse(null);
    }

    public void agregarJugador(Jugador j) {
        if (jugadores.size() < 2) {
            jugadores.add(j);
            if (jugadorTurno == null) {
                jugadorTurno = j;
            }
        }
    }

    public void procesarRendicion(Jugador jugadorQueSeRinde) {
        if (this.estado == EstadoPartida.FINALIZADA) {
            return;
        }
        if (timer != null) {
            timer.cancel();
        }

        Jugador rival = obtenerRival(jugadorQueSeRinde);
        if (rival != null) {
            this.ganador = rival;
        }

        this.estado = EstadoPartida.FINALIZADA;
        this.motivoFin = MotivoFin.ABANDONO;

        EventBus.publish(new PartidaFinalizadaEvent(ganador, motivoFin));
    }

    // --- Getters y Setters
    public EstadoPartida getEstado() {
        return estado;
    }

    public void setEstado(EstadoPartida estado) {
        this.estado = estado;
    }

    public Jugador getJugadorTurno() {
        return jugadorTurno;
    }

    public void setJugadorTurno(Jugador jugadorTurno) {
        this.jugadorTurno = jugadorTurno;
    }

    public Jugador getGanador() {
        return ganador;
    }

    public void setGanador(Jugador ganador) {
        this.ganador = ganador;
    }

    public MotivoFin getMotivoFin() {
        return motivoFin;
    }

    public void setMotivoFin(MotivoFin motivoFin) {
        this.motivoFin = motivoFin;
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }

    public void setTiempoRestante(int tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    @Override
    public Tablero getTablero(Jugador jugador) {
        return jugador.getTableroPropio();
    }

    @Override
    public ResumenFlota getResumenFlotaRival(Jugador observador) {
        for (Jugador j : jugadores) {
            if (!j.equals(observador)) {
                return j.getTableroPropio().resumirFlota();
            }
        }
        return ResumenFlota.vacio();
    }
}
