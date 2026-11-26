package ui;

import application.IControlJuego;
import dtos.JugadorDTO;
import dtos.NaveDTO;
import enums.Orientacion;
import enums.TipoNave;
import eventbus.IEventSubscriber;
import events.ColocacionActualizadaEvent;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class PantallaColocarNaves extends JFrame {

    private static final int N = 10;
    private static final int TAM_CELDA = 40;

    private final IControlJuego control;
    private Integer naveSeleccionada;
    private Orientacion orientacionSeleccionada = Orientacion.VERTICAL;
    private Integer naveEnProceso;      

    private final IEventSubscriber subscriber;
    private final JugadorDTO jugadorLocal;
    private final boolean iniciaConTurno;
    private List<NaveDTO> flota = new ArrayList<>();
    private boolean tableroConfirmado = false;
    private boolean activa = true;

    // Vista previa de colocación sobre el tablero
    private final List<Point> casillasPreview = new ArrayList<>();
    private Point anclaPreview;           // casilla bajo el cursor (fila, col)
    private boolean previewValida = true;
    private final List<Point> casillasInvalidas = new ArrayList<>();
    private Timer timerInvalida;

    // Arrastre
    private boolean arrastrando = false;
    private Point puntoArrastre;          // en coordenadas del glass pane

    private final Map<Integer, TarjetaNave> tarjetas = new LinkedHashMap<>();
    private final CeldaTablero[][] celdas = new CeldaTablero[N][N];
    private JPanel pnlTablero;
    private BotonRedondeado btnRotar;
    private BotonRedondeado btnConfirmar;
    private JLabel lblEstado;
    private CapaArrastre capaArrastre;

    private static final Color COLOR_FONDO = new Color(150, 200, 240);
    private static final Color COLOR_NAVY = new Color(13, 60, 110);
    private static final Color COLOR_BARRA = new Color(13, 44, 76);
    private static final Color COLOR_CELDA = new Color(150, 200, 240);
    private static final Color COLOR_LINEA = new Color(40, 80, 130);
    private static final Color COLOR_NARANJA = new Color(247, 148, 29);
    private static final Color COLOR_AMARILLO = new Color(250, 200, 30);
    private static final Color COLOR_GRIS = new Color(128, 128, 128);
    private static final Color COLOR_VERDE = new Color(40, 167, 69);
    private static final Color COLOR_TARJETA = new Color(225, 230, 236);
    private static final Color COLOR_TARJETA_SEL = new Color(240, 236, 170);
    private static final Color COLOR_PREVIEW_OK = new Color(60, 200, 90, 150);
    private static final Color COLOR_PREVIEW_MAL = new Color(230, 40, 40, 150);

    public PantallaColocarNaves(IControlJuego control, IEventSubscriber subscriber, boolean iniciaConTurno) {
        this.control = control;
        this.subscriber = subscriber;
        this.jugadorLocal = control.getJugadorLocalDTO();
        this.iniciaConTurno = iniciaConTurno;

        initUI();
        repintarTablero(control.getFlotaLocalDTO());
        registrarSuscriptoresEventos();
    }

    //  Construcción de la UI
    private void initUI() {
        setTitle("Colocar naves - Batalla Naval (" + jugadorLocal.nombre() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 640);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(COLOR_FONDO);
        main.add(crearHeader(), BorderLayout.NORTH);
        main.add(crearCuerpo(), BorderLayout.CENTER);
        main.add(crearBarraInferior(), BorderLayout.SOUTH);
        setContentPane(main);

        capaArrastre = new CapaArrastre();
        setGlassPane(capaArrastre);
        capaArrastre.setVisible(true);
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_BARRA);
        header.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 20));

        JButton btnRegresar = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = suavizar(g);
                g2.setColor(COLOR_AMARILLO);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.drawPolyline(new int[]{cx + 3, cx - 4, cx + 3}, new int[]{cy - 7, cy, cy + 7}, 3);
                g2.dispose();
            }
        };
        btnRegresar.setPreferredSize(new Dimension(36, 36));
        btnRegresar.setContentAreaFilled(false);
        btnRegresar.setBorderPainted(false);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegresar.setToolTipText("Salir");
        btnRegresar.addActionListener(e -> onRegresar());

        JLabel lblJugador = new JLabel(jugadorLocal.nombre());
        lblJugador.setFont(new Font("Impact", Font.PLAIN, 30));
        lblJugador.setForeground(Color.WHITE);

        header.add(btnRegresar, BorderLayout.WEST);
        header.add(lblJugador, BorderLayout.EAST);
        return header;
    }

    private JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout(30, 0));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));
        cuerpo.add(crearPanelFlota(), BorderLayout.WEST);
        cuerpo.add(crearPanelTablero(), BorderLayout.CENTER);
        return cuerpo;
    }

    private JPanel crearPanelFlota() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setOpaque(false);
        pnl.setPreferredSize(new Dimension(300, 0));

        JLabel lblFlota = new JLabel("Flota");
        lblFlota.setFont(new Font("Impact", Font.PLAIN, 30));
        lblFlota.setForeground(COLOR_NAVY);

        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 10));
        grid.setOpaque(false);
        for (NaveDTO n : control.getFlotaLocalDTO()) {
            TarjetaNave t = new TarjetaNave(n.id(), n.tipo());
            tarjetas.put(n.id(), t);
            JPanel holder = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            holder.setOpaque(false);
            holder.add(t);
            grid.add(holder);
        }

        JPanel envoltura = new JPanel(new BorderLayout());
        envoltura.setOpaque(false);
        envoltura.add(grid, BorderLayout.NORTH);

        pnl.add(lblFlota, BorderLayout.NORTH);
        pnl.add(envoltura, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel crearPanelTablero() {
        JPanel contenedor = new JPanel();
        contenedor.setOpaque(false);
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));

        // Letras A-J
        JPanel pnlLetras = new JPanel(new GridLayout(1, N));
        pnlLetras.setOpaque(false);
        for (char l = 'A'; l < 'A' + N; l++) {
            pnlLetras.add(etiquetaCoordenada(String.valueOf(l)));
        }
        // Números 1-10
        JPanel pnlNumeros = new JPanel(new GridLayout(N, 1));
        pnlNumeros.setOpaque(false);
        for (int i = 1; i <= N; i++) {
            pnlNumeros.add(etiquetaCoordenada(String.valueOf(i)));
        }

        pnlTablero = new JPanel(new GridLayout(N, N, 0, 0)) {
            @Override
            protected void paintChildren(Graphics g) {
                super.paintChildren(g);
                pintarNavesColocadas(suavizar(g));
            }
        };
        pnlTablero.setBorder(BorderFactory.createLineBorder(COLOR_LINEA, 2));
        for (int f = 0; f < N; f++) {
            for (int c = 0; c < N; c++) {
                CeldaTablero celda = new CeldaTablero(f, c);
                celdas[f][c] = celda;
                pnlTablero.add(celda);
            }
        }
        pnlTablero.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (!arrastrando) limpiarPreview();
            }
        });

        int lado = TAM_CELDA * N + 4;
        Dimension dTablero = new Dimension(lado, lado);
        pnlTablero.setPreferredSize(dTablero);
        pnlLetras.setPreferredSize(new Dimension(lado, 28));
        pnlNumeros.setPreferredSize(new Dimension(30, lado));

        JPanel filaLetras = new JPanel(new BorderLayout());
        filaLetras.setOpaque(false);
        filaLetras.add(Box.createHorizontalStrut(30), BorderLayout.WEST);
        filaLetras.add(pnlLetras, BorderLayout.CENTER);

        JPanel filaTablero = new JPanel(new BorderLayout());
        filaTablero.setOpaque(false);
        filaTablero.add(pnlNumeros, BorderLayout.WEST);
        filaTablero.add(pnlTablero, BorderLayout.CENTER);

        JPanel tableroCompleto = new JPanel(new BorderLayout());
        tableroCompleto.setOpaque(false);
        tableroCompleto.add(filaLetras, BorderLayout.NORTH);
        tableroCompleto.add(filaTablero, BorderLayout.CENTER);
        tableroCompleto.setMaximumSize(new Dimension(lado + 30, lado + 28));
        tableroCompleto.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Pie: mensaje de estado + botón rotar
        lblEstado = new JLabel();
        lblEstado.setFont(new Font("Arial", Font.BOLD, 12));
        lblEstado.setForeground(COLOR_NAVY);
        lblEstado.setPreferredSize(new Dimension(lado - 180, 40));
        mostrarEstado("Arrastra una nave al tablero, o selecciónala y haz clic en una casilla.");

        btnRotar = new BotonRedondeado(textoBotonRotar(), COLOR_NARANJA, 10);
        btnRotar.setFont(new Font("Arial", Font.BOLD, 13));
        btnRotar.setPreferredSize(new Dimension(160, 34));
        btnRotar.addActionListener(e -> onRotarNave());

        JPanel pie = new JPanel(new BorderLayout(10, 0));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(10, 30, 0, 0));
        pie.add(lblEstado, BorderLayout.CENTER);
        pie.add(btnRotar, BorderLayout.EAST);
        pie.setMaximumSize(new Dimension(lado + 30, 50));
        pie.setAlignmentX(Component.CENTER_ALIGNMENT);

        contenedor.add(tableroCompleto);
        contenedor.add(pie);
        contenedor.add(Box.createVerticalGlue());
        return contenedor;
    }

    private JPanel crearBarraInferior() {
        JPanel barra = new JPanel(new GridBagLayout());
        barra.setBackground(COLOR_BARRA);
        barra.setPreferredSize(new Dimension(0, 75));

        btnConfirmar = new BotonRedondeado("Confirmar tablero", COLOR_GRIS, 40);
        btnConfirmar.setFont(new Font("Impact", Font.PLAIN, 22));
        btnConfirmar.setPreferredSize(new Dimension(240, 46));
        btnConfirmar.addActionListener(e -> onConfirmarTablero());

        barra.add(btnConfirmar);
        return barra;
    }

    private JLabel etiquetaCoordenada(String texto) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setFont(new Font("Impact", Font.PLAIN, 20));
        lbl.setForeground(COLOR_NAVY);
        return lbl;
    }

    //  Suscripción al EventBus (reemplaza IObservador.update)
    private void registrarSuscriptoresEventos() {
        subscriber.suscribir(ColocacionActualizadaEvent.class,
                evento -> SwingUtilities.invokeLater(() -> update(evento)));
    }

    public void update(ColocacionActualizadaEvent evento) {
        if (!activa) return;
        boolean esMio = jugadorLocal.id().equals(evento.idJugador());

        switch (evento.resultado()) {
            case COLOCADA -> {
                if (!esMio) return;
                naveSeleccionada = null;
                repintarTablero(evento.flota());
                mostrarEstado("Nave colocada. Haz clic sobre una nave del tablero para retirarla.");
            }
            case POSICION_INVALIDA -> {
                if (!esMio) return;
                marcarPosicionInvalida(evento.fila(), evento.columna());
                regresarNaveASeccion(evento.idNave());
            }
            case RETIRADA -> {
                if (!esMio) return;
                repintarTablero(evento.flota());
                regresarNaveASeccion(evento.idNave());
            }
            case FLOTA_INCOMPLETA -> {
                if (!esMio) return;
                mostrarAviso("Debes colocar todas tus naves");
            }
            case TABLERO_CONFIRMADO -> {
                if (esMio) {
                    tableroConfirmado = true;
                    bloquearEdicion();
                    mostrarAlerta("Tablero confirmado");
                    mostrarEstado("Esperando a que tu rival confirme su tablero...");
                } else {
                    mostrarEstado("Tu rival ya confirmó su tablero.");
                }
            }
            case PARTIDA_INICIADA -> abrirPantallaDisparar();
        }
    }

    //  Acciones del jugador 
    public void onRotarNave() {
        if (tableroConfirmado) return;
        orientacionSeleccionada = (orientacionSeleccionada == Orientacion.VERTICAL)
                ? Orientacion.HORIZONTAL
                : Orientacion.VERTICAL;
        btnRotar.setText(textoBotonRotar());
        // Refresca la vista previa si el cursor está sobre el tablero
        if (anclaPreview != null) {
            mostrarPreview(anclaPreview.x, anclaPreview.y);
        }
        capaArrastre.repaint();
    }

    public void onSoltarNave(int idNave, int fila, int col) {
        if (tableroConfirmado) return;
        limpiarPreview();
        naveEnProceso = idNave;
        control.colocarNave(idNave, fila, col, orientacionSeleccionada);
    }

    public void onClicCasilla(int fila, int col) {
        if (tableroConfirmado) return;

        if (naveSeleccionada != null) {
            onSoltarNave(naveSeleccionada, fila, col);
            return;
        }
        NaveDTO nave = buscarNaveEn(fila, col);
        if (nave != null) {
            onClicNaveColocada(nave.id());
        }
    }

    public void onClicNaveColocada(int idNave) {
        mostrarFormularioReposicionar(idNave);
    }

    private NaveDTO buscarNaveEn(int fila, int col) {
        for (NaveDTO n : flota) {
            if (n.colocada() && casillasDe(n.filaInicio(), n.colInicio(), n.tipo(), n.orientacion())
                    .contains(new Point(fila, col))) {
                return n;
            }
        }
        return null;
    }

    public void mostrarFormularioReposicionar(int idNave) {
        NaveDTO nave = buscarNavePorId(idNave);
        String nombre = nave != null ? nombreNave(nave.tipo()) : "nave";
        Object[] opciones = {"Eliminar nave", "Cancelar"};
        int op = JOptionPane.showOptionDialog(this,
                "¿Quieres retirar el " + nombre + " del tablero para volver a colocarlo?",
                "Reposicionar nave",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);
        if (op == 0) {
            onEliminarNave(idNave);
        }
    }

    public void onEliminarNave(int idNave) {
        control.retirarNave(idNave);
    }

    public void onConfirmarTablero() {
        if (tableroConfirmado) return;
        mostrarFormularioConfirmacion();
    }

    public void mostrarFormularioConfirmacion() {
        int op = JOptionPane.showConfirmDialog(this,
                "¿Confirmas la posición de tus naves?\nDespués ya no podrás moverlas.",
                "Confirmar tablero",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (op == JOptionPane.YES_OPTION) {
            onAceptarConfirmacion();
        }
    }

    public void onAceptarConfirmacion() {
        control.confirmarTablero();
    }

    private void onRegresar() {
        int op = JOptionPane.showConfirmDialog(this,
                "¿Deseas salir? Se perderá la colocación de tus naves.",
                "Salir", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (op == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    //  Respuestas de la vista
    public void repintarTablero(List<NaveDTO> nuevaFlota) {
        if (nuevaFlota != null && !nuevaFlota.isEmpty()) {
            this.flota = new ArrayList<>(nuevaFlota);
        }
        for (NaveDTO n : flota) {
            TarjetaNave t = tarjetas.get(n.id());
            if (t != null) {
                t.setColocada(n.colocada());
            }
        }
        actualizarSeleccionVisual();
        actualizarBotonConfirmar();
        pnlTablero.repaint();
    }

    public void marcarPosicionInvalida(int fila, int col) {
        NaveDTO nave = naveEnProceso != null ? buscarNavePorId(naveEnProceso) : null;
        TipoNave tipo = nave != null ? nave.tipo() : TipoNave.BARCO;

        casillasInvalidas.clear();
        for (Point p : casillasDe(fila, col, tipo, orientacionSeleccionada)) {
            if (enRango(p.x, p.y)) casillasInvalidas.add(p);
        }
        if (casillasInvalidas.isEmpty() && enRango(fila, col)) {
            casillasInvalidas.add(new Point(fila, col));
        }
        pnlTablero.repaint();

        if (timerInvalida != null) timerInvalida.stop();
        timerInvalida = new Timer(900, e -> {
            casillasInvalidas.clear();
            pnlTablero.repaint();
        });
        timerInvalida.setRepeats(false);
        timerInvalida.start();

        mostrarEstado("Posición inválida: la nave queda fuera del tablero o pegada a otra nave.");
    }

    public void regresarNaveASeccion(Integer idNave) {
        if (idNave != null) {
            TarjetaNave t = tarjetas.get(idNave);
            if (t != null) t.setColocada(false);
        }
        naveSeleccionada = null;
        actualizarSeleccionVisual();
        actualizarBotonConfirmar();
    }

    public void mostrarAviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
        onCerrarAviso();
    }

    public void onCerrarAviso() {
        cerrarAviso();
    }

    public void cerrarAviso() {
        mostrarEstado("Coloca las naves que te faltan y vuelve a confirmar.");
    }

    public void mostrarAlerta(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Batalla Naval", JOptionPane.INFORMATION_MESSAGE);
    }

    public void abrirPantallaDisparar() {
        if (!activa) return;
        activa = false;
        dispose();
        PantallaDisparar pantalla = new PantallaDisparar(control, subscriber, iniciaConTurno);
        new AlertasView(pantalla, control.getJugadorLocalDTO(), subscriber);
        pantalla.setVisible(true);
    }

    //  Auxiliares de vista
    private void seleccionarNave(int idNave) {
        if (tableroConfirmado) return;
        TarjetaNave t = tarjetas.get(idNave);
        if (t == null || t.isColocada()) return;
        naveSeleccionada = idNave;
        actualizarSeleccionVisual();
        mostrarEstado("Selecciona la casilla de inicio para el " + nombreNave(t.tipo) + ".");
    }

    private void actualizarSeleccionVisual() {
        for (TarjetaNave t : tarjetas.values()) {
            t.setSeleccionada(naveSeleccionada != null && t.idNave == naveSeleccionada);
        }
    }

    private void actualizarBotonConfirmar() {
        boolean completa = !flota.isEmpty() && flota.stream().allMatch(NaveDTO::colocada);
        btnConfirmar.setColorFondo(completa && !tableroConfirmado ? COLOR_VERDE : COLOR_GRIS);
    }

    private void bloquearEdicion() {
        naveSeleccionada = null;
        limpiarPreview();
        actualizarSeleccionVisual();
        btnRotar.setEnabled(false);
        btnRotar.setColorFondo(COLOR_GRIS);
        btnConfirmar.setText("Tablero confirmado");
        btnConfirmar.setColorFondo(COLOR_GRIS);
        btnConfirmar.setEnabled(false);
        for (TarjetaNave t : tarjetas.values()) t.setCursor(Cursor.getDefaultCursor());
    }

    private void mostrarPreview(int fila, int col) {
        casillasPreview.clear();
        anclaPreview = new Point(fila, col);
        Integer id = naveSeleccionada;
        if (id == null || tableroConfirmado) {
            pnlTablero.repaint();
            return;
        }
        TarjetaNave t = tarjetas.get(id);
        List<Point> huella = casillasDe(fila, col, t.tipo, orientacionSeleccionada);
        previewValida = true;
        for (Point p : huella) {
            if (!enRango(p.x, p.y)) {
                previewValida = false;
            } else {
                casillasPreview.add(p);
                if (buscarNaveEn(p.x, p.y) != null) previewValida = false;
            }
        }
        pnlTablero.repaint();
    }

    private void limpiarPreview() {
        anclaPreview = null;
        if (!casillasPreview.isEmpty()) {
            casillasPreview.clear();
            pnlTablero.repaint();
        }
    }

    private CeldaTablero celdaEnPantalla(Point enPantalla) {
        Point p = new Point(enPantalla);
        SwingUtilities.convertPointFromScreen(p, pnlTablero);
        if (!pnlTablero.contains(p)) return null;
        Component c = pnlTablero.getComponentAt(p);
        return (c instanceof CeldaTablero celda) ? celda : null;
    }

    private void pintarNavesColocadas(Graphics2D g2) {
        for (NaveDTO n : flota) {
            if (!n.colocada()) continue;
            int tam = n.tipo().getTamano();
            boolean horizontal = n.orientacion() == Orientacion.HORIZONTAL;
            int fFin = horizontal ? n.filaInicio() : n.filaInicio() + tam - 1;
            int cFin = horizontal ? n.colInicio() + tam - 1 : n.colInicio();
            if (!enRango(n.filaInicio(), n.colInicio()) || !enRango(fFin, cFin)) continue;

            Rectangle r = celdas[n.filaInicio()][n.colInicio()].getBounds()
                    .union(celdas[fFin][cFin].getBounds());
            DibujoNave.pintar(g2, n.tipo(), new Rectangle2D.Double(r.x + 2, r.y + 2, r.width - 4, r.height - 4), horizontal);
        }
        g2.dispose();
    }

    private String textoBotonRotar() {
        return orientacionSeleccionada == Orientacion.VERTICAL ? "Rotar a horizontal" : "Rotar a vertical";
    }

    private void mostrarEstado(String texto) {
        lblEstado.setText("<html>" + texto + "</html>");
    }

    private NaveDTO buscarNavePorId(int idNave) {
        return flota.stream().filter(n -> n.id() == idNave).findFirst().orElse(null);
    }

    private static List<Point> casillasDe(int fila, int col, TipoNave tipo, Orientacion o) {
        List<Point> lista = new ArrayList<>();
        for (int i = 0; i < tipo.getTamano(); i++) {
            lista.add(o == Orientacion.VERTICAL ? new Point(fila + i, col) : new Point(fila, col + i));
        }
        return lista;
    }

    private static boolean enRango(int f, int c) {
        return f >= 0 && f < N && c >= 0 && c < N;
    }

    private static String nombreNave(TipoNave tipo) {
        return switch (tipo) {
            case PORTAAVION -> "portaaviones";
            case CRUCERO -> "crucero";
            case SUBMARINO -> "submarino";
            case BARCO -> "barco";
        };
    }

    private static Graphics2D suavizar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }

    // Componentes internos
    private class TarjetaNave extends JComponent {
        final int idNave;
        final TipoNave tipo;
        private boolean colocada = false;
        private boolean seleccionada = false;

        TarjetaNave(int idNave, TipoNave tipo) {
            this.idNave = idNave;
            this.tipo = tipo;
            int ancho = (tipo == TipoNave.BARCO) ? 78 : 132;
            setPreferredSize(new Dimension(ancho, 44));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText(nombreNave(tipo) + " (" + tipo.getTamano() + " casillas)");

            MouseAdapter ma = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (colocada || tableroConfirmado) return;
                    seleccionarNave(idNave);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (colocada || tableroConfirmado || naveSeleccionada == null) return;
                    arrastrando = true;
                    puntoArrastre = SwingUtilities.convertPoint(TarjetaNave.this, e.getPoint(), capaArrastre);
                    capaArrastre.repaint();

                    CeldaTablero celda = celdaEnPantalla(e.getLocationOnScreen());
                    if (celda != null) mostrarPreview(celda.fila, celda.col);
                    else limpiarPreview();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (!arrastrando) return;
                    arrastrando = false;
                    capaArrastre.repaint();
                    CeldaTablero celda = celdaEnPantalla(e.getLocationOnScreen());
                    if (celda != null && naveSeleccionada != null) {
                        onSoltarNave(naveSeleccionada, celda.fila, celda.col);
                    } else {
                        limpiarPreview();
                    }
                }
            };
            addMouseListener(ma);
            addMouseMotionListener(ma);
        }

        boolean isColocada() { return colocada; }

        void setColocada(boolean colocada) {
            this.colocada = colocada;
            setCursor(colocada ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            repaint();
        }

        void setSeleccionada(boolean seleccionada) {
            this.seleccionada = seleccionada;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suavizar(g);
            int w = getWidth(), h = getHeight();
            RoundRectangle2D fondo = new RoundRectangle2D.Double(1, 1, w - 3, h - 3, 10, 10);

            if (colocada) {
                // Hueco vacío: la nave ya está en el tablero
                g2.setColor(new Color(255, 255, 255, 70));
                g2.fill(fondo);
                g2.setColor(new Color(255, 255, 255, 160));
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1f, new float[]{5f, 4f}, 0f));
                g2.draw(fondo);
            } else {
                g2.setColor(seleccionada ? COLOR_TARJETA_SEL : COLOR_TARJETA);
                g2.fill(fondo);
                g2.setColor(seleccionada ? COLOR_AMARILLO : new Color(190, 198, 208));
                g2.setStroke(new BasicStroke(seleccionada ? 2.5f : 1f));
                g2.draw(fondo);
                DibujoNave.pintar(g2, tipo, new Rectangle2D.Double(6, 6, w - 12, h - 12), true);
            }
            g2.dispose();
        }
    }

    /** Casilla del tablero propio. */
    private class CeldaTablero extends JComponent {
        final int fila;
        final int col;

        CeldaTablero(int fila, int col) {
            this.fila = fila;
            this.col = col;
            setPreferredSize(new Dimension(TAM_CELDA, TAM_CELDA));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!arrastrando) mostrarPreview(fila, col);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    onClicCasilla(fila, col);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();
            Point yo = new Point(fila, col);

            g2.setColor(COLOR_CELDA);
            g2.fillRect(0, 0, w, h);
            if (casillasInvalidas.contains(yo)) {
                g2.setColor(COLOR_PREVIEW_MAL);
                g2.fillRect(0, 0, w, h);
            } else if (casillasPreview.contains(yo)) {
                g2.setColor(previewValida ? COLOR_PREVIEW_OK : COLOR_PREVIEW_MAL);
                g2.fillRect(0, 0, w, h);
            }
            g2.setColor(COLOR_LINEA);
            g2.drawRect(0, 0, w - 1, h - 1);
            g2.dispose();
        }
    }

    /** Glass pane que dibuja la nave mientras se arrastra. */
    private class CapaArrastre extends JComponent {
        CapaArrastre() {
            setOpaque(false);
        }

        @Override
        public boolean contains(int x, int y) {
            return false; // no intercepta eventos de ratón
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (!arrastrando || puntoArrastre == null || naveSeleccionada == null) return;
            TarjetaNave t = tarjetas.get(naveSeleccionada);
            if (t == null) return;
            boolean horizontal = orientacionSeleccionada == Orientacion.HORIZONTAL;
            double largo = TAM_CELDA * t.tipo.getTamano() - 4;
            double ancho = TAM_CELDA - 4;
            double x = puntoArrastre.x - TAM_CELDA / 2.0 + 2;
            double y = puntoArrastre.y - TAM_CELDA / 2.0 + 2;
            Rectangle2D r = horizontal
                    ? new Rectangle2D.Double(x, y, largo, ancho)
                    : new Rectangle2D.Double(x, y, ancho, largo);
            Graphics2D g2 = suavizar(g);
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.75f));
            DibujoNave.pintar(g2, t.tipo, r, horizontal);
            g2.dispose();
        }
    }

    private static class BotonRedondeado extends JButton {
        private Color colorFondo;
        private final int radio;

        BotonRedondeado(String texto, Color colorFondo, int radio) {
            super(texto);
            this.colorFondo = colorFondo;
            this.radio = radio;
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        void setColorFondo(Color c) {
            this.colorFondo = c;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suavizar(g);
            Color c = getModel().isPressed() ? colorFondo.darker() : colorFondo;
            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static final class DibujoNave {
        private DibujoNave() {}

        static void pintar(Graphics2D g0, TipoNave tipo, Rectangle2D r, boolean horizontal) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double x = r.getX(), y = r.getY(), L = r.getWidth(), H = r.getHeight();
            if (!horizontal) {
                g.translate(x + L, y);
                g.rotate(Math.PI / 2);
                x = 0;
                y = 0;
                double t = L; L = H; H = t;
            }

            switch (tipo) {
                case SUBMARINO -> pintarSubmarino(g, x, y, L, H);
                case PORTAAVION -> pintarPortaaviones(g, x, y, L, H);
                case CRUCERO -> pintarCrucero(g, x, y, L, H);
                case BARCO -> pintarBarco(g, x, y, L, H);
            }
            g.dispose();
        }

        private static Path2D casco(double x, double y, double L, double H, double arriba, double abajo) {
            Path2D p = new Path2D.Double();
            p.moveTo(x + L * 0.03, y + H * arriba);
            p.lineTo(x + L * 0.86, y + H * arriba);
            p.lineTo(x + L, y + H * (arriba - 0.06));
            p.lineTo(x + L * 0.90, y + H * abajo);
            p.lineTo(x + L * 0.07, y + H * abajo);
            p.lineTo(x, y + H * (arriba + 0.12));
            p.closePath();
            return p;
        }

        private static void pintarPortaaviones(Graphics2D g, double x, double y, double L, double H) {
            g.setColor(new Color(70, 80, 92));
            g.fill(casco(x, y, L, H, 0.42, 0.85));
            g.setColor(new Color(52, 58, 66)); // cubierta de vuelo
            g.fill(new Rectangle2D.Double(x + L * 0.02, y + H * 0.34, L * 0.94, H * 0.12));
            g.setColor(new Color(230, 230, 230));
            g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{4f, 3f}, 0f));
            g.draw(new java.awt.geom.Line2D.Double(x + L * 0.06, y + H * 0.40, x + L * 0.90, y + H * 0.40));
            g.setColor(new Color(95, 105, 118)); // isla
            g.fill(new Rectangle2D.Double(x + L * 0.62, y + H * 0.12, L * 0.10, H * 0.24));
            g.fill(new Rectangle2D.Double(x + L * 0.655, y + H * 0.02, L * 0.025, H * 0.12));
        }

        private static void pintarCrucero(Graphics2D g, double x, double y, double L, double H) {
            g.setColor(new Color(100, 112, 126));
            g.fill(casco(x, y, L, H, 0.48, 0.86));
            g.setColor(new Color(130, 140, 152));
            g.fill(new Rectangle2D.Double(x + L * 0.30, y + H * 0.28, L * 0.32, H * 0.22));
            g.fill(new Rectangle2D.Double(x + L * 0.38, y + H * 0.12, L * 0.14, H * 0.18));
            g.setColor(new Color(80, 90, 102)); // torretas
            g.fill(new java.awt.geom.Ellipse2D.Double(x + L * 0.14, y + H * 0.34, L * 0.09, H * 0.16));
            g.fill(new java.awt.geom.Ellipse2D.Double(x + L * 0.70, y + H * 0.34, L * 0.09, H * 0.16));
            g.setStroke(new BasicStroke((float) Math.max(1.5, H * 0.05)));
            g.draw(new java.awt.geom.Line2D.Double(x + L * 0.79, y + H * 0.40, x + L * 0.88, y + H * 0.36));
            g.draw(new java.awt.geom.Line2D.Double(x + L * 0.14, y + H * 0.40, x + L * 0.05, y + H * 0.36));
        }

        private static void pintarSubmarino(Graphics2D g, double x, double y, double L, double H) {
            g.setColor(new Color(35, 90, 110));
            g.fill(new RoundRectangle2D.Double(x + L * 0.02, y + H * 0.40, L * 0.96, H * 0.42, H * 0.42, H * 0.42));
            g.setColor(new Color(200, 50, 50)); // franja roja
            g.fill(new Rectangle2D.Double(x + L * 0.10, y + H * 0.62, L * 0.80, H * 0.07));
            g.setColor(new Color(28, 72, 90)); // vela
            g.fill(new RoundRectangle2D.Double(x + L * 0.40, y + H * 0.18, L * 0.16, H * 0.26, 6, 6));
            g.setColor(new Color(60, 120, 140));
            g.fill(new Rectangle2D.Double(x + L * 0.10, y + H * 0.46, L * 0.25, H * 0.05));
        }

        private static void pintarBarco(Graphics2D g, double x, double y, double L, double H) {
            g.setColor(new Color(215, 220, 228));
            g.fill(casco(x, y, L, H, 0.48, 0.84));
            g.setColor(new Color(200, 60, 60)); // línea de flotación
            g.fill(new Rectangle2D.Double(x + L * 0.08, y + H * 0.74, L * 0.82, H * 0.07));
            g.setColor(new Color(110, 125, 145)); // cabina
            g.fill(new RoundRectangle2D.Double(x + L * 0.25, y + H * 0.22, L * 0.40, H * 0.28, 5, 5));
            g.setColor(new Color(180, 215, 240));
            g.fill(new Rectangle2D.Double(x + L * 0.30, y + H * 0.28, L * 0.30, H * 0.08));
        }
    }
}