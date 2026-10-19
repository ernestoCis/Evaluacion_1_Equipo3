/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package main;

import controller.ControlJuego;
import controller.IControlJuego;
import javax.swing.SwingUtilities;
import model.Jugador;
import model.Nave;
import model.Partida;
import model.Tablero;
import model.enums.Orientacion;
import model.enums.TipoNave;
import view.PantallaDisparar;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            Partida partida = new Partida();

            Jugador j1 = new Jugador("Jugador 1", "Azul");
            Jugador j2 = new Jugador("Jugador 2", "Rojo");

            partida.agregarJugador(j1);
            partida.agregarJugador(j2);

            // --- Flota del Jugador 1 (11 naves) ---
            Tablero tableroPropio = j1.getTableroPropio();
            tableroPropio.colocarNave(new Nave(TipoNave.PORTAAVION, Orientacion.VERTICAL), 0, 0);   // A1-A4
            tableroPropio.colocarNave(new Nave(TipoNave.PORTAAVION, Orientacion.VERTICAL), 5, 9);   // J6-J9
            tableroPropio.colocarNave(new Nave(TipoNave.CRUCERO, Orientacion.HORIZONTAL), 1, 3);    // D2-F2
            tableroPropio.colocarNave(new Nave(TipoNave.CRUCERO, Orientacion.HORIZONTAL), 8, 2);    // C9-E9
            tableroPropio.colocarNave(new Nave(TipoNave.SUBMARINO, Orientacion.HORIZONTAL), 3, 7);  // H4-I4
            tableroPropio.colocarNave(new Nave(TipoNave.SUBMARINO, Orientacion.VERTICAL), 5, 2);    // C6-C7
            tableroPropio.colocarNave(new Nave(TipoNave.SUBMARINO, Orientacion.HORIZONTAL), 5, 5);  // F6-G6
            tableroPropio.colocarNave(new Nave(TipoNave.SUBMARINO, Orientacion.VERTICAL), 0, 8);    // I1-I2
            tableroPropio.colocarNave(new Nave(TipoNave.BARCO, Orientacion.HORIZONTAL), 6, 0);      // A7
            tableroPropio.colocarNave(new Nave(TipoNave.BARCO, Orientacion.HORIZONTAL), 9, 6);      // G10
            tableroPropio.colocarNave(new Nave(TipoNave.BARCO, Orientacion.HORIZONTAL), 3, 4);      // E4

            // --- Flota del Jugador 2 / rival (11 naves) ---
            Tablero tableroRival = j2.getTableroPropio();
            tableroRival.colocarNave(new Nave(TipoNave.PORTAAVION, Orientacion.HORIZONTAL), 0, 0);  // A1-D1
            tableroRival.colocarNave(new Nave(TipoNave.PORTAAVION, Orientacion.HORIZONTAL), 0, 5);  // F1-I1
            tableroRival.colocarNave(new Nave(TipoNave.CRUCERO, Orientacion.VERTICAL), 2, 0);       // A3-A5
            tableroRival.colocarNave(new Nave(TipoNave.CRUCERO, Orientacion.VERTICAL), 2, 9);       // J3-J5
            tableroRival.colocarNave(new Nave(TipoNave.SUBMARINO, Orientacion.HORIZONTAL), 2, 2);   // C3-D3
            tableroRival.colocarNave(new Nave(TipoNave.SUBMARINO, Orientacion.HORIZONTAL), 2, 5);   // F3-G3
            tableroRival.colocarNave(new Nave(TipoNave.SUBMARINO, Orientacion.VERTICAL), 6, 2);     // C7-C8
            tableroRival.colocarNave(new Nave(TipoNave.SUBMARINO, Orientacion.VERTICAL), 6, 7);     // H7-H8
            tableroRival.colocarNave(new Nave(TipoNave.BARCO, Orientacion.HORIZONTAL), 9, 0);       // A10
            tableroRival.colocarNave(new Nave(TipoNave.BARCO, Orientacion.HORIZONTAL), 9, 5);       // F10
            tableroRival.colocarNave(new Nave(TipoNave.BARCO, Orientacion.HORIZONTAL), 9, 9);       // J10

            // Crear Controlador y Pantalla
            IControlJuego controlador = new ControlJuego(partida);
            PantallaDisparar pantalla = new PantallaDisparar(controlador, j1);

            partida.addObserver(pantalla);
            pantalla.setVisible(true);

            controlador.iniciarPartida(); 
        });
    }
}
