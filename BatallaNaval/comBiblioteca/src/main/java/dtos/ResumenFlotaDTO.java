/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

import enums.TipoNave;
import java.io.Serializable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public final class ResumenFlotaDTO implements Serializable{
    private static final ConteoNavesDTO CERO = new ConteoNavesDTO(0, 0, 0);
    private final Map<TipoNave, ConteoNavesDTO> porTipo;

    public ResumenFlotaDTO(Map<TipoNave, ConteoNavesDTO> datos) {
        EnumMap<TipoNave, ConteoNavesDTO> copia = new EnumMap<>(TipoNave.class);
        if (datos != null) {
            copia.putAll(datos);
        }
        this.porTipo = Collections.unmodifiableMap(copia);
    }

    public static ResumenFlotaDTO vacio() {
        return new ResumenFlotaDTO(Collections.emptyMap());
    }

    public ConteoNavesDTO get(TipoNave tipo) {
        return porTipo.getOrDefault(tipo, CERO);
    }
}
