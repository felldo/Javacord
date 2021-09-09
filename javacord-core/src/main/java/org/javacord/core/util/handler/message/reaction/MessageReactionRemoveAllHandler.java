package org.javacord.core.util.handler.message.reaction;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.reaction.ReactionRemoveAllEvent;
import org.javacord.core.entity.channel.PrivateChannelImpl;
import org.javacord.core.entity.message.MessageImpl;
import org.javacord.core.event.message.reaction.ReactionRemoveAllEventImpl;
import org.javacord.core.util.event.DispatchQueueSelector;
import org.javacord.core.util.gateway.PacketHandler;
import org.javacord.core.util.logging.LoggerUtil;

import java.util.Optional;

/**
 * Handles the message reaction remove all packet.
 */
public class MessageReactionRemoveAllHandler extends PacketHandler {

    /**
     * The logger of this class.
     */
    private static final Logger logger = LoggerUtil.getLogger(MessageReactionRemoveAllHandler.class);

    /**
     * Creates a new instance of this class.
     *
     * @param api The api.
     */
    public MessageReactionRemoveAllHandler(final DiscordApi api) {
        super(api, true, "MESSAGE_REACTION_REMOVE_ALL");
    }

    @Override
    public void handle(final JsonNode packet) {
        final long messageId = packet.get("message_id").asLong();
        final Optional<Message> message = api.getCachedMessageById(messageId);

        message.ifPresent(msg -> ((MessageImpl) msg).removeAllReactionsFromCache());

        final long channelId = packet.get("channel_id").asLong();
        TextChannel channel = api.getTextChannelById(channelId).orElse(null);
        if (channel == null) {
            if (packet.hasNonNull("guild_id")) {
                // we don't know anything about the channel as it is part of a server and not cached
                LoggerUtil.logMissingChannel(logger, channelId);
                return;
            }
            // channel is a private channel:
            channel = PrivateChannelImpl
                    .dispatchPrivateChannelCreateEvent(api, new PrivateChannelImpl(api, channelId, null, null));
        }

        final ReactionRemoveAllEvent event = new ReactionRemoveAllEventImpl(api, messageId, channel);

        final Optional<Server> optionalServer = channel.asServerChannel().map(ServerChannel::getServer);
        api.getEventDispatcher().dispatchReactionRemoveAllEvent(
                optionalServer.map(DispatchQueueSelector.class::cast).orElse(api),
                messageId,
                optionalServer.orElse(null),
                channel,
                event);
    }

}
