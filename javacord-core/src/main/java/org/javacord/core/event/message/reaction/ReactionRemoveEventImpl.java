package org.javacord.core.event.message.reaction;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.event.message.reaction.ReactionRemoveEvent;

/**
 * The implementation of {@link ReactionRemoveEvent}.
 */
public class ReactionRemoveEventImpl extends SingleReactionEventImpl implements ReactionRemoveEvent {

    /**
     * Creates a new reaction remove event.
     *
     * @param api The discord api instance.
     * @param messageId The id of the message.
     * @param channel The text channel in which the message was sent.
     * @param emoji The emoji.
     * @param userId The id of the user whose reaction got removed.
     */
    public ReactionRemoveEventImpl(final DiscordApi api, final long messageId, final TextChannel channel, final Emoji emoji, final long userId) {
        super(api, messageId, channel, emoji, userId);
    }

}
