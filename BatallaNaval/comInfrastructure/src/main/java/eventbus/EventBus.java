/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eventbus;

import events.DomainEvent;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 *
 * @author Jesus Cisneros, Paulina Guevara, Sebastian Villegas, Benjamin Barcelo
 */
public class EventBus implements IEventPublisher, IEventSubscriber{

    private final Map<Class<? extends DomainEvent>, List<Consumer<? extends DomainEvent>>> subscribers = new ConcurrentHashMap<>();

    @Override
    public <T extends DomainEvent> void suscribir(Class<T> eventType, Consumer<T> listener) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void publicar(DomainEvent event) {
        if (event == null) return;
        List<Consumer<? extends DomainEvent>> list = subscribers.get(event.getClass());
        if (list != null) {
            for (Consumer<? extends DomainEvent> listener : list) {
                ((Consumer<DomainEvent>) listener).accept(event);
            }
        }
    }
    
}
