/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import application.IControlJuego;
import dtos.ConteoNavesDTO;
import dtos.CoordenadaDTO;
import dtos.JugadorDTO;
import dtos.ResumenFlotaDTO;
import dtos.TableroDTO;
import enums.EstadoCasilla;
import enums.ResultadoDisparo;
import enums.TipoNave;
import eventbus.IEventSubscriber;
import events.DisparoProcesadoEvent;
import events.PartidaFinalizadaEvent;
import events.PartidaSincronizadaEvent;
import events.TiempoTickEvent;
import events.TurnoCambiadoEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class PantallaDisparar extends JFrame {

    private final IControlJuego control;
    private final JugadorDTO jugadorLocal;
    private boolean esMiTurno;

    private int filaSeleccionada = -1;
    private int colSeleccionada = -1;

    private JButton btnAbandonar;
    private JLabel lblMarcadorAzul;
    private JLabel lblMarcadorRojo;
    private JLabel lblTiempo;
    private JLabel lblJugadorNombre;
    private JLabel lblJugadorTurno;
    private JLabel lblTituloTablero;

    private final JPanel[][] miniGridPropio = new JPanel[10][10];
    private final CasillaBoton[][] gridBotonesOp = new CasillaBoton[10][10];

    private JLabel lblPortaaviones;
    private JLabel lblCruceros;
    private JLabel lblSubmarinos;
    private JLabel lblBarcos;

    private JLabel lblInstruccionPill;
    private JButton btnDisparar;

    private final Color COLOR_FONDO = new Color(133, 187, 235);
    private final Color COLOR_BARRA_INFERIOR = new Color(13, 44, 76);
    private final Color COLOR_PANEL_DERECHO = new Color(74, 142, 214);
    private final Color COLOR_BOTON_ROJO = new Color(225, 45, 45);
    private final Color COLOR_BOTON_VERDE = new Color(40, 167, 69);
    private final Color COLOR_TEMPORIZADOR = new Color(220, 180, 0);
    private final Color COLOR_GRIS_BOTON = new Color(128, 128, 128);

    public PantallaDisparar(IControlJuego control, IEventSubscriber subscriber, boolean iniciaConTurno) {
        this.control = control;
        this.jugadorLocal = control.getJugadorLocalDTO();
        this.esMiTurno = iniciaConTurno;

        initUI();
        registrarSuscriptoresEventos(subscriber);
    }

    private void initUI() {
        setTitle("Pantalla de Disparo - Batalla Naval (" + jugadorLocal.nombre() + ")");
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
        refrescarMinimapa();
        actualizarPanelNaves(control.getResumenFlotaRivalDTO());
    }

    private void registrarSuscriptoresEventos(IEventSubscriber subscriber) {
        subscriber.suscribir(PartidaSincronizadaEvent.class, event -> SwingUtilities.invokeLater(() -> {
            JugadorDTO rival = control.getJugadorRivalDTO();
            if (rival != null) {
                lblTituloTablero.setText("Tablero de " + rival.nombre());
            }
            actualizarPanelNaves(control.getResumenFlotaRivalDTO());
        }));

        subscriber.suscribir(TiempoTickEvent.class, event -> SwingUtilities.invokeLater(() ->
            lblTiempo.setText(String.format("00:%02d", event.tiempoRestante()))
        ));

        subscriber.suscribir(DisparoProcesadoEvent.class, event -> SwingUtilities.invokeLater(() -> {
            boolean dispareYo = event.idJugadorDisparador().equals(jugadorLocal.id());

            if (dispareYo) {
                lblMarcadorAzul.setText(String.valueOf(event.puntajeDisparador()));
                actualizarPanelNaves(event.resumenFlotaRival());

                CasillaBoton btn = gridBotonesOp[event.fila()][event.columna()];
                if (event.resultado() == ResultadoDisparo.AGUA) {
                    btn.setEstadoVisual(CasillaBoton.EstadoVisual.AGUA);
                } else {
                    btn.setEstadoVisual(CasillaBoton.EstadoVisual.IMPACTO_FUEGO);
                }
                btn.setEnabled(false);

                // Pintar TODAS las casillas de la nave hundida en gris ceniza
                if (event.resultado() == ResultadoDisparo.HUNDIDA && event.casillasHundidas() != null) {
                    for (CoordenadaDTO coord : event.casillasHundidas()) {
                        gridBotonesOp[coord.fila()][coord.columna()].setEstadoVisual(CasillaBoton.EstadoVisual.HUNDIDA);
                    }
                }
            } else {
                // El oponente disparó sobre nuestro tablero
                lblMarcadorRojo.setText(String.valueOf(event.puntajeDisparador()));
                refrescarMinimapa();
            }
        }));

        subscriber.suscribir(TurnoCambiadoEvent.class, event -> SwingUtilities.invokeLater(() -> {
            esMiTurno = event.idNuevoJugadorTurno().equals(jugadorLocal.id());
            lblJugadorTurno.setText(esMiTurno ? "Tu turno" : "Turno del oponente");
            if (!esMiTurno) {
                limpiarSeleccion();
            }
        }));

        subscriber.suscribir(PartidaFinalizadaEvent.class, event -> SwingUtilities.invokeLater(() -> {
            esMiTurno = false;
            limpiarSeleccion();
            for (int f = 0; f < 10; f++) {
                for (int c = 0; c < 10; c++) {
                    gridBotonesOp[f][c].setEnabled(false);
                }
            }
            lblJugadorTurno.setText("Partida finalizada");
        }));
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

        Dimension dimMarcador = new Dimension(110, 38);
        JPanel pnlMarcadorBox = new JPanel(new GridLayout(1, 2));
        pnlMarcadorBox.setPreferredSize(dimMarcador);
        pnlMarcadorBox.setMaximumSize(dimMarcador);
        pnlMarcadorBox.setMinimumSize(dimMarcador);
        pnlMarcadorBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblMarcadorAzul = new JLabel("0", SwingConstants.CENTER);
        lblMarcadorAzul.setFont(new Font("Impact", Font.BOLD, 24));
        lblMarcadorAzul.setForeground(Color.WHITE);
        lblMarcadorAzul.setOpaque(true);
        lblMarcadorAzul.setBackground(new Color(0, 51, 204));

        lblMarcadorRojo = new JLabel("0", SwingConstants.CENTER);
        lblMarcadorRojo.setFont(new Font("Impact", Font.BOLD, 24));
        lblMarcadorRojo.setForeground(Color.WHITE);
        lblMarcadorRojo.setOpaque(true);
        lblMarcadorRojo.setBackground(new Color(204, 0, 0));

        pnlMarcadorBox.add(lblMarcadorAzul);
        pnlMarcadorBox.add(lblMarcadorRojo);

        lblTiempo = new JLabel("00:30", SwingConstants.CENTER);
        lblTiempo.setFont(new Font("Impact", Font.PLAIN, 20));
        lblTiempo.setForeground(Color.WHITE);
        lblTiempo.setOpaque(true);
        lblTiempo.setBackground(COLOR_TEMPORIZADOR);

        Dimension dimTiempo = new Dimension(130, 30);
        lblTiempo.setPreferredSize(dimTiempo);
        lblTiempo.setMaximumSize(dimTiempo);
        lblTiempo.setMinimumSize(dimTiempo);
        lblTiempo.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlCentro.add(pnlMarcadorBox);
        pnlCentro.add(Box.createVerticalStrut(4));
        pnlCentro.add(lblTiempo);

        JPanel pnlDerecha = new JPanel();
        pnlDerecha.setLayout(new BoxLayout(pnlDerecha, BoxLayout.Y_AXIS));
        pnlDerecha.setOpaque(false);

        lblJugadorNombre = new JLabel(jugadorLocal.nombre());
        lblJugadorNombre.setFont(new Font("Impact", Font.PLAIN, 32));
        lblJugadorNombre.setForeground(Color.WHITE);

        lblJugadorTurno = new JLabel(esMiTurno ? "Tu turno" : "Turno del oponente");
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
        pnlMinimapaContenedor.setPreferredSize(new Dimension(180, 0));

        Dimension miniSize = new Dimension(160, 160);
        JPanel pnlGridMini = new JPanel(new GridLayout(10, 10, 1, 1));
        pnlGridMini.setPreferredSize(miniSize);
        pnlGridMini.setMaximumSize(miniSize);
        pnlGridMini.setMinimumSize(miniSize);
        pnlGridMini.setBackground(Color.WHITE);
        pnlGridMini.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        for (int f = 0; f < 10; f++) {
            for (int c = 0; c < 10; c++) {
                JPanel cell = new JPanel();
                cell.setBackground(new Color(130, 180, 230));
                cell.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 120), 1));
                miniGridPropio[f][c] = cell;
                pnlGridMini.add(cell);
            }
        }

        JLabel lblTituloMini = new JLabel("Tu tablero", SwingConstants.CENTER);
        lblTituloMini.setFont(new Font("Arial", Font.BOLD, 16));
        lblTituloMini.setForeground(Color.WHITE);
        lblTituloMini.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlGridMini.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlMinimapaContenedor.add(Box.createVerticalGlue());
        pnlMinimapaContenedor.add(pnlGridMini);
        pnlMinimapaContenedor.add(Box.createVerticalStrut(10));
        pnlMinimapaContenedor.add(lblTituloMini);
        pnlMinimapaContenedor.add(Box.createVerticalGlue());

        return pnlMinimapaContenedor;
    }

    private JPanel crearPanelTableroPrincipal() {
        JPanel pnlPrincipalContenedor = new JPanel(new BorderLayout());
        pnlPrincipalContenedor.setOpaque(false);

        JPanel pnlColumnas = new JPanel(new GridLayout(1, 11));
        pnlColumnas.setOpaque(false);
        pnlColumnas.add(new JLabel(""));
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
                CasillaBoton btn = new CasillaBoton();
                btn.addActionListener(e -> seleccionarCasilla(fila, col));
                gridBotonesOp[f][c] = btn;
                pnlGridOp.add(btn);
            }
        }

        JugadorDTO rival = control.getJugadorRivalDTO();
        lblTituloTablero = new JLabel("Tablero de " + (rival != null ? rival.nombre() : "Oponente"), SwingConstants.CENTER);
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

        lblPortaaviones = crearLabelNave("-Porta aviones:");
        lblCruceros = crearLabelNave("-Cruceros:");
        lblSubmarinos = crearLabelNave("-Submarinos:");
        lblBarcos = crearLabelNave("-Barcos:");

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
        btnDisparar.setEnabled(false);
        btnDisparar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2, true),
                BorderFactory.createEmptyBorder(5, 20, 5, 20)
        ));
        btnDisparar.addActionListener(e -> accionEjecutarDisparo());

        pnlBarraInferior.add(btnDisparar);
        return pnlBarraInferior;
    }

    private void seleccionarCasilla(int f, int c) {
        if (!esMiTurno) return;

        if (filaSeleccionada != -1 && colSeleccionada != -1) {
            CasillaBoton prevBtn = gridBotonesOp[filaSeleccionada][colSeleccionada];
            if (prevBtn.getEstadoVisual() == CasillaBoton.EstadoVisual.SELECCIONADA) {
                prevBtn.setEstadoVisual(CasillaBoton.EstadoVisual.NORMAL);
            }
        }

        filaSeleccionada = f;
        colSeleccionada = c;
        gridBotonesOp[f][c].setEstadoVisual(CasillaBoton.EstadoVisual.SELECCIONADA);

        btnDisparar.setEnabled(true);
        btnDisparar.setBackground(COLOR_BOTON_VERDE);
    }

    private void limpiarSeleccion() {
        if (filaSeleccionada != -1 && colSeleccionada != -1) {
            CasillaBoton prevBtn = gridBotonesOp[filaSeleccionada][colSeleccionada];
            if (prevBtn.getEstadoVisual() == CasillaBoton.EstadoVisual.SELECCIONADA) {
                prevBtn.setEstadoVisual(CasillaBoton.EstadoVisual.NORMAL);
            }
        }
        filaSeleccionada = -1;
        colSeleccionada = -1;
        btnDisparar.setEnabled(false);
        btnDisparar.setBackground(COLOR_GRIS_BOTON);
    }

    private void accionEjecutarDisparo() {
        if (filaSeleccionada != -1 && colSeleccionada != -1) {
            int f = filaSeleccionada;
            int c = colSeleccionada;
            limpiarSeleccion();
            control.disparar(f, c);
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
            control.abandonarPartida();
        }
    }

    private void actualizarPanelNaves(ResumenFlotaDTO resumen) {
        lblPortaaviones.setText(formatear("Porta aviones", resumen.get(TipoNave.PORTAAVION)));
        lblCruceros.setText(formatear("Cruceros", resumen.get(TipoNave.CRUCERO)));
        lblSubmarinos.setText(formatear("Submarinos", resumen.get(TipoNave.SUBMARINO)));
        lblBarcos.setText(formatear("Barcos", resumen.get(TipoNave.BARCO)));
    }

    private String formatear(String nombre, ConteoNavesDTO c) {
        return "<html>-" + nombre + ":<br>"
                + "&nbsp;&nbsp;" + c.sinDanos() + " sin daños<br>"
                + "&nbsp;&nbsp;" + c.danadas() + " dañados<br>"
                + "&nbsp;&nbsp;" + c.hundidas() + " hundidos</html>";
    }

    private void refrescarMinimapa() {
        TableroDTO tableroPropio = control.getTableroPropioDTO();
        if (tableroPropio == null) return;

        EstadoCasilla[][] estados = tableroPropio.estados();
        for (int f = 0; f < 10; f++) {
            for (int c = 0; c < 10; c++) {
                EstadoCasilla est = estados[f][c];
                if (est == EstadoCasilla.HUNDIDA) {
                    miniGridPropio[f][c].setBackground(new Color(90, 0, 0));
                } else if (est == EstadoCasilla.IMPACTADA) {
                    miniGridPropio[f][c].setBackground(Color.RED);
                } else if (est == EstadoCasilla.AGUA) {
                    miniGridPropio[f][c].setBackground(Color.CYAN);
                } else {
                    boolean tieneNave = tableroPropio.casillasConNave().contains(new CoordenadaDTO(f, c));
                    miniGridPropio[f][c].setBackground(tieneNave ? Color.GREEN : new Color(130, 180, 230));
                }
            }
        }
    }

    private static class CasillaBoton extends JButton {
        public enum EstadoVisual { NORMAL, SELECCIONADA, AGUA, IMPACTO_FUEGO, HUNDIDA }
        private EstadoVisual estadoVisual = EstadoVisual.NORMAL;

        public CasillaBoton() {
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
        }

        public void setEstadoVisual(EstadoVisual estado) {
            this.estadoVisual = estado;
            repaint();
        }

        public EstadoVisual getEstadoVisual() { return estadoVisual; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            switch (estadoVisual) {
                case NORMAL -> {
                    g2.setColor(new Color(110, 165, 225));
                    g2.fillRect(0, 0, w, h);
                }
                case SELECCIONADA -> {
                    g2.setColor(new Color(40, 167, 69));
                    g2.fillRect(0, 0, w, h);
                }
                case AGUA -> {
                    g2.setColor(new Color(180, 180, 180));
                    g2.fillRect(0, 0, w, h);
                    g2.setColor(Color.BLACK);
                    int radioAgua = Math.min(w, h) / 3;
                    g2.fillOval((w - radioAgua) / 2, (h - radioAgua) / 2, radioAgua, radioAgua);
                }
                case IMPACTO_FUEGO -> {
                    g2.setPaint(new GradientPaint(0, 0, new Color(230, 40, 0), w, h, new Color(110, 15, 0)));
                    g2.fillRect(0, 0, w, h);
                    g2.setColor(new Color(40, 40, 40, 170));
                    g2.fillOval(-w / 4, -h / 4, w / 2, h / 2);
                    g2.fillOval(w / 2, -h / 4, w / 2, h / 2);
                    g2.fillOval(-w / 4, h / 2, w / 2, h / 2);
                    g2.setColor(new Color(255, 170, 0));
                    int sizeFuego = (int) (Math.min(w, h) * 0.6);
                    g2.fillOval((w - sizeFuego) / 2, (h - sizeFuego) / 2, sizeFuego, sizeFuego);
                    g2.setColor(new Color(255, 235, 50));
                    int innerFuego = sizeFuego / 2;
                    g2.fillOval((w - innerFuego) / 2, (h - innerFuego) / 2, innerFuego, innerFuego);
                }
                case HUNDIDA -> {
                    g2.setPaint(new GradientPaint(0, 0, new Color(120, 112, 108), w, h, new Color(48, 44, 44)));
                    g2.fillRect(0, 0, w, h);
                    g2.setColor(new Color(25, 25, 25, 170));
                    g2.fillOval(-w / 4, -h / 4, w / 2, h / 2);
                    g2.fillOval(w / 2, -h / 4, w / 2, h / 2);
                    g2.fillOval(-w / 4, h / 2, w / 2, h / 2);
                    g2.setColor(new Color(150, 140, 135));
                    int sizeCeniza = (int) (Math.min(w, h) * 0.6);
                    g2.fillOval((w - sizeCeniza) / 2, (h - sizeCeniza) / 2, sizeCeniza, sizeCeniza);
                    g2.setColor(new Color(195, 188, 182));
                    int innerCeniza = sizeCeniza / 2;
                    g2.fillOval((w - innerCeniza) / 2, (h - innerCeniza) / 2, innerCeniza, innerCeniza);
                }
            }
            g2.setColor(Color.WHITE);
            g2.drawRect(0, 0, w - 1, h - 1);
            g2.dispose();
        }
    }
}
