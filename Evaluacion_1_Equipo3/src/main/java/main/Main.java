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

            // --- BARCOS DUMMY EN EL MINIMAPA DEL JUGADOR 1 ---
            Tablero tableroPropio = j1.getTableroPropio();

            Nave portaaviones = new Nave(TipoNave.PORTAAVION, Orientacion.VERTICAL);
            portaaviones.agregarCasilla(tableroPropio.obtenerCasilla(1, 1));
            portaaviones.agregarCasilla(tableroPropio.obtenerCasilla(2, 1));
            portaaviones.agregarCasilla(tableroPropio.obtenerCasilla(3, 1));
            portaaviones.agregarCasilla(tableroPropio.obtenerCasilla(4, 1));
            tableroPropio.agregarNave(portaaviones);

            Nave submarino = new Nave(TipoNave.SUBMARINO, Orientacion.HORIZONTAL);
            submarino.agregarCasilla(tableroPropio.obtenerCasilla(6, 4));
            submarino.agregarCasilla(tableroPropio.obtenerCasilla(6, 5));
            tableroPropio.agregarNave(submarino);

            IControlJuego controlador = new ControlJuego(partida);

            PantallaDisparar pantalla = new PantallaDisparar(controlador, j1);

            partida.addObserver(pantalla);

            pantalla.setVisible(true);

            partida.notifyObservers();
        });
    }
}
