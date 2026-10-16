/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package model.enums;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public enum TipoNave {
    PORTAAVION(4),
    CRUCERO(3),
    SUBMARINO(2),
    BARCO(1);

    private final int tamano;

    TipoNave(int tamano) {
        this.tamano = tamano;
    }

    public int getTamano() {
        return tamano;
    }
}
