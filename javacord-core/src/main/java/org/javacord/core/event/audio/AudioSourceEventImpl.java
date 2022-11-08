package org.javacord.core.event.audio;

import org.javacord.api.audio.AudioConnection;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.event.audio.AudioSourceEvent;
import org.javacord.core.event.EventImpl;

/**
 * The implementation of {@link AudioSourceEvent}.
 */
public abstract class AudioSourceEventImpl extends EventImpl implements AudioSourceEvent {

    private final AudioSource source;
    private final AudioConnection connection;

    /**
     * Creates a new audio source event.
     *
     * @param source The audio source of the event.
     * @param connection The audio connection of the event.
     */
    public AudioSourceEventImpl(AudioSource source, AudioConnection connection) {
        super(connection.getChannel().getApi());
        this.source = source;
        this.connection = connection;
    }

    @Override
    public AudioSource getSource() {
        return source;
    }

    @Override
    public AudioConnection getConnection() {
        return connection;
    }

}
