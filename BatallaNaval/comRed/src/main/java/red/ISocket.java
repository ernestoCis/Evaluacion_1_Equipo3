/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package red;

import events.DomainEvent;
import java.io.IOException;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public interface ISocket {
    void iniciarServidor(int puerto) throws IOException;
    void conectarCliente(String host, int puerto) throws IOException;
    void enviarEvento(DomainEvent evento);
    void cerrar();
}
