/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package infrastructure.eventbus;

import domain.events.DomainEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class EventBus {

    private static final Map<Class<? extends DomainEvent>, List<Consumer<? extends DomainEvent>>> subscribers = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends DomainEvent> void subscribe(Class<T> eventType, Consumer<T> subscriber) {
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(subscriber);
    }

    @SuppressWarnings("unchecked")
    public static <T extends DomainEvent> void publish(T event) {
        List<Consumer<? extends DomainEvent>> eventSubscribers = subscribers.get(event.getClass());
        if (eventSubscribers != null) {
            for (Consumer<? extends DomainEvent> subscriber : eventSubscribers) {
                Consumer<T> typedSubscriber = (Consumer<T>) subscriber;
                // Garantizar ejecución segura en la GUI de Swing
                if (SwingUtilities.isEventDispatchThread()) {
                    typedSubscriber.accept(event);
                } else {
                    SwingUtilities.invokeLater(() -> typedSubscriber.accept(event));
                }
            }
        }
    }

    public static void clear() {
        subscribers.clear();
    }
}
