package org.javacord.core.util.handler.guild;

import com.fasterxml.jackson.databind.JsonNode;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.server.ServerBecomesAvailableEvent;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.core.entity.server.ServerImpl;
import org.javacord.core.event.server.ServerBecomesAvailableEventImpl;
import org.javacord.core.event.server.ServerJoinEventImpl;
import org.javacord.core.util.gateway.PacketHandler;

/**
 * Handles the guild create packet.
 */
public class GuildCreateHandler extends PacketHandler {

    /**
     * Creates a new instance of this class.
     *
     * @param api The api.
     */
    public GuildCreateHandler(final DiscordApi api) {
        super(api, true, "GUILD_CREATE");
    }

    @Override
    public void handle(final JsonNode packet) {
        if (packet.has("unavailable") && packet.get("unavailable").asBoolean()) {
            return;
        }
        final long id = packet.get("id").asLong();
        if (api.getUnavailableServers().contains(id)) {
            final ServerImpl server = new ServerImpl(api, packet);
            final ServerBecomesAvailableEvent event = new ServerBecomesAvailableEventImpl(server);

            api.getEventDispatcher().dispatchServerBecomesAvailableEvent(server, event);
            return;
        }

        final ServerImpl server = new ServerImpl(api, packet);
        final ServerJoinEvent event = new ServerJoinEventImpl(server);

        api.getEventDispatcher().dispatchServerJoinEvent(server, event);
    }

}
