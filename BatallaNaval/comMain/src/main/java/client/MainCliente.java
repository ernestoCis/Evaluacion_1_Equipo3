/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import application.ControlJuegoCliente;
import dtos.JugadorDTO;
import dtos.NaveDTO;
import enums.Orientacion;
import enums.TipoNave;
import eventbus.EventBus;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import red.GestorRedSocket;
import red.ISocket;
import ui.AlertasView;
import ui.PantallaDisparar;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class MainCliente {

    private static final int PUERTO_TCP = 5000;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String ipServidor = JOptionPane.showInputDialog(null, "Ingresa la IP del PC Jugador 1 (Servidor):", "127.0.0.1");
            if (ipServidor == null || ipServidor.isBlank()) System.exit(0);

            EventBus eventBus = new EventBus();
            ISocket gestorRed = new GestorRedSocket(eventBus, eventBus);

            JugadorDTO j2 = new JugadorDTO("J2", "Jugador 2", "Rojo", 0);
            List<NaveDTO> flotaJ2 = crearFlotaJugador2();

            ControlJuegoCliente controlJuego = new ControlJuegoCliente(j2, flotaJ2, eventBus, eventBus);

            // Nota: PantallaDisparar se crea DESPUÉS de ControlJuegoCliente para que los DTOs se actualicen primero
            PantallaDisparar pantalla = new PantallaDisparar(controlJuego, eventBus, false);
            new AlertasView(pantalla, controlJuego.getJugadorLocalDTO(), eventBus);
            pantalla.setVisible(true);

            final String hostFinal = ipServidor.trim();
            new Thread(() -> {
                try {
                    gestorRed.conectarCliente(hostFinal, PUERTO_TCP);
                    controlJuego.enviarFlotaInicialAlServidor();
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                            pantalla, "No se pudo conectar al servidor: " + ex.getMessage(), "Error Red", JOptionPane.ERROR_MESSAGE
                    ));
                }
            }, "Hilo-Cliente-TCP").start();
        });
    }

    private static List<NaveDTO> crearFlotaJugador2() {
        return List.of(
                new NaveDTO(TipoNave.PORTAAVION, Orientacion.HORIZONTAL, 0, 0),
                new NaveDTO(TipoNave.PORTAAVION, Orientacion.HORIZONTAL, 0, 5),
                new NaveDTO(TipoNave.CRUCERO, Orientacion.VERTICAL, 2, 0),
                new NaveDTO(TipoNave.CRUCERO, Orientacion.VERTICAL, 2, 9),
                new NaveDTO(TipoNave.SUBMARINO, Orientacion.HORIZONTAL, 2, 2),
                new NaveDTO(TipoNave.SUBMARINO, Orientacion.HORIZONTAL, 2, 5),
                new NaveDTO(TipoNave.SUBMARINO, Orientacion.VERTICAL, 6, 2),
                new NaveDTO(TipoNave.SUBMARINO, Orientacion.VERTICAL, 6, 7),
                new NaveDTO(TipoNave.BARCO, Orientacion.HORIZONTAL, 9, 0),
                new NaveDTO(TipoNave.BARCO, Orientacion.HORIZONTAL, 9, 5),
                new NaveDTO(TipoNave.BARCO, Orientacion.HORIZONTAL, 9, 9)
        );
    }
}
