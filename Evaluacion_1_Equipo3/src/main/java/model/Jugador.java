/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class Jugador{
    private String nombre;
    private String color;
    private int puntajePartida;
    private Tablero tableroPropio;
    private Tablero tableroAtaque;
    private List<Disparo> disparosRealizados;

    public Jugador(String nombre, String color) {
        this.nombre = nombre;
        this.color = color;
        this.puntajePartida = 0;
        this.tableroPropio = new Tablero(10, 10);
        this.tableroAtaque = new Tablero(10, 10);
        this.disparosRealizados = new ArrayList<>();
    }

    public void realizarDisparo(Disparo disparo) {
        this.disparosRealizados.add(disparo);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getPuntajePartida() {
        return puntajePartida;
    }

    public void setPuntajePartida(int puntajePartida) {
        this.puntajePartida = puntajePartida;
    }

    public Tablero getTableroPropio() {
        return tableroPropio;
    }

    public void setTableroPropio(Tablero tableroPropio) {
        this.tableroPropio = tableroPropio;
    }

    public Tablero getTableroAtaque() {
        return tableroAtaque;
    }

    public void setTableroAtaque(Tablero tableroAtaque) {
        this.tableroAtaque = tableroAtaque;
    }

    public List<Disparo> getDisparosRealizados() {
        return disparosRealizados;
    }

    public void setDisparosRealizados(List<Disparo> disparosRealizados) {
        this.disparosRealizados = disparosRealizados;
    }
}
