/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

import enums.Orientacion;
import enums.TipoNave;
import java.io.Serializable;


/**
 * Representación de una nave para la UI y la red.
 * id y colocada se agregan para el CU Colocar Naves (Nave.id / Nave.colocada del diagrama de clases).
 * Si la nave no está colocada, filaInicio y colInicio valen -1.
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public record NaveDTO(int id, TipoNave tipo, Orientacion orientacion, int filaInicio, int colInicio, boolean colocada)
        implements Serializable {

    public NaveDTO(TipoNave tipo, Orientacion orientacion, int filaInicio, int colInicio) {
        this(-1, tipo, orientacion, filaInicio, colInicio, true);
    }
}