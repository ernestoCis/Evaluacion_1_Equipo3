/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import model.enums.*;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class Tablero {

    private int filas;
    private int columnas;
    private boolean confirmado;
    private Casilla[][] casillas;
    private List<Nave> naves;

    public Tablero(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.confirmado = false;
        this.casillas = new Casilla[filas][columnas];
        this.naves = new ArrayList<>();
        inicializarCasillas();
    }

    public ResumenFlota resumirFlota() {
        Map<TipoNave, ConteoNaves> datos = new EnumMap<>(TipoNave.class);
        for (TipoNave tipo : TipoNave.values()) {
            datos.put(tipo, new ConteoNaves(
                    contar(tipo, EstadoNave.SIN_DAÑOS),
                    contar(tipo, EstadoNave.DAÑADA),
                    contar(tipo, EstadoNave.HUNDIDA)));
        }
        return new ResumenFlota(datos);
    }

    private int contar(TipoNave tipo, EstadoNave estado) {
        return (int) naves.stream()
                .filter(n -> n.getTipo() == tipo && n.getEstado() == estado)
                .count();
    }

    private void inicializarCasillas() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                casillas[f][c] = new Casilla(f, c);
            }
        }
    }

    public boolean colocarNave(Nave nave, int filaInicio, int colInicio) {
        int tamano = nave.getTipo().getTamano();
        List<Casilla> casillasAOcupar = new ArrayList<>();

        for (int i = 0; i < tamano; i++) {
            int f = (nave.getOrientacion() == Orientacion.VERTICAL) ? filaInicio + i : filaInicio;
            int c = (nave.getOrientacion() == Orientacion.HORIZONTAL) ? colInicio + i : colInicio;

            if (!validarRango(f, c)) {
                System.out.println("[ERROR TABLERO] No se puede colocar " + nave.getTipo() + " en (" + f + "," + c + "): Fuera de rango.");
                return false;
            }

            Casilla casilla = obtenerCasilla(f, c);

            // Verificar si ya hay otra nave ocupando esa casilla
            for (Nave n : naves) {
                if (n.ocupaCasilla(casilla)) {
                    System.out.println("[ERROR TABLERO] No se puede colocar " + nave.getTipo() + " en (" + f + "," + c + "): Casilla ocupada.");
                    return false;
                }
            }
            casillasAOcupar.add(casilla);
        }

        // Asignar casillas a la nave e insertarla en la lista del tablero
        for (Casilla c : casillasAOcupar) {
            nave.agregarCasilla(c);
        }
        nave.setColocada(true);
        this.naves.add(nave);
        return true;
    }

    public Casilla obtenerCasilla(int fila, int columna) {
        if (validarRango(fila, columna)) {
            return casillas[fila][columna];
        }
        return null;
    }

    public boolean validarRango(int fila, int columna) {
        return fila >= 0 && fila < filas && columna >= 0 && columna < columnas;
    }

    public boolean validarDisparo(int fila, int columna) {
        if (!validarRango(fila, columna)) {
            return false;
        }
        Casilla c = obtenerCasilla(fila, columna);
        return c.obtenerEstado() == EstadoCasilla.SIN_DISPARAR;
    }

    public ResultadoDisparo verificarImpacto(int fila, int columna) {
        Casilla casilla = obtenerCasilla(fila, columna);

        for (Nave nave : naves) {
            if (nave.ocupaCasilla(casilla)) {
                casilla.cambiarEstado(EstadoCasilla.IMPACTADA);
                boolean hundida = nave.recibirDisparo();
                if (hundida) {
                    // La nave completa fue derribada: todas sus casillas pasan a HUNDIDA
                    for (Casilla c : nave.getCasillasOcupadas()) {
                        c.cambiarEstado(EstadoCasilla.HUNDIDA);
                    }
                    return ResultadoDisparo.HUNDIDA;
                }
                return ResultadoDisparo.IMPACTO;
            }
        }

        casilla.cambiarEstado(EstadoCasilla.AGUA);
        return ResultadoDisparo.AGUA;
    }

    public void agregarNave(Nave nave) {
        naves.add(nave);
    }

    public boolean todasNavesHundidas() {
        for (Nave nave : naves) {
            if (!nave.estaHundida()) {
                return false;
            }
        }
        return !naves.isEmpty();
    }

    public int getFilas() {
        return filas;
    }

    public void setFilas(int filas) {
        this.filas = filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public void setColumnas(int columnas) {
        this.columnas = columnas;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }

    public Casilla[][] getCasillas() {
        return casillas;
    }

    public void setCasillas(Casilla[][] casillas) {
        this.casillas = casillas;
    }

    public List<Nave> getNaves() {
        return naves;
    }

    public void setNaves(List<Nave> naves) {
        this.naves = naves;
    }
}
