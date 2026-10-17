/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.Jugador;
import model.Partida;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class ControlJuego implements IControlJuego {

    private Partida partida;

    public ControlJuego(Partida partida) {
        this.partida = partida;
    }

    @Override
    public void disparar(Jugador jugador, int fila, int col) {
        // 1. Validaciones previas en la partida (Turno y Tiempo)
        if (!partida.validarTurno(jugador)) {
            System.out.println("Error: No es el turno de " + jugador.getNombre());
            return;
        }

        if (!partida.validarTiempo()) {
            System.out.println("Error: El tiempo de turno ha expirado.");
            partida.actualizarTurno();
            partida.notifyObservers();
            return;
        }

        // 2. Procesar el disparo localmente en la partida
        partida.procesarDisparo(jugador, fila, col);
    }

    @Override
    public void iniciarPartida() {
        partida.reiniciarTiempo();
        partida.notifyObservers();
    }

    @Override
    public void rendirse(Jugador jugador) {
        if (jugador == null) {
            return;
        }

        System.out.println("El jugador " + jugador.getNombre() + " se ha rendido.");

        // Procesa la rendición en el Modelo local
        partida.procesarRendicion(jugador);
    }
}
