/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;
import java.util.List;
import model.enums.Orientacion;
import model.enums.TipoNave;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class Nave{
    private TipoNave tipo;
    private int impactos;
    private Orientacion orientacion;
    private boolean colocada;
    private List<Casilla> casillasOcupadas;

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

    public void agregarCasilla(Casilla casilla) {
        this.casillasOcupadas.add(casilla);
    }

    public boolean ocupaCasilla(Casilla casilla) {
        return casillasOcupadas.contains(casilla);
    }

    public TipoNave getTipo() {
        return tipo;
    }

    public void setTipo(TipoNave tipo) {
        this.tipo = tipo;
    }

    public int getImpactos() {
        return impactos;
    }

    public void setImpactos(int impactos) {
        this.impactos = impactos;
    }

    public Orientacion getOrientacion() {
        return orientacion;
    }

    public void setOrientacion(Orientacion orientacion) {
        this.orientacion = orientacion;
    }

    public boolean isColocada() {
        return colocada;
    }

    public void setColocada(boolean colocada) {
        this.colocada = colocada;
    }

    public List getCasillasOcupadas() {
        return casillasOcupadas;
    }

    public void setCasillasOcupadas(List casillasOcupadas) {
        this.casillasOcupadas = casillasOcupadas;
    }
}
