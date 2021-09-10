package org.javacord.core.util.handler.guild;

import com.fasterxml.jackson.databind.JsonNode;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.server.VoiceServerUpdateEvent;
import org.javacord.core.audio.AudioConnectionImpl;
import org.javacord.core.event.server.VoiceServerUpdateEventImpl;
import org.javacord.core.util.event.DispatchQueueSelector;
import org.javacord.core.util.gateway.PacketHandler;

/**
 * Handles the voice server update packet.
 */
public class VoiceServerUpdateHandler extends PacketHandler {

    /**
     * Creates a new instance of this class.
     *
     * @param api The api.
     */
    public VoiceServerUpdateHandler(final DiscordApi api) {
        super(api, true, "VOICE_SERVER_UPDATE");
    }

    @Override
    public void handle(final JsonNode packet) {
        final String token = packet.get("token").asText();
        final String endpoint = packet.get("endpoint").asText();
        final long serverId = packet.get("guild_id").asLong();

        // We need the session id to connect to an audio websocket
        final AudioConnectionImpl pendingAudioConnection =
                api.getPendingAudioConnectionByServerId(serverId);
        if (pendingAudioConnection != null) {
            pendingAudioConnection.setToken(token);
            pendingAudioConnection.setEndpoint(endpoint);
            pendingAudioConnection.tryConnect();
        }
        api.getServerById(serverId).ifPresent(server -> {
            final VoiceServerUpdateEvent event = new VoiceServerUpdateEventImpl(server, token, endpoint);
            api.getEventDispatcher().dispatchVoiceServerUpdateEvent((DispatchQueueSelector) server, server, event);
        });
    }
}
