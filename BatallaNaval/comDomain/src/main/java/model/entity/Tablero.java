/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entity;

import enums.EstadoCasilla;
import enums.EstadoNave;
import enums.Orientacion;
import enums.ResultadoDisparo;
import enums.TipoNave;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import model.valueObject.Casilla;
import model.valueObject.ConteoNaves;
import model.valueObject.ResumenFlota;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class Tablero {
    private final int filas;
    private final int columnas;
    private boolean confirmado;
    private final Casilla[][] casillas;
    private final List<Nave> naves;

    public Tablero(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.confirmado = false;
        this.casillas = new Casilla[filas][columnas];
        this.naves = new ArrayList<>();
        inicializarCasillas();
    }

    private void inicializarCasillas() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                casillas[f][c] = new Casilla(f, c);
            }
        }
    }

    public boolean colocarNave(Nave nave, int filaInicio, int colInicio) {
        int tamanio = nave.getTipo().getTamano();
        List<Casilla> casillasAOcupar = new ArrayList<>();

        for (int i = 0; i < tamanio; i++) {
            int f = (nave.getOrientacion() == Orientacion.VERTICAL) ? filaInicio + i : filaInicio;
            int c = (nave.getOrientacion() == Orientacion.HORIZONTAL) ? colInicio + i : colInicio;

            if (!validarRango(f, c)) {
                System.out.println("[ERROR TABLERO] No se puede colocar " + nave.getTipo() + " en (" + f + "," + c + "): Fuera de rango.");
                return false;
            }

            Casilla casilla = obtenerCasilla(f, c);
            for (Nave n : naves) {
                if (n.ocupaCasilla(casilla)){
                    System.out.println("[ERROR TABLERO] No se puede colocar " + nave.getTipo() + " en (" + f + "," + c + "): Casilla ocupada.");
                    return false;
                }
            }
            casillasAOcupar.add(casilla);
        }

        for (Casilla c : casillasAOcupar) {
            nave.agregarCasilla(c);
        }
        nave.setColocada(true);
        this.naves.add(nave);
        return true;
    }

    public Casilla obtenerCasilla(int fila, int columna) {
        return validarRango(fila, columna) ? casillas[fila][columna] : null;
    }

    public boolean validarRango(int fila, int columna) {
        return fila >= 0 && fila < filas && columna >= 0 && columna < columnas;
    }

    public boolean validarDisparo(int fila, int columna) {
        if (!validarRango(fila, columna)) return false;
        return obtenerCasilla(fila, columna).obtenerEstado() == EstadoCasilla.SIN_DISPARAR;
    }

    public ResultadoDisparo verificarImpacto(int fila, int columna) {
        Casilla casilla = obtenerCasilla(fila, columna);
        for (Nave nave : naves) {
            if (nave.ocupaCasilla(casilla)) {
                casilla.cambiarEstado(EstadoCasilla.IMPACTADA);
                boolean hundida = nave.recibirDisparo();
                if (hundida) {
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

    public List<Casilla> obtenerCasillasNaveEn(int fila, int columna) {
        Casilla casilla = obtenerCasilla(fila, columna);
        for (Nave nave : naves) {
            if (nave.ocupaCasilla(casilla)) {
                return nave.getCasillasOcupadas();
            }
        }
        return Collections.emptyList();
    }

    public boolean todasNavesHundidas() {
        return !naves.isEmpty() && naves.stream().allMatch(Nave::estaHundida);
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
        return (int) naves.stream().filter(n -> n.getTipo() == tipo && n.getEstado() == estado).count();
    }
    
    public void agregarNave(Nave nave) {
        naves.add(nave);
    }

    public int getFilas() { return filas; }
    public int getColumnas() { return columnas; }
    public boolean isConfirmado() { return confirmado; }
    public void setConfirmado(boolean confirmado) { this.confirmado = confirmado; }
    public List<Nave> getNaves() { return naves; }
}
