/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

import enums.EstadoCasilla;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public record TableroDTO(EstadoCasilla[][] estados, List<CoordenadaDTO> casillasConNave) implements Serializable {}