package org.javacord.core.util.handler.message;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.core.entity.channel.PrivateChannelImpl;
import org.javacord.core.entity.user.MemberImpl;
import org.javacord.core.entity.user.UserImpl;
import org.javacord.core.event.message.MessageCreateEventImpl;
import org.javacord.core.util.event.DispatchQueueSelector;
import org.javacord.core.util.gateway.PacketHandler;
import org.javacord.core.util.logging.LoggerUtil;

import java.util.Optional;

/**
 * Handles the message create packet.
 */
public class MessageCreateHandler extends PacketHandler {

    /**
     * The logger of this class.
     */
    private static final Logger logger = LoggerUtil.getLogger(MessageCreateHandler.class);

    /**
     * Creates a new instance of this class.
     *
     * @param api The api.
     */
    public MessageCreateHandler(final DiscordApi api) {
        super(api, true, "MESSAGE_CREATE");
    }

    @Override
    public void handle(final JsonNode packet) {
        final long channelId = packet.get("channel_id").asLong();

        // if the message isn't from a server (or ephemeral)
        // See https://github.com/discord/discord-api-docs/issues/2248
        if (!packet.hasNonNull("guild_id")) {
            // Check for EPHEMERAL messages as they do NOT include a guild_id when the EPHEMERAL flag is set.
            if (packet.hasNonNull("flags") && (packet.get("flags").asInt() & MessageFlag.EPHEMERAL.getId()) > 0) {
                final Optional<ServerTextChannel> serverTextChannel = api.getServerTextChannelById(channelId);
                if (serverTextChannel.isPresent()) {
                    handle(serverTextChannel.get(), packet);
                    return;
                }
            }

            final UserImpl author = new UserImpl(api, packet.get("author"), (MemberImpl) null, null);

            final PrivateChannelImpl privateChannel = PrivateChannelImpl
                    .getOrCreatePrivateChannel(api, channelId, author.getId(), author);

            handle(privateChannel, packet);
            return;
        }

        final Optional<TextChannel> optionalChannel = api.getTextChannelById(channelId);
        if (optionalChannel.isPresent()) {
            handle(optionalChannel.get(), packet);
        } else {
            LoggerUtil.logMissingChannel(logger, channelId);
        }
    }

    private void handle(final TextChannel channel, final JsonNode packet) {
        final Message message = api.getOrCreateMessage(channel, packet);
        final MessageCreateEvent event = new MessageCreateEventImpl(message);

        final Optional<Server> optionalServer = channel.asServerChannel().map(ServerChannel::getServer);
        final MessageAuthor author = message.getAuthor();
        api.getEventDispatcher().dispatchMessageCreateEvent(
                optionalServer.map(DispatchQueueSelector.class::cast).orElse(api),
                optionalServer.orElse(null),
                channel,
                author.asUser().orElse(null),
                author.isWebhook() ? author.getId() : null,
                event);
    }

}
