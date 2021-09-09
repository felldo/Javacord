package org.javacord.api.entity.message;

import org.javacord.api.entity.message.component.HighLevelComponent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class represents an entity which can receive messages.
 */
public interface Messageable {

    /**
     * Sends a message.
     *
     * @param content  The content of the message.
     * @param embed    The embed which should be displayed.
     * @param tts      Whether the message should be "text to speech" or not.
     * @param nonce    The nonce of the message.
     * @param stream   The stream for the file to send.
     * @param fileName The name of the file.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(
            final String content, final EmbedBuilder embed, final boolean tts, final String nonce, final InputStream stream, final String fileName) {
        return new MessageBuilder()
                .append(content == null ? "" : content)
                .setEmbed(embed)
                .setTts(tts)
                .setNonce(nonce)
                .addAttachment(stream, fileName)
                .send(this);
    }

    /**
     * Sends a message.
     *
     * @param content The content of the message.
     * @param embed   The embed which should be displayed.
     * @param tts     Whether the message should be "text to speech" or not.
     * @param nonce   The nonce of the message.
     * @param files   The file(s) to send.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(
            final String content, final EmbedBuilder embed, final boolean tts, final String nonce, final File... files) {
        final MessageBuilder messageBuilder = new MessageBuilder()
                .append(content == null ? "" : content)
                .setEmbed(embed)
                .setTts(tts)
                .setNonce(nonce);
        for (final File file : files) {
            messageBuilder.addAttachment(file);
        }
        return messageBuilder.send(this);
    }

    /**
     * Sends a message.
     *
     * @param content The content of the message.
     * @param embed   The embed which should be displayed.
     * @param tts     Whether the message should be "text to speech" or not.
     * @param nonce   The nonce of the message.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final String content, final EmbedBuilder embed, final boolean tts, final String nonce) {
        return new MessageBuilder()
                .append(content == null ? "" : content)
                .setEmbed(embed)
                .setTts(tts)
                .setNonce(nonce)
                .send(this);
    }

    /**
     * Sends a message.
     *
     * @param content    The content of the message.
     * @param embed      The embed which should be displayed.
     * @param components High level components to add to the message, most probably of type ActionRow.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final String content,
                                                   final EmbedBuilder embed,
                                                   final HighLevelComponent... components) {
        return sendMessage(content, Collections.singletonList(embed), components);
    }

    /**
     * Sends a message.
     *
     * @param content    The content of the message.
     * @param embeds     A list of embeds which should be displayed.
     * @param components High level components to add to the message, most probably of type ActionRow.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final String content,
                                                   final List<EmbedBuilder> embeds,
                                                   final HighLevelComponent... components) {
        return new MessageBuilder()
                .append(content == null ? "" : content)
                .setEmbeds(embeds)
                .addComponents(components)
                .send(this);
    }

    /**
     * Sends a message.
     *
     * @param content The content of the message.
     * @param embeds  An array of the new embeds of the message.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final String content, final EmbedBuilder... embeds) {
        return new MessageBuilder()
                .append(content == null ? "" : content)
                .addEmbeds(embeds)
                .send(this);
    }

    /**
     * Sends a message.
     *
     * @param content    The content of the message.
     * @param components High level components to add to the message, most probably of type ActionRow.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final String content, final HighLevelComponent... components) {
        return new MessageBuilder()
                .append(content == null ? "" : content)
                .addComponents(components)
                .send(this);
    }

    /**
     * Sends a message.
     *
     * @param content The content of the message.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final String content) {
        return new MessageBuilder()
                .append(content == null ? "" : content)
                .send(this);
    }

    /**
     * Sends a message.
     *
     * @param embed      The embed which should be displayed.
     * @param components High level components to add to the message, most probably of type ActionRow.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final EmbedBuilder embed, final HighLevelComponent... components) {
        return sendMessage(Collections.singletonList(embed), components);
    }

    /**
     * Sends a message.
     *
     * @param embeds     A list of embeds which should be displayed.
     * @param components High level components to add to the message, most probably of type ActionRow.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final List<EmbedBuilder> embeds, final HighLevelComponent... components) {
        return new MessageBuilder()
                .setEmbeds(embeds)
                .addComponents(components)
                .send(this);
    }

    /**
     * Sends a message.
     *
     * @param embed The new embed of the message.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final EmbedBuilder embed) {
        return sendMessage(Collections.singletonList(embed));
    }

    /**
     * Sends a message.
     *
     * @param embeds An array of the new embeds of the message.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final EmbedBuilder... embeds) {
        return sendMessage(Arrays.asList(embeds));
    }

    /**
     * Sends a message.
     *
     * @param embeds A list of embeds which should be displayed.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final List<EmbedBuilder> embeds) {
        return new MessageBuilder()
                .setEmbeds(embeds)
                .send(this);
    }

    /**
     * Sends a message.
     *
     * @param files The file(s) to send.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final File... files) {
        final MessageBuilder messageBuilder = new MessageBuilder();
        for (final File file : files) {
            messageBuilder.addAttachment(file);
        }
        return messageBuilder.send(this);
    }

    /**
     * Sends a message.
     *
     * @param stream   The stream for the file to send.
     * @param fileName The name of the file.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final InputStream stream, final String fileName) {
        return new MessageBuilder()
                .addAttachment(stream, fileName)
                .send(this);
    }

    /**
     * Sends a message.
     *
     * @param content The content of the message.
     * @param files   The file(s) to send.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final String content, final File... files) {
        final MessageBuilder messageBuilder = new MessageBuilder()
                .append(content == null ? "" : content);
        for (final File file : files) {
            messageBuilder.addAttachment(file);
        }
        return messageBuilder.send(this);
    }

    /**
     * Sends a message.
     *
     * @param content  The content of the message.
     * @param stream   The stream for the file to send.
     * @param fileName The name of the file.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final String content, final InputStream stream, final String fileName) {
        return new MessageBuilder()
                .append(content == null ? "" : content)
                .addAttachment(stream, fileName)
                .send(this);
    }

    /**
     * Sends a message.
     *
     * @param embed The embed which should be displayed.
     * @param files The file(s) to send.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final EmbedBuilder embed, final File... files) {
        return sendMessage(Collections.singletonList(embed), files);
    }

    /**
     * Sends a message.
     *
     * @param embeds A list of embeds which should be displayed.
     * @param files  The file(s) to send.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final List<EmbedBuilder> embeds, final File... files) {
        final MessageBuilder messageBuilder = new MessageBuilder()
                .setEmbeds(embeds);
        for (final File file : files) {
            messageBuilder.addAttachment(file);
        }
        return messageBuilder.send(this);
    }

    /**
     * Sends a message.
     *
     * @param embed    The embed which should be displayed.
     * @param stream   The stream for the file to send.
     * @param fileName The name of the file.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final EmbedBuilder embed, final InputStream stream, final String fileName) {
        return sendMessage(Collections.singletonList(embed), stream, fileName);
    }

    /**
     * Sends a message.
     *
     * @param embeds   A list of embeds which should be displayed.
     * @param stream   The stream for the file to send.
     * @param fileName The name of the file.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final List<EmbedBuilder> embeds, final InputStream stream, final String fileName) {
        return new MessageBuilder()
                .setEmbeds(embeds)
                .addAttachment(stream, fileName)
                .send(this);
    }

    /**
     * Sends a message.
     *
     * @param content The content of the message.
     * @param embed   The embed which should be displayed.
     * @param files   The file(s) to send.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final String content, final EmbedBuilder embed, final File... files) {
        return sendMessage(content, Collections.singletonList(embed), files);
    }

    /**
     * Sends a message.
     *
     * @param content The content of the message.
     * @param embeds  A list of embeds which should be displayed.
     * @param files   The file(s) to send.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final String content, final List<EmbedBuilder> embeds, final File... files) {
        final MessageBuilder messageBuilder = new MessageBuilder()
                .append(content == null ? "" : content)
                .setEmbeds(embeds);
        for (final File file : files) {
            messageBuilder.addAttachment(file);
        }
        return messageBuilder.send(this);
    }

    /**
     * Sends a message.
     *
     * @param content  The content of the message.
     * @param embed    The embed which should be displayed.
     * @param stream   The stream for the file to send.
     * @param fileName The name of the file.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final String content, final EmbedBuilder embed, final InputStream stream,
                                                   final String fileName) {
        return new MessageBuilder()
                .append(content == null ? "" : content)
                .setEmbed(embed)
                .addAttachment(stream, fileName)
                .send(this);
    }

    /**
     * Sends a message.
     *
     * @param content  The content of the message.
     * @param embeds   A list of embeds which should be displayed.
     * @param stream   The stream for the file to send.
     * @param fileName The name of the file.
     * @return The sent message.
     */
    default CompletableFuture<Message> sendMessage(final String content, final List<EmbedBuilder> embeds, final InputStream stream,
                                                   final String fileName) {
        return new MessageBuilder()
                .append(content == null ? "" : content)
                .setEmbeds(embeds)
                .addAttachment(stream, fileName)
                .send(this);
    }
}
