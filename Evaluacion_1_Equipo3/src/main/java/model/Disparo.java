/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import model.enums.ResultadoDisparo;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class Disparo{
    private int fila;
    private int columna;
    private ResultadoDisparo resultado;
    private Jugador jugador;

    public Disparo(Jugador jugador, int fila, int columna, ResultadoDisparo resultado) {
        this.jugador = jugador;
        this.fila = fila;
        this.columna = columna;
        this.resultado = resultado;
    }

    public ResultadoDisparo obtenerResultadoDisparo() {
        return resultado;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    public ResultadoDisparo getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoDisparo resultado) {
        this.resultado = resultado;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }
}
