/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.valueObject;

import enums.ResultadoDisparo;
import model.entity.Jugador;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class Disparo {
    private final Jugador disparador;
    private final int fila;
    private final int columna;
    private final ResultadoDisparo resultado;

    public Disparo(Jugador disparador, int fila, int columna, ResultadoDisparo resultado) {
        this.disparador = disparador;
        this.fila = fila;
        this.columna = columna;
        this.resultado = resultado;
    }

    public Jugador getDisparador() { return disparador; }
    public int getFila() { return fila; }
    public int getColumna() { return columna; }
    public ResultadoDisparo obtenerResultadoDisparo() { return resultado; }
}
