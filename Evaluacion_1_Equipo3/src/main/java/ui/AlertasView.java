/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import domain.events.DisparoProcesadoEvent;
import domain.events.PartidaFinalizadaEvent;
import domain.events.TurnoCambiadoEvent;
import domain.model.entity.Jugador;
import domain.model.enums.ResultadoDisparo;
import infrastructure.eventbus.EventBus;
import java.awt.Component;
import javax.swing.JOptionPane;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class AlertasView {

    private final Component parentComponent;
    private final Jugador jugadorLocal;

    public AlertasView(Component parentComponent, Jugador jugadorLocal) {
        this.parentComponent = parentComponent;
        this.jugadorLocal = jugadorLocal;
        registrarSuscriptoresEventos();
    }

    private void registrarSuscriptoresEventos() {
        // Alerta por Nave Hundida
        EventBus.subscribe(DisparoProcesadoEvent.class, event -> {
            if (event.resultado() == ResultadoDisparo.HUNDIDA && event.jugadorDisparador().equals(jugadorLocal)) {
                JOptionPane.showMessageDialog(parentComponent,
                        "¡Nave enemiga completamente destruida! (+1 punto)",
                        "Alerta Impacto Nave",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            if (event.mensajeAviso() != null && event.jugadorDisparador().equals(jugadorLocal)) {
                JOptionPane.showMessageDialog(parentComponent,
                        event.mensajeAviso(),
                        "Aviso de turno",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        // Alerta por cambio de turno provocado por tiempo agotado
        EventBus.subscribe(TurnoCambiadoEvent.class, event -> {
            if (event.mensajeAviso() != null && event.nuevoJugadorTurno().equals(jugadorLocal)) {
                JOptionPane.showMessageDialog(parentComponent,
                        event.mensajeAviso(),
                        "Tiempo Agotado",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        // Alertas de fin de partida (Victoria / Derrota / Abandono)
        EventBus.subscribe(PartidaFinalizadaEvent.class, event -> {
            boolean gano = event.ganador() != null && event.ganador().equals(jugadorLocal);
            if (gano) {
                JOptionPane.showMessageDialog(parentComponent,
                        "¡VICTORIA!\nHundiste la última nave: la flota enemiga fue destruida por completo.",
                        "Fin de Partida",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentComponent,
                        "DERROTA. Tu flota ha sido destruida o te has rendido.",
                        "Fin de Partida",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
