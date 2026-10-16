/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.enums.EstadoPartida;
import model.enums.MotivoFin;
import model.enums.ResultadoDisparo;
import observer.IObservable;
import observer.IObservador;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class Partida implements IModel, IObservable{
    private EstadoPartida estado;
    private Jugador jugadorTurno;
    private Jugador ganador;
    private MotivoFin motivoFin;
    private int tiempoRestante;
    private LocalDateTime inicioTurno;
    
    private List<Jugador> jugadores;
    private List<IObservador> observadores = new ArrayList<>();

    public Partida() {
        this.estado = EstadoPartida.ESPERANDO_JUGADOR;
        this.jugadores = new ArrayList<>();
        this.observadores = new ArrayList<>();
        this.tiempoRestante = 30; // 30 segundos por turno
    }
    
    // --- Implementación de IObservable ---
    @Override
    public void addObserver(IObservador o) {
        observadores.add(o);
    }

    @Override
    public void removeObserver(IObservador o) {
        observadores.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (IObservador obs : observadores) {
            obs.update(this);
        }
    }
    
    // --- Validaciones y reglas ---
    public boolean validarTurno(Jugador j) {
        return this.jugadorTurno.equals(j);
    }

    public boolean validarTiempo() {
        return this.tiempoRestante > 0;
    }
    
    public void procesarDisparo(Jugador jugadorDisparador, int fila, int col) {
        if (!validarTurno(jugadorDisparador)) {
            return;
        }

        if (!validarTiempo()) {
            actualizarTurno();
            notifyObservers();
            return;
        }

        Jugador rival = obtenerRival(jugadorDisparador);
        Tablero tableroRival = rival.getTableroPropio();

        if (!tableroRival.validarDisparo(fila, col)) {
            return; // Ya fue disparada o fuera de rango
        }

        ResultadoDisparo resultado = tableroRival.verificarImpacto(fila, col);
        Disparo disparo = new Disparo(jugadorDisparador, fila, col, resultado);
        jugadorDisparador.realizarDisparo(disparo);

        // Si falló (Agua), cambia el turno; si impactó, conserva turno extra
        if (resultado == ResultadoDisparo.AGUA) {
            actualizarTurno();
        } else {
            reiniciarTiempo();
        }

        verificarFinPartida();
        notifyObservers();
    }
    
    public void actualizarTurno() {
        if (jugadores.size() == 2) {
            Jugador rival = obtenerRival(jugadorTurno);
            this.jugadorTurno = rival;
            reiniciarTiempo();
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
                this.ganador = j;
                this.estado = EstadoPartida.FINALIZADA;
                this.motivoFin = MotivoFin.FLOTA_HUNDIDA;
                break;
            }
        }
    }
    
    public Jugador obtenerRival(Jugador jugador) {
        for (Jugador j : jugadores) {
            if (!j.equals(jugador)) {
                return j;
            }
        }
        return null;
    }
    
    public void agregarJugador(Jugador j) {
        if (jugadores.size() < 2) {
            jugadores.add(j);
            if (jugadorTurno == null) {
                jugadorTurno = j;
            }
        }
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

    public List<IObservador> getObservadores() {
        return observadores;
    }

    public void setObservadores(List<IObservador> observadores) {
        this.observadores = observadores;
    }

    @Override
    public Tablero getTablero(Jugador jugador) {
        return jugador.getTableroPropio();
    }
}
