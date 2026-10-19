package model;

import model.enums.TipoNave;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class ResumenFlota {
    private static final ConteoNaves CERO = new ConteoNaves(0, 0, 0);
    private final Map<TipoNave, ConteoNaves> porTipo;

    public ResumenFlota(Map<TipoNave, ConteoNaves> datos) {
        EnumMap<TipoNave, ConteoNaves> copia = new EnumMap<>(TipoNave.class);
        copia.putAll(datos);
        this.porTipo = Collections.unmodifiableMap(copia);
    }

    public static ResumenFlota vacio() {
        return new ResumenFlota(Collections.emptyMap());
    }

    public ConteoNaves get(TipoNave tipo) {
        return porTipo.getOrDefault(tipo, CERO);
    }
}
