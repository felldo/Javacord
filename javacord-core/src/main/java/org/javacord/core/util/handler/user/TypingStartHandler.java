package org.javacord.core.util.handler.user;

import com.fasterxml.jackson.databind.JsonNode;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.user.UserStartTypingEvent;
import org.javacord.core.entity.server.ServerImpl;
import org.javacord.core.entity.user.MemberImpl;
import org.javacord.core.event.user.UserStartTypingEventImpl;
import org.javacord.core.util.gateway.PacketHandler;

/**
 * Handles the typing start packet.
 */
public class TypingStartHandler extends PacketHandler {

    /**
     * Creates a new instance of this class.
     *
     * @param api The api.
     */
    public TypingStartHandler(final DiscordApi api) {
        super(api, true, "TYPING_START");
    }

    @Override
    public void handle(final JsonNode packet) {
        final long userId = packet.get("user_id").asLong();
        final long channelId = packet.get("channel_id").asLong();
        final TextChannel channel = api.getTextChannelById(channelId).orElse(null);

        ServerImpl server = null;
        if (packet.hasNonNull("guild_id")) {
            final long serverId = packet.get("guild_id").asLong();
            server = (ServerImpl) api.getPossiblyUnreadyServerById(serverId).orElseThrow(AssertionError::new);
        }

        MemberImpl member = null;
        if (packet.hasNonNull("member") && server != null) {
            member = new MemberImpl(api, server, packet.get("member"), null);
        }

        if (channel != null) {
            final UserStartTypingEvent event = new UserStartTypingEventImpl(channel, userId, member);
            api.getEventDispatcher().dispatchUserStartTypingEvent(
                    server != null ? server : api,
                    server,
                    channel,
                    userId,
                    event);
        }
    }

}
