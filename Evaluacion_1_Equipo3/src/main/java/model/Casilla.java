/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import model.enums.EstadoCasilla;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class Casilla{
    private int fila;
    private int columna;
    private EstadoCasilla estado;

    public Casilla(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
        this.estado = EstadoCasilla.SIN_DISPARAR;
    }

    public EstadoCasilla obtenerEstado() {
        return estado;
    }

    public void cambiarEstado(EstadoCasilla nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Casilla casilla = (Casilla) obj;
        return fila == casilla.fila && columna == casilla.columna;
    }
}
