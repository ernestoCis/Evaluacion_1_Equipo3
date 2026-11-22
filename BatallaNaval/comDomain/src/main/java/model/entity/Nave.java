/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entity;

import enums.EstadoNave;
import enums.Orientacion;
import enums.TipoNave;
import java.util.ArrayList;
import java.util.List;
import model.valueObject.Casilla;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class Nave {
    private TipoNave tipo;
    private int impactos;
    private Orientacion orientacion;
    private boolean colocada;
    private final List<Casilla> casillasOcupadas;

    public Nave(TipoNave tipo, Orientacion orientacion) {
        this.tipo = tipo;
        this.orientacion = orientacion;
        this.impactos = 0;
        this.colocada = false;
        this.casillasOcupadas = new ArrayList<>();
    }

    public boolean recibirDisparo() {
        this.impactos++;
        return estaHundida();
    }

    public boolean estaHundida() {
        return impactos >= tipo.getTamano();
    }

    public EstadoNave getEstado() {
        if (estaHundida()) return EstadoNave.HUNDIDA;
        return impactos > 0 ? EstadoNave.DAÑADA : EstadoNave.SIN_DAÑOS;
    }

    public void agregarCasilla(Casilla casilla) { this.casillasOcupadas.add(casilla); }
    public boolean ocupaCasilla(Casilla casilla) { return casillasOcupadas.contains(casilla); }
    public TipoNave getTipo() { return tipo; }
    public Orientacion getOrientacion() { return orientacion; }
    public boolean isColocada() { return colocada; }
    public void setColocada(boolean colocada) { this.colocada = colocada; }
    public List<Casilla> getCasillasOcupadas() { return casillasOcupadas; }
}
