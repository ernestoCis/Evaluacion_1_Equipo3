/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import dtos.JugadorDTO;
import enums.ResultadoDisparo;
import eventbus.IEventSubscriber;
import events.DisparoProcesadoEvent;
import events.PartidaFinalizadaEvent;
import events.TurnoCambiadoEvent;
import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class AlertasView {

    private final Component parentComponent;
    private final JugadorDTO jugadorLocal;

    public AlertasView(Component parentComponent, JugadorDTO jugadorLocal, IEventSubscriber subscriber) {
        this.parentComponent = parentComponent;
        this.jugadorLocal = jugadorLocal;
        registrarSuscriptoresEventos(subscriber);
    }

    private void registrarSuscriptoresEventos(IEventSubscriber subscriber) {
        subscriber.suscribir(DisparoProcesadoEvent.class, event -> SwingUtilities.invokeLater(() -> {
            if (!event.idJugadorDisparador().equals(jugadorLocal.id())) return;

            if (event.resultado() == ResultadoDisparo.HUNDIDA) {
                JOptionPane.showMessageDialog(parentComponent,
                        "¡Nave enemiga completamente destruida! (+1 punto)",
                        "Alerta Impacto Nave",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if (event.mensajeAviso() != null) {
                JOptionPane.showMessageDialog(parentComponent,
                        event.mensajeAviso(),
                        "Aviso de turno",
                        JOptionPane.WARNING_MESSAGE);
            }
        }));

        subscriber.suscribir(TurnoCambiadoEvent.class, event -> SwingUtilities.invokeLater(() -> {
            if (event.mensajeAviso() != null && event.idNuevoJugadorTurno().equals(jugadorLocal.id())) {
                JOptionPane.showMessageDialog(parentComponent,
                        event.mensajeAviso(),
                        "Tiempo Agotado",
                        JOptionPane.WARNING_MESSAGE);
            }
        }));

        subscriber.suscribir(PartidaFinalizadaEvent.class, event -> SwingUtilities.invokeLater(() -> {
            boolean gano = event.idGanador() != null && event.idGanador().equals(jugadorLocal.id());
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
        }));
    }
}
