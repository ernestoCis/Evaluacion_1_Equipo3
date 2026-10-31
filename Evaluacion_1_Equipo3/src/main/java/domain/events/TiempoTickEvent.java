/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain.events;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public record TiempoTickEvent(int tiempoRestante, long timestamp) implements DomainEvent {

    public TiempoTickEvent(int tiempoRestante) {
        this(tiempoRestante, System.currentTimeMillis());
    }
}
