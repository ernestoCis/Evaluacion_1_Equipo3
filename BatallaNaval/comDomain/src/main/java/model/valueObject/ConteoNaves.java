/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.valueObject;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public final class ConteoNaves {
    private final int sinDanos;
    private final int danadas;
    private final int hundidas;

    public ConteoNaves(int sinDanos, int danadas, int hundidas) {
        this.sinDanos = sinDanos;
        this.danadas = danadas;
        this.hundidas = hundidas;
    }

    public int getSinDanos() { return sinDanos; }
    public int getDanadas() { return danadas; }
    public int getHundidas() { return hundidas; }
}
