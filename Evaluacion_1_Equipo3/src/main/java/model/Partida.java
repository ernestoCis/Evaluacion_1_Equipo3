/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;         // <-- Importación nueva
import java.util.TimerTask;     // <-- Importación nueva
import model.enums.EstadoPartida;
import model.enums.MotivoFin;
import model.enums.ResultadoDisparo;
import observer.IObservable;
import observer.IObservador;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class Partida implements IModel, IObservable {

    private EstadoPartida estado;
    private Jugador jugadorTurno;
    private Jugador ganador;
    private MotivoFin motivoFin;
    private int tiempoRestante;
    private LocalDateTime inicioTurno;
    private String mensaje; // Aviso para el jugador (tiempo agotado, disparo al agua)

    private List<Jugador> jugadores;
    private List<IObservador> observadores = new ArrayList<>();

    private Timer timer;

    public Partida() {
        this.estado = EstadoPartida.ESPERANDO_JUGADOR;
        this.jugadores = new ArrayList<>();
        this.observadores = new ArrayList<>();
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
                    actualizarTurno(); // Si llega a 0, cambia el turno
                    notificar("¡Tiempo agotado! Se pierde el turno.");
                } else {
                    notifyObservers();
                }
            }
        }, 1000, 1000);
    }

    public void calcularPuntaje(Jugador jugador, ResultadoDisparo resultado) {
        // Solo aumenta 1 punto si el resultado es HUNDIDA
        if (resultado == ResultadoDisparo.HUNDIDA) {
            jugador.setPuntajePartida(jugador.getPuntajePartida() + 1);
        }
        // Ya no hacemos nada si es solo IMPACTO
    }

    @Override
    public void addObserver(IObservador o) {
        observadores.add(o);
    }

    @Override
    public void removeObserver(IObservador o) {
        observadores.remove(o);
    }

    private void notificar(String mensaje) {
        this.mensaje = mensaje;
        notifyObservers();
        this.mensaje = null;
    }

    @Override
    public String getMensaje() {
        return mensaje;
    }

    @Override
    public void notifyObservers() {
        for (IObservador obs : observadores) {
            obs.update(this);
        }
    }

    public boolean validarTurno(Jugador j) {
        return this.jugadorTurno.equals(j);
    }

    public boolean validarTiempo() {
        return this.tiempoRestante > 0;
    }

    public void procesarDisparo(Jugador jugadorDisparador, int fila, int col) {
        // Si la partida ya terminó, no se aceptan más disparos
        if (estado == EstadoPartida.FINALIZADA) {
            return;
        }
        
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

        calcularPuntaje(jugadorDisparador, resultado);

        // Si falló (Agua), cambia el turno; si impactó, conserva turno extra
        if (resultado == ResultadoDisparo.AGUA) {
            actualizarTurno();
        } else {
            iniciarTemporizador(); // <-- Iniciamos el timer de nuevo si acierta
        }

        verificarFinPartida();
        if (resultado == ResultadoDisparo.AGUA) {
            notificar("¡Agua! Se pierde el turno.");
        } else {
            notifyObservers();
        }
    }

    public void actualizarTurno() {
        if (jugadores.size() == 2) {
            Jugador rival = obtenerRival(jugadorTurno);
//            this.jugadorTurno = rival;
            this.jugadorTurno = jugadores.get(0); // Respetando tu lógica de 1 jugador

            iniciarTemporizador(); // <-- Arranca el tiempo del nuevo turno
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
                    timer.cancel(); // Detenemos el tiempo si alguien ganó
                }
                this.ganador = j;
                this.estado = EstadoPartida.FINALIZADA;
                this.motivoFin = MotivoFin.FLOTA_HUNDIDA;
                break;
            }
        }
    }

    @Override
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

    public void procesarRendicion(Jugador jugadorQueSeRinde) {
        if (this.estado == EstadoPartida.FINALIZADA) {
            return;
        }

        if (timer != null) {
            timer.cancel(); // Detenemos el tiempo si alguien se rinde
        }
        Jugador rival = obtenerRival(jugadorQueSeRinde);
        if (rival != null) {
            this.ganador = rival;
        }

        this.estado = EstadoPartida.FINALIZADA;
        this.motivoFin = MotivoFin.ABANDONO;

        notifyObservers();
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
