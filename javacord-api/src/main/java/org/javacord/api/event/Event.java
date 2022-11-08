package org.javacord.api.event;

import org.javacord.api.DiscordApi;
import java.util.concurrent.TimeUnit;

/**
 * The basic event.
 */
public interface Event {

    /**
     * Gets the api instance of the event.
     *
     * @return The api instance of the event.
     */
    DiscordApi getApi();

    /**
     * Removes the listener.
     */
    void removeListener();

    /**
     * Removes the listener after the given delay.
     *
     * @param delay The time to wait before removing the listener.
     * @param timeUnit The time unit of the delay.
     */
    void removeListenerAfter(long delay, TimeUnit timeUnit);

}
