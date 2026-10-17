/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.IControlJuego;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import model.Casilla;
import model.IModel;
import model.Jugador;
import model.Tablero;
import model.enums.EstadoCasilla;
import model.enums.EstadoPartida;
import observer.IObservador;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class PantallaDisparar extends JFrame implements IObservador{
    // --- Control y Datos Locales ---
    private final IControlJuego control;
    private final Jugador jugadorLocal;
    
    private int filaSeleccionada = -1;
    private int colSeleccionada = -1;

    private JButton btnAbandonar;
    private JLabel lblMarcadorAzul;
    private JLabel lblMarcadorRojo;
    private JLabel lblTiempo;
    private JLabel lblJugadorNombre;
    private JLabel lblJugadorTurno;

    private JPanel[][] miniGridPropio = new JPanel[10][10];
    private JButton[][] gridBotonesOp = new JButton[10][10];

    private JLabel lblPortaaviones;
    private JLabel lblCruceros;
    private JLabel lblSubmarinos;
    private JLabel lblBarcos;

    private JLabel lblInstruccionPill;
    private JButton btnDisparar;

    private final Color COLOR_FONDO = new Color(133, 187, 235);        // Azul claro de fondo
    private final Color COLOR_BARRA_INFERIOR = new Color(13, 44, 76);  // Azul marino oscuro
    private final Color COLOR_PANEL_DERECHO = new Color(74, 142, 214);  // Azul medio
    private final Color COLOR_BOTON_ROJO = new Color(225, 45, 45);     // Botón abandonar
    private final Color COLOR_BOTON_VERDE = new Color(40, 167, 69);    // Pill indicador
    private final Color COLOR_TEMPORIZADOR = new Color(220, 180, 0);   // Amarrillo temporizador
    private final Color COLOR_GRIS_BOTON = new Color(128, 128, 128);   // Botón Disparar inactivo

    public PantallaDisparar(IControlJuego control, Jugador jugadorLocal) {
        this.control = control;
        this.jugadorLocal = jugadorLocal;

        initUI();
    }

    private void initUI() {
        setTitle("Pantalla de Disparo - Batalla Naval");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_FONDO);

        mainPanel.add(crearHeaderPanel(), BorderLayout.NORTH);

        mainPanel.add(crearCuerpoPanel(), BorderLayout.CENTER);

        mainPanel.add(crearBarraInferiorPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel crearHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        btnAbandonar = new JButton("Abandonar partida");
        btnAbandonar.setFont(new Font("Arial", Font.BOLD, 12));
        btnAbandonar.setForeground(Color.WHITE);
        btnAbandonar.setBackground(COLOR_BOTON_ROJO);
        btnAbandonar.setFocusPainted(false);
        btnAbandonar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1, true),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btnAbandonar.addActionListener(e -> accionAbandonar());

        JPanel pnlIzquierda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlIzquierda.setOpaque(false);
        pnlIzquierda.add(btnAbandonar);

        JPanel pnlCentro = new JPanel();
        pnlCentro.setLayout(new BoxLayout(pnlCentro, BoxLayout.Y_AXIS));
        pnlCentro.setOpaque(false);

        JPanel pnlMarcador = new JPanel(new GridLayout(1, 2));
        pnlMarcador.setPreferredSize(new Dimension(160, 45));

        lblMarcadorAzul = new JLabel("0", SwingConstants.CENTER);
        lblMarcadorAzul.setFont(new Font("Impact", Font.BOLD, 28));
        lblMarcadorAzul.setForeground(Color.WHITE);
        lblMarcadorAzul.setOpaque(true);
        lblMarcadorAzul.setBackground(Color.BLUE);

        JLabel lblGuion = new JLabel("-", SwingConstants.CENTER);
        lblGuion.setFont(new Font("Impact", Font.BOLD, 28));
        lblGuion.setForeground(Color.WHITE);

        lblMarcadorRojo = new JLabel("0", SwingConstants.CENTER);
        lblMarcadorRojo.setFont(new Font("Impact", Font.BOLD, 28));
        lblMarcadorRojo.setForeground(Color.WHITE);
        lblMarcadorRojo.setOpaque(true);
        lblMarcadorRojo.setBackground(Color.RED);

        JPanel pnlAzul = new JPanel(new BorderLayout());
        pnlAzul.setBackground(new Color(0, 51, 204));
        pnlAzul.add(lblMarcadorAzul, BorderLayout.CENTER);

        JPanel pnlRojo = new JPanel(new BorderLayout());
        pnlRojo.setBackground(new Color(204, 0, 0));
        pnlRojo.add(lblMarcadorRojo, BorderLayout.CENTER);

        JPanel pnlMarcadorBox = new JPanel(new GridLayout(1, 2));
        pnlMarcadorBox.add(pnlAzul);
        pnlMarcadorBox.add(pnlRojo);

        lblTiempo = new JLabel("00:30", SwingConstants.CENTER);
        lblTiempo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTiempo.setForeground(Color.WHITE);
        lblTiempo.setOpaque(true);
        lblTiempo.setBackground(COLOR_TEMPORIZADOR);
        lblTiempo.setPreferredSize(new Dimension(100, 22));
        lblTiempo.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlCentro.add(pnlMarcadorBox);
        pnlCentro.add(Box.createVerticalStrut(3));
        pnlCentro.add(lblTiempo);

        JPanel pnlDerecha = new JPanel();
        pnlDerecha.setLayout(new BoxLayout(pnlDerecha, BoxLayout.Y_AXIS));
        pnlDerecha.setOpaque(false);

        lblJugadorNombre = new JLabel(jugadorLocal.getNombre() != null ? jugadorLocal.getNombre() : "Jugador 1");
        lblJugadorNombre.setFont(new Font("Impact", Font.PLAIN, 32));
        lblJugadorNombre.setForeground(Color.WHITE);

        lblJugadorTurno = new JLabel("Tu turno");
        lblJugadorTurno.setFont(new Font("Arial", Font.BOLD, 12));
        lblJugadorTurno.setForeground(Color.WHITE);

        pnlDerecha.add(lblJugadorNombre);
        pnlDerecha.add(lblJugadorTurno);

        headerPanel.add(pnlIzquierda, BorderLayout.WEST);
        headerPanel.add(pnlCentro, BorderLayout.CENTER);
        headerPanel.add(pnlDerecha, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel crearCuerpoPanel() {
        JPanel cuerpoPanel = new JPanel(new BorderLayout(15, 0));
        cuerpoPanel.setOpaque(false);
        cuerpoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        cuerpoPanel.add(crearPanelMinimapa(), BorderLayout.WEST);

        cuerpoPanel.add(crearPanelTableroPrincipal(), BorderLayout.CENTER);

        cuerpoPanel.add(crearPanelEstadoDerecho(), BorderLayout.EAST);

        return cuerpoPanel;
    }

    private JPanel crearPanelMinimapa() {
        JPanel pnlMinimapaContenedor = new JPanel();
        pnlMinimapaContenedor.setLayout(new BoxLayout(pnlMinimapaContenedor, BoxLayout.Y_AXIS));
        pnlMinimapaContenedor.setOpaque(false);

        JPanel pnlGridMini = new JPanel(new GridLayout(10, 10, 1, 1));
        pnlGridMini.setPreferredSize(new Dimension(180, 180));
        pnlGridMini.setBackground(Color.WHITE);
        pnlGridMini.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        for (int f = 0; f < 10; f++) {
            for (int c = 0; c < 10; c++) {
                JPanel cell = new JPanel();
                cell.setBackground(new Color(130, 180, 230)); // Agua por defecto
                cell.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 120), 1));
                miniGridPropio[f][c] = cell;
                pnlGridMini.add(cell);
            }
        }

        JLabel lblTituloMini = new JLabel("Tu tablero", SwingConstants.CENTER);
        lblTituloMini.setFont(new Font("Arial", Font.BOLD, 16));
        lblTituloMini.setForeground(Color.WHITE);
        lblTituloMini.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlMinimapaContenedor.add(Box.createVerticalStrut(20));
        pnlMinimapaContenedor.add(pnlGridMini);
        pnlMinimapaContenedor.add(Box.createVerticalStrut(8));
        pnlMinimapaContenedor.add(lblTituloMini);

        return pnlMinimapaContenedor;
    }

    private JPanel crearPanelTableroPrincipal() {
        JPanel pnlPrincipalContenedor = new JPanel(new BorderLayout());
        pnlPrincipalContenedor.setOpaque(false);

        JPanel pnlColumnas = new JPanel(new GridLayout(1, 11));
        pnlColumnas.setOpaque(false);
        pnlColumnas.add(new JLabel("")); // Esquina vacía
        String[] letras = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        for (String l : letras) {
            JLabel lbl = new JLabel(l, SwingConstants.CENTER);
            lbl.setFont(new Font("Impact", Font.PLAIN, 22));
            lbl.setForeground(Color.WHITE);
            pnlColumnas.add(lbl);
        }

        JPanel pnlFilas = new JPanel(new GridLayout(10, 1));
        pnlFilas.setOpaque(false);
        pnlFilas.setPreferredSize(new Dimension(30, 0));
        for (int i = 1; i <= 10; i++) {
            JLabel lbl = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            lbl.setFont(new Font("Impact", Font.PLAIN, 22));
            lbl.setForeground(Color.WHITE);
            pnlFilas.add(lbl);
        }

        JPanel pnlGridOp = new JPanel(new GridLayout(10, 10, 2, 2));
        pnlGridOp.setOpaque(false);

        for (int f = 0; f < 10; f++) {
            final int fila = f;
            for (int c = 0; c < 10; c++) {
                final int col = c;
                JButton btn = new JButton();
                btn.setBackground(new Color(110, 165, 225));
                btn.setBorder(new LineBorder(Color.WHITE, 1));
                btn.setFocusPainted(false);

                btn.addActionListener(e -> seleccionarCasilla(fila, col));

                gridBotonesOp[f][c] = btn;
                pnlGridOp.add(btn);
            }
        }

        JLabel lblTituloTablero = new JLabel("Tablero de Jugador 2", SwingConstants.CENTER);
        lblTituloTablero.setFont(new Font("Arial", Font.BOLD, 15));
        lblTituloTablero.setForeground(Color.WHITE);
        lblTituloTablero.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        pnlPrincipalContenedor.add(pnlColumnas, BorderLayout.NORTH);
        pnlPrincipalContenedor.add(pnlFilas, BorderLayout.WEST);
        pnlPrincipalContenedor.add(pnlGridOp, BorderLayout.CENTER);
        pnlPrincipalContenedor.add(lblTituloTablero, BorderLayout.SOUTH);

        return pnlPrincipalContenedor;
    }

    private JPanel crearPanelEstadoDerecho() {
        JPanel pnlDerecho = new JPanel(new BorderLayout());
        pnlDerecho.setOpaque(false);
        pnlDerecho.setPreferredSize(new Dimension(230, 0));

        JPanel pnlListaBarcos = new JPanel();
        pnlListaBarcos.setLayout(new BoxLayout(pnlListaBarcos, BoxLayout.Y_AXIS));
        pnlListaBarcos.setBackground(COLOR_PANEL_DERECHO);
        pnlListaBarcos.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblPortaaviones = crearLabelNave("-Porta aviones:\n 2 sin daños");
        lblCruceros = crearLabelNave("-Cruceros:\n 2 sin daños");
        lblSubmarinos = crearLabelNave("-Submarinos:\n 4 sin daños");
        lblBarcos = crearLabelNave("-Barcos:\n 3 sin daños");

        pnlListaBarcos.add(lblPortaaviones);
        pnlListaBarcos.add(Box.createVerticalStrut(15));
        pnlListaBarcos.add(lblCruceros);
        pnlListaBarcos.add(Box.createVerticalStrut(15));
        pnlListaBarcos.add(lblSubmarinos);
        pnlListaBarcos.add(Box.createVerticalStrut(15));
        pnlListaBarcos.add(lblBarcos);

        lblInstruccionPill = new JLabel("<html><center>Selecciona una casilla<br>para disparar</center></html>", SwingConstants.CENTER);
        lblInstruccionPill.setFont(new Font("Arial", Font.BOLD, 12));
        lblInstruccionPill.setForeground(Color.WHITE);
        lblInstruccionPill.setOpaque(true);
        lblInstruccionPill.setBackground(COLOR_BOTON_VERDE);
        lblInstruccionPill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel pnlBottomPill = new JPanel(new BorderLayout());
        pnlBottomPill.setOpaque(false);
        pnlBottomPill.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        pnlBottomPill.add(lblInstruccionPill, BorderLayout.CENTER);

        pnlDerecho.add(pnlListaBarcos, BorderLayout.CENTER);
        pnlDerecho.add(pnlBottomPill, BorderLayout.SOUTH);

        return pnlDerecho;
    }

    private JLabel crearLabelNave(String texto) {
        JLabel lbl = new JLabel("<html>" + texto.replaceAll("\n", "<br>") + "</html>");
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    private JPanel crearBarraInferiorPanel() {
        JPanel pnlBarraInferior = new JPanel(new GridBagLayout());
        pnlBarraInferior.setBackground(COLOR_BARRA_INFERIOR);
        pnlBarraInferior.setPreferredSize(new Dimension(0, 80));

        btnDisparar = new JButton("Disparar");
        btnDisparar.setFont(new Font("Impact", Font.PLAIN, 28));
        btnDisparar.setForeground(Color.WHITE);
        btnDisparar.setBackground(COLOR_GRIS_BOTON);
        btnDisparar.setFocusPainted(false);
        btnDisparar.setPreferredSize(new Dimension(250, 50));
        btnDisparar.setEnabled(false); // Inactivo hasta seleccionar una casilla

        btnDisparar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2, true),
                BorderFactory.createEmptyBorder(5, 20, 5, 20)
        ));

        btnDisparar.addActionListener(e -> accionEjecutarDisparo());

        pnlBarraInferior.add(btnDisparar);

        return pnlBarraInferior;
    }

    private void seleccionarCasilla(int f, int c) {
        if (filaSeleccionada != -1 && colSeleccionada != -1) {
            gridBotonesOp[filaSeleccionada][colSeleccionada].setBorder(new LineBorder(Color.WHITE, 1));
        }

        filaSeleccionada = f;
        colSeleccionada = c;

        gridBotonesOp[f][c].setBorder(new LineBorder(Color.YELLOW, 3));

        btnDisparar.setEnabled(true);
        btnDisparar.setBackground(new Color(40, 167, 69)); // Cambia a verde activo
    }

    private void accionEjecutarDisparo() {
        if (filaSeleccionada != -1 && colSeleccionada != -1) {
            control.disparar(jugadorLocal, filaSeleccionada, colSeleccionada);

            btnDisparar.setEnabled(false);
            btnDisparar.setBackground(COLOR_GRIS_BOTON);
            filaSeleccionada = -1;
            colSeleccionada = -1;
        }
    }

    private void accionAbandonar() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que deseas abandonar la partida?",
                "Abandonar partida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (opcion == JOptionPane.YES_OPTION) {
            control.rendirse(jugadorLocal);
        }
    }

    @Override
    public void update(IModel model) {
        SwingUtilities.invokeLater(() -> {
            int t = model.getTiempoRestante();
            lblTiempo.setText(String.format("00:%02d", t));

            boolean esMiTurno = model.getJugadorTurno() != null && model.getJugadorTurno().equals(jugadorLocal);
            lblJugadorTurno.setText(esMiTurno ? "Tu turno" : "Turno del oponente");

            Tablero tableroPropio = model.getTablero(jugadorLocal);
            if (tableroPropio != null) {
                for (int f = 0; f < 10; f++) {
                    for (int c = 0; c < 10; c++) {
                        Casilla casilla = tableroPropio.obtenerCasilla(f, c);
                        if (casilla != null) {
                            if (casilla.obtenerEstado() == EstadoCasilla.IMPACTADA) {
                                miniGridPropio[f][c].setBackground(Color.RED);
                            } else if (casilla.obtenerEstado() == EstadoCasilla.AGUA) {
                                miniGridPropio[f][c].setBackground(Color.CYAN);
                            } else {
                                boolean tieneNave = tableroPropio.getNaves().stream()
                                        .anyMatch(n -> n.ocupaCasilla(casilla));
                                miniGridPropio[f][c].setBackground(tieneNave ? Color.GREEN : new Color(130, 180, 230));
                            }
                        }
                    }
                }
            }

            if (model.getEstado() == EstadoPartida.FINALIZADA) {
                boolean gano = model.getGanador() != null && model.getGanador().equals(jugadorLocal);
                if (gano) {
                    JOptionPane.showMessageDialog(this, "¡VICTORIA! Has destruido la flota enemiga.", "Fin de Partida", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "DERROTA. Tu flota ha sido destruida.", "Fin de Partida", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
