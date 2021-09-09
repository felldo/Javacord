package org.javacord.api.entity.message;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.webhook.IncomingWebhook;
import org.javacord.api.util.DiscordRegexPattern;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;

/**
 * This class can help you to create messages.
 */
public class MessageBuilder extends MessageBuilderBase<MessageBuilder> {

    /**
     * Class constructor.
     */
    public MessageBuilder() {
        super(MessageBuilder.class);
    }

    /**
     * Creates a message builder from a message.
     *
     * @param message The message to copy.
     * @return A message builder which would produce the same text as the given message.
     */
    public static MessageBuilder fromMessage(final Message message) {
        final MessageBuilder builder = new MessageBuilder();

        return builder.copy(message);
    }

    /**
     * Fill the builder's values with a message.
     *
     * @param message The message to copy.
     * @return The current instance in order to chain call methods.
     */
    public MessageBuilder copy(final Message message) {
        delegate.copy(message);
        return this;
    }

    /**
     * Sets if the message should be text to speech.
     *
     * @param tts Whether the message should be text to speech or not.
     * @return The current instance in order to chain call methods.
     */
    public MessageBuilder setTts(final boolean tts) {
        delegate.setTts(tts);
        return this;
    }

    /**
     * Sets the message to reply to.
     *
     * @param message The message to reply to.
     * @return The current instance in order to chain call methods.
     */
    public MessageBuilder replyTo(final Message message) {
        delegate.replyTo(message.getId());
        return this;
    }

    /**
     * Sets the message to reply to.
     *
     * @param messageId The id of the message to reply to.
     * @return The current instance in order to chain call methods.
     */
    public MessageBuilder replyTo(final long messageId) {
        delegate.replyTo(messageId);
        return this;
    }

    /**
     * Sends the message.
     *
     * @param user The user to which the message should be sent.
     * @return The sent message.
     */
    public CompletableFuture<Message> send(final User user) {
        return delegate.send(user);
    }

    /**
     * Sends the message.
     *
     * @param channel The channel in which the message should be sent.
     * @return The sent message.
     */
    public CompletableFuture<Message> send(final TextChannel channel) {
        return delegate.send(channel);
    }

    /**
     * Sends the message.
     *
     * @param webhook The webhook from which the message should be sent.
     * @return The sent message.
     */
    public CompletableFuture<Message> send(final IncomingWebhook webhook) {
        return delegate.send(webhook);
    }

    /**
     * Sends the message.
     *
     * @param messageable The receiver of the message.
     * @return The sent message.
     */
    public CompletableFuture<Message> send(final Messageable messageable) {
        return delegate.send(messageable);
    }

    /**
     * Sends the message.
     *
     * @param api The api instance needed to send and return the message.
     * @param webhookId The id of the webhook from which the message should be sent.
     * @param webhookToken The token of the webhook from which the message should be sent.
     * @return The sent message.
     */
    public CompletableFuture<Message> sendWithWebhook(final DiscordApi api, final long webhookId, final String webhookToken) {
        return delegate.sendWithWebhook(api, Long.toUnsignedString(webhookId), webhookToken);
    }

    /**
     * Sends the message.
     *
     * @param api The api instance needed to send and return the message.
     * @param webhookId The id of the webhook from which the message should be sent.
     * @param webhookToken The token of the webhook from which the message should be sent.
     * @return The sent message.
     */
    public CompletableFuture<Message> sendWithWebhook(final DiscordApi api, final String webhookId, final String webhookToken) {
        return delegate.sendWithWebhook(api, webhookId, webhookToken);
    }

    /**
     * Sends the message.
     *
     * @param api The api instance needed to send the message.
     * @param webhookUrl The url of the webhook from which the message should be sent.
     *
     * @return The sent message.
     * @throws IllegalArgumentException If the link isn't valid.
     */
    public CompletableFuture<Message> sendWithWebhook(final DiscordApi api, final String webhookUrl)
            throws IllegalArgumentException {
        final Matcher matcher = DiscordRegexPattern.WEBHOOK_URL.matcher(webhookUrl);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("The webhook url has an invalid format");
        }

        return sendWithWebhook(api, matcher.group("id"), matcher.group("token"));
    }
}
