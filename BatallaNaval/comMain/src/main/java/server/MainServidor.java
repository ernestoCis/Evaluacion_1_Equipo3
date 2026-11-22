/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import application.ControlJuegoServidor;
import application.IControlJuego;
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
public class MainServidor {

    private static final int PUERTO_TCP = 5000;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EventBus eventBus = new EventBus();
            ISocket gestorRed = new GestorRedSocket(eventBus, eventBus);

            Partida partida = new Partida();
            Jugador j1 = crearJugador1ConFlota();
            partida.agregarJugador(j1);

            IControlJuego controlJuego = new ControlJuegoServidor(partida, j1.getId(), eventBus, eventBus);

            PantallaDisparar pantalla = new PantallaDisparar(controlJuego, eventBus, true);
            new AlertasView(pantalla, controlJuego.getJugadorLocalDTO(), eventBus);
            pantalla.setVisible(true);

            new Thread(() -> {
                try {
                    gestorRed.iniciarServidor(PUERTO_TCP);
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                            pantalla, "Error al iniciar servidor: " + ex.getMessage(), "Error Red", JOptionPane.ERROR_MESSAGE
                    ));
                }
            }, "Hilo-Servidor-TCP").start();
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
}
