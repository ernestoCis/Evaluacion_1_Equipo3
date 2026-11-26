/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enums;

/**
 * Resultado de la última operación del CU Colocar Naves.
 * Lo fija Partida (setResultado) y viaja dentro de ColocacionActualizadaEvent.
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public enum ResultadoColocacion {
    COLOCADA,
    POSICION_INVALIDA,
    RETIRADA,
    FLOTA_INCOMPLETA,
    TABLERO_CONFIRMADO,
    PARTIDA_INICIADA
}