/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.valueObject.Disparo;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class Jugador {
    private final String id;
    private String nombre;
    private String color;
    private int puntajePartida;
    private final Tablero tableroPropio;
    private final Tablero tableroAtaque;
    private final List<Disparo> disparosRealizados;

    public Jugador(String id, String nombre, String color) {
        this.id = id;
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

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public int getPuntajePartida() { return puntajePartida; }
    public void setPuntajePartida(int puntajePartida) { this.puntajePartida = puntajePartida; }
    public Tablero getTableroPropio() { return tableroPropio; }
    public Tablero getTableroAtaque() { return tableroAtaque; }
    public List<Disparo> getDisparosRealizados() { return disparosRealizados; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Jugador jugador)) return false;
        return Objects.equals(id, jugador.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
