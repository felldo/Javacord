package org.javacord.core.event;

import org.javacord.api.DiscordApi;
import org.javacord.api.event.Event;
import org.javacord.api.listener.AttachableListener;
import org.javacord.api.util.event.ListenerManager;
import java.util.concurrent.TimeUnit;

/**
 * The implementation of {@link Event}.
 */
public abstract class EventImpl implements Event {

    /**
     * The api instance of the event.
     */
    protected final DiscordApi api;

    private ListenerManager<? extends AttachableListener> listenerManager;

    /**
     * Creates a new event.
     *
     * @param api The api instance of the event.
     */
    public EventImpl(DiscordApi api) {
        this.api = api;
    }

    @Override
    public DiscordApi getApi() {
        return api;
    }

    /**
     * Sets the listener manager of the event.
     *
     * @param listenerManager The listener manager of the event.
     * @return The current instance in order to chain call methods.
     */
    public EventImpl setListenerManager(ListenerManager<? extends AttachableListener> listenerManager) {
        this.listenerManager = listenerManager;
        return this;
    }

    @Override
    public void removeListener() {
        //Should never be null
        listenerManager.remove();
    }

    @Override
    public void removeListenerAfter(long delay, TimeUnit timeUnit) {
        api.getThreadPool().getScheduler().schedule((Runnable) this::removeListener, delay, timeUnit);
    }
}
