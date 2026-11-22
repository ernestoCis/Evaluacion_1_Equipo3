/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

import enums.Orientacion;
import enums.TipoNave;
import java.io.Serializable;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public record NaveDTO(TipoNave tipo, Orientacion orientacion, int filaInicio, int colInicio) implements Serializable {}
