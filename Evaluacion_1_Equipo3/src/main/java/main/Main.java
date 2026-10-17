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

            Tablero tableroPropio = j1.getTableroPropio();
            tableroPropio.colocarNave(new Nave(TipoNave.PORTAAVION, Orientacion.VERTICAL), 1, 1);
            tableroPropio.colocarNave(new Nave(TipoNave.SUBMARINO, Orientacion.HORIZONTAL), 6, 4);

            Tablero tableroRival = j2.getTableroPropio();

            // Portaaviones (4 casillas) -> Horizontal en A1, B1, C1, D1
            tableroRival.colocarNave(new Nave(TipoNave.PORTAAVION, Orientacion.HORIZONTAL), 0, 0);

            // Crucero (3 casillas) -> Vertical en C4, C5, C6
            tableroRival.colocarNave(new Nave(TipoNave.CRUCERO, Orientacion.VERTICAL), 3, 2);

            // Submarino (2 casillas) -> Vertical en E8, E9
            tableroRival.colocarNave(new Nave(TipoNave.SUBMARINO, Orientacion.VERTICAL), 7, 4);

            // Barco (1 casilla) -> Horizontal en H3
            tableroRival.colocarNave(new Nave(TipoNave.BARCO, Orientacion.HORIZONTAL), 2, 7);


            // Crear Controlador y Pantalla
            IControlJuego controlador = new ControlJuego(partida);
            PantallaDisparar pantalla = new PantallaDisparar(controlador, j1);

            partida.addObserver(pantalla);
            pantalla.setVisible(true);

            partida.notifyObservers();
        });
    }
}
