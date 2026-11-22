/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package red;

import eventbus.IEventPublisher;
import eventbus.IEventSubscriber;
import events.DisparoProcesadoEvent;
import events.DomainEvent;
import events.PartidaFinalizadaEvent;
import events.PartidaSincronizadaEvent;
import events.TiempoTickEvent;
import events.TurnoCambiadoEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class GestorRedSocket implements ISocket {

    private final IEventPublisher publisher;
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile boolean activo = false;

    public GestorRedSocket(IEventPublisher publisher, IEventSubscriber subscriber) {
        this.publisher = publisher;
        suscribirEventosLocales(subscriber);
    }

    private void suscribirEventosLocales(IEventSubscriber subscriber) {
        subscriber.suscribir(PartidaSincronizadaEvent.class, this::reenviarSiEsLocal);
        subscriber.suscribir(DisparoProcesadoEvent.class, this::reenviarSiEsLocal);
        subscriber.suscribir(TurnoCambiadoEvent.class, this::reenviarSiEsLocal);
        subscriber.suscribir(TiempoTickEvent.class, this::reenviarSiEsLocal);
        subscriber.suscribir(PartidaFinalizadaEvent.class, this::reenviarSiEsLocal);
    }

    private void reenviarSiEsLocal(DomainEvent evento) {
        if (!evento.esRemoto()) {
            enviarEvento(evento);
        }
    }

    @Override
    public void iniciarServidor(int puerto) throws IOException {
        serverSocket = new ServerSocket(puerto);
        System.out.println("[RED - SERVIDOR] Esperando conexión en puerto " + puerto + "...");
        socket = serverSocket.accept();
        System.out.println("[RED - SERVIDOR] Jugador 2 conectado desde: " + socket.getInetAddress());
        inicializarFlujos();
    }

    @Override
    public void conectarCliente(String host, int puerto) throws IOException {
        System.out.println("[RED - CLIENTE] Conectando a " + host + ":" + puerto + "...");
        socket = new Socket(host, puerto);
        System.out.println("[RED - CLIENTE] Conectado al servidor.");
        inicializarFlujos();
    }

    private void inicializarFlujos() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        
        in = new ObjectInputStream(socket.getInputStream());
        activo = true;

        Thread hiloEscucha = new Thread(this::escucharMensajes, "Hilo-Escucha-Socket");
        hiloEscucha.setDaemon(true);
        hiloEscucha.start();
    }

    private void escucharMensajes() {
        try {
            while (activo && socket != null && !socket.isClosed()) {
                Object recibido = in.readObject();
                if (recibido instanceof DomainEvent evento) {
                    publisher.publicar(evento.comoRemoto());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            if (activo) {
                System.err.println("[RED] Conexión perdida con el oponente: " + e.getMessage());
            }
        }
    }

    @Override
    public synchronized void enviarEvento(DomainEvent evento) {
        if (out == null || !activo) return;
        try {
            out.writeObject(evento);
            out.flush();
            out.reset();
        } catch (IOException e) {
            System.err.println("[RED] Error enviando evento: " + e.getMessage());
        }
    }

    @Override
    public void cerrar() {
        activo = false;
        try {
            if (socket != null) socket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) {}
    }
}
