/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.commain;

import application.ControlJuego;
import enums.Orientacion;
import enums.TipoNave;
import eventbus.EventBus;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import model.aggregate.Partida;
import model.entity.Jugador;
import model.entity.Nave;
import model.entity.Tablero;
import red.GestorRedSocket;
import red.ISocket;
import ui.AlertasView;
import ui.PantallaDisparar;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class Main {

    private static final int PUERTO_TCP = 5000;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String[] opciones = {"PC Jugador 1 (Servidor :5000)", "PC Jugador 2 (Cliente :5000)"};
            int seleccion = JOptionPane.showOptionDialog(
                    null,
                    "Selecciona el rol de este equipo en la partida:",
                    "Despliegue Batalla Naval - TCP/IP",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (seleccion == -1) System.exit(0);
            boolean esServidor = (seleccion == 0);

            String ipServidor = "127.0.0.1";
            if (!esServidor) {
                ipServidor = JOptionPane.showInputDialog(null, "Ingresa la IP del PC Jugador 1:", "127.0.0.1");
                if (ipServidor == null || ipServidor.isBlank()) System.exit(0);
            }

            // Infraestructura (EventBus) y Red (Socket TCP/IP 5000)
            EventBus eventBus = new EventBus();
            ISocket gestorRed = new GestorRedSocket(eventBus, eventBus);

            //mDominio (11 naves)
            Partida partida = new Partida();
            Jugador jugadorLocal = esServidor
                    ? crearJugador1ConFlota()
                    : crearJugador2ConFlota();
            partida.agregarJugador(jugadorLocal);

            // Aplicación
            ControlJuego controlJuego = new ControlJuego(partida, jugadorLocal.getId(), esServidor, eventBus, eventBus);

            // UI
            PantallaDisparar pantalla = new PantallaDisparar(controlJuego, eventBus, esServidor);
            new AlertasView(pantalla, controlJuego.getJugadorLocalDTO(), eventBus);
            pantalla.setVisible(true);

            // Conexión TCP/IP en segundo plano para no congelar Swing
            final String hostFinal = ipServidor;
            new Thread(() -> {
                try {
                    if (esServidor) {
                        gestorRed.iniciarServidor(PUERTO_TCP);
                    } else {
                        gestorRed.conectarCliente(hostFinal, PUERTO_TCP);
                        // El cliente envía su flota al conectarse para sincronizar la partida
                        controlJuego.enviarFlotaInicialPorRed();
                    }
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                            pantalla, "Error de conexión TCP/IP: " + ex.getMessage(), "Error Red", JOptionPane.ERROR_MESSAGE
                    ));
                }
            }, "Hilo-Conexion-Red").start();
        });
    }

    private static Jugador crearJugador1ConFlota() {
        Jugador j1 = new Jugador("J1", "Jugador 1", "Azul");
        Tablero t = j1.getTableroPropio();
        t.colocarNave(new Nave(TipoNave.PORTAAVION, Orientacion.VERTICAL), 0, 0);
        t.colocarNave(new Nave(TipoNave.PORTAAVION, Orientacion.VERTICAL), 5, 9);
        t.colocarNave(new Nave(TipoNave.CRUCERO, Orientacion.HORIZONTAL), 1, 3);
        t.colocarNave(new Nave(TipoNave.CRUCERO, Orientacion.HORIZONTAL), 8, 2);
        t.colocarNave(new Nave(TipoNave.SUBMARINO, Orientacion.HORIZONTAL), 3, 7);
        t.colocarNave(new Nave(TipoNave.SUBMARINO, Orientacion.VERTICAL), 5, 2);
        t.colocarNave(new Nave(TipoNave.SUBMARINO, Orientacion.HORIZONTAL), 5, 5);
        t.colocarNave(new Nave(TipoNave.SUBMARINO, Orientacion.VERTICAL), 0, 8);
        t.colocarNave(new Nave(TipoNave.BARCO, Orientacion.HORIZONTAL), 6, 0);
        t.colocarNave(new Nave(TipoNave.BARCO, Orientacion.HORIZONTAL), 9, 6);
        t.colocarNave(new Nave(TipoNave.BARCO, Orientacion.HORIZONTAL), 3, 4);
        return j1;
    }

    private static Jugador crearJugador2ConFlota() {
        Jugador j2 = new Jugador("J2", "Jugador 2", "Rojo");
        Tablero t = j2.getTableroPropio();
        t.colocarNave(new Nave(TipoNave.PORTAAVION, Orientacion.HORIZONTAL), 0, 0);
        t.colocarNave(new Nave(TipoNave.PORTAAVION, Orientacion.HORIZONTAL), 0, 5);
        t.colocarNave(new Nave(TipoNave.CRUCERO, Orientacion.VERTICAL), 2, 0);
        t.colocarNave(new Nave(TipoNave.CRUCERO, Orientacion.VERTICAL), 2, 9);
        t.colocarNave(new Nave(TipoNave.SUBMARINO, Orientacion.HORIZONTAL), 2, 2);
        t.colocarNave(new Nave(TipoNave.SUBMARINO, Orientacion.HORIZONTAL), 2, 5);
        t.colocarNave(new Nave(TipoNave.SUBMARINO, Orientacion.VERTICAL), 6, 2);
        t.colocarNave(new Nave(TipoNave.SUBMARINO, Orientacion.VERTICAL), 6, 7);
        t.colocarNave(new Nave(TipoNave.BARCO, Orientacion.HORIZONTAL), 9, 0);
        t.colocarNave(new Nave(TipoNave.BARCO, Orientacion.HORIZONTAL), 9, 5);
        t.colocarNave(new Nave(TipoNave.BARCO, Orientacion.HORIZONTAL), 9, 9);
        return j2;
    }
}
