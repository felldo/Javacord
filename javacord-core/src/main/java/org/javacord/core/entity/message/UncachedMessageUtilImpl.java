package org.javacord.core.entity.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.UncachedMessageUtil;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.core.DiscordApiImpl;
import org.javacord.core.entity.message.embed.EmbedBuilderDelegateImpl;
import org.javacord.core.entity.user.MemberImpl;
import org.javacord.core.entity.user.UserImpl;
import org.javacord.core.listener.message.InternalUncachedMessageAttachableListenerManager;
import org.javacord.core.util.rest.RestEndpoint;
import org.javacord.core.util.rest.RestMethod;
import org.javacord.core.util.rest.RestRequest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This class provides methods to interact with messages without having an instance of it.
 */
public class UncachedMessageUtilImpl implements UncachedMessageUtil, InternalUncachedMessageAttachableListenerManager {

    private final DiscordApiImpl api;

    /**
     * Creates a new instance of this class.
     *
     * @param api The discord api instance.
     */
    public UncachedMessageUtilImpl(final DiscordApiImpl api) {
        this.api = api;
    }

    @Override
    public CompletableFuture<Message> crossPost(final String channelId, final String messageId) {
        return new RestRequest<Message>(api, RestMethod.POST, RestEndpoint.MESSAGE)
                .setUrlParameters(channelId, messageId, "crosspost")
                .execute(result ->
                        new MessageImpl(api, api.getTextChannelById(channelId).orElseThrow(() ->
                                new IllegalStateException("TextChannel is missing.")), result.getJsonBody()));
    }

    @Override
    public CompletableFuture<Void> delete(final long channelId, final long messageId) {
        return delete(channelId, messageId, null);
    }

    @Override
    public CompletableFuture<Void> delete(final String channelId, final String messageId) {
        return delete(channelId, messageId, null);
    }

    @Override
    public CompletableFuture<Void> delete(final long channelId, final long messageId, final String reason) {
        return new RestRequest<Void>(api, RestMethod.DELETE, RestEndpoint.MESSAGE_DELETE)
                .setUrlParameters(Long.toUnsignedString(channelId), Long.toUnsignedString(messageId))
                .setAuditLogReason(reason)
                .execute(result -> null);
    }

    @Override
    public CompletableFuture<Void> delete(final String channelId, final String messageId, final String reason) {
        try {
            return delete(Long.parseLong(channelId), Long.parseLong(messageId), reason);
        } catch (final NumberFormatException e) {
            final CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Override
    public CompletableFuture<Void> delete(final long channelId, final long... messageIds) {
        // split by younger than two weeks / older than two weeks
        final Instant twoWeeksAgo = Instant.now().minus(14, ChronoUnit.DAYS);
        final Map<Boolean, List<Long>> messageIdsByAge = Arrays.stream(messageIds).distinct().boxed()
                .collect(Collectors.groupingBy(
                        messageId -> DiscordEntity.getCreationTimestamp(messageId).isAfter(twoWeeksAgo)));

        final AtomicInteger batchCounter = new AtomicInteger();
        return CompletableFuture.allOf(Stream.concat(
                // for messages younger than 2 weeks
                messageIdsByAge.getOrDefault(true, Collections.emptyList()).stream()
                        // send batches of 100 messages
                        .collect(Collectors.groupingBy(messageId -> batchCounter.getAndIncrement() / 100))
                        .values().stream()
                        .map(messageIdBatch -> {
                            // do not use batch deletion for a single message
                            if (messageIdBatch.size() == 1) {
                                return Message.delete(api, channelId, messageIdBatch.get(0));
                            }

                            final ObjectNode body = JsonNodeFactory.instance.objectNode();
                            final ArrayNode messages = body.putArray("messages");
                            messageIdBatch.stream()
                                    .map(Long::toUnsignedString)
                                    .forEach(messages::add);

                            return new RestRequest<Void>(api, RestMethod.POST, RestEndpoint.MESSAGES_BULK_DELETE)
                                    .setUrlParameters(Long.toUnsignedString(channelId))
                                    .setBody(body)
                                    .execute(result -> null);
                        }),
                // for messages older than 2 weeks use single message deletion
                messageIdsByAge.getOrDefault(false, Collections.emptyList()).stream()
                        .map(messageId -> Message.delete(api, channelId, messageId))
        ).toArray(CompletableFuture[]::new));
    }

    @Override
    public CompletableFuture<Void> delete(final String channelId, final String... messageIds) {
        final long[] messageLongIds = Arrays.stream(messageIds).filter(s -> {
            try {
                //noinspection ResultOfMethodCallIgnored
                Long.parseLong(s);
                return true;
            } catch (final NumberFormatException e) {
                return false;
            }
        }).mapToLong(Long::parseLong).toArray();
        return delete(Long.parseLong(channelId), messageLongIds);
    }

    @Override
    public CompletableFuture<Void> delete(final Message... messages) {
        return CompletableFuture.allOf(
                Arrays.stream(messages)
                        .collect(Collectors.groupingBy(message -> message.getChannel().getId(),
                                Collectors.mapping(Message::getId, Collectors.toList())))
                        .entrySet().stream()
                        .map(entry -> delete(entry.getKey(),
                                entry.getValue().stream().mapToLong(Long::longValue).toArray()))
                        .toArray(CompletableFuture[]::new));
    }

    @Override
    public CompletableFuture<Void> delete(final Iterable<Message> messages) {
        return delete(StreamSupport.stream(messages.spliterator(), false).toArray(Message[]::new));
    }

    @Override
    public CompletableFuture<Message> edit(final long channelId, final long messageId, final String content) {
        return edit(channelId, messageId, content, true, Collections.emptyList(), false);
    }

    @Override
    public CompletableFuture<Message> edit(final String channelId, final String messageId, final String content) {
        return edit(channelId, messageId, content, true, Collections.emptyList(), false);
    }

    @Override
    public CompletableFuture<Message> edit(final long channelId, final long messageId, final List<EmbedBuilder> embeds) {
        return edit(channelId, messageId, null, false, embeds, true);
    }

    @Override
    public CompletableFuture<Message> edit(final String channelId, final String messageId, final List<EmbedBuilder> embeds) {
        return edit(channelId, messageId, null, false, embeds, true);
    }

    @Override
    public CompletableFuture<Message> edit(
            final long channelId, final long messageId, final String content, final List<EmbedBuilder> embeds) {
        return edit(channelId, messageId, content, true, embeds, true);
    }

    @Override
    public CompletableFuture<Message> edit(
            final String channelId, final String messageId, final String content, final List<EmbedBuilder> embeds) {
        return edit(channelId, messageId, content, true, embeds, true);
    }

    @Override
    public CompletableFuture<Message> edit(final long channelId, final long messageId, final String content,
                                           final boolean updateContent, final List<EmbedBuilder> embeds, final boolean updateEmbed) {
        final ObjectNode body = JsonNodeFactory.instance.objectNode();
        if (updateContent) {
            if (content == null || content.isEmpty()) {
                body.putNull("content");
            } else {
                body.put("content", content);
            }
        }
        if (updateEmbed) {
            final ArrayNode embedArray = body.putArray("embeds");
            embeds.stream().map(embedBuilder -> ((EmbedBuilderDelegateImpl) embedBuilder.getDelegate()).toJsonNode())
                    .forEach(embedArray::add);
        }
        return new RestRequest<Message>(api, RestMethod.PATCH, RestEndpoint.MESSAGE)
                .setUrlParameters(Long.toUnsignedString(channelId), Long.toUnsignedString(messageId))
                .setBody(body)
                .execute(result -> new MessageImpl(api, api.getTextChannelById(channelId).orElseThrow(() ->
                        new IllegalStateException("TextChannel is missing.")), result.getJsonBody()));
    }

    @Override
    public CompletableFuture<Message> edit(final String channelId, final String messageId, final String content,
                                           final boolean updateContent, final List<EmbedBuilder> embeds, final boolean updateEmbed) {
        try {
            return edit(Long.parseLong(channelId), Long.parseLong(messageId), content, true, embeds, true);
        } catch (final NumberFormatException e) {
            final CompletableFuture<Message> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Override
    public CompletableFuture<Message> removeContent(final long channelId, final long messageId) {
        return edit(channelId, messageId, "");
    }

    @Override
    public CompletableFuture<Message> removeContent(final String channelId, final String messageId) {
        return edit(channelId, messageId, null, true, Collections.emptyList(), false);
    }

    @Override
    public CompletableFuture<Message> removeEmbed(final long channelId, final long messageId) {
        return edit(channelId, messageId, null, false, Collections.emptyList(), true);
    }

    @Override
    public CompletableFuture<Message> removeEmbed(final String channelId, final String messageId) {
        return edit(channelId, messageId, null, false, Collections.emptyList(), true);
    }

    @Override
    public CompletableFuture<Message> removeContentAndEmbed(final long channelId, final long messageId) {
        return edit(channelId, messageId, null, true, Collections.emptyList(), true);
    }

    @Override
    public CompletableFuture<Message> removeContentAndEmbed(final String channelId, final String messageId) {
        return edit(channelId, messageId, null, true, Collections.emptyList(), true);
    }

    @Override
    public CompletableFuture<Void> addReaction(final long channelId, final long messageId, final String unicodeEmoji) {
        return new RestRequest<Void>(api, RestMethod.PUT, RestEndpoint.REACTION)
                .setUrlParameters(
                        Long.toUnsignedString(channelId), Long.toUnsignedString(messageId), unicodeEmoji, "@me")
                .execute(result -> null);
    }

    @Override
    public CompletableFuture<Void> addReaction(
            final String channelId, final String messageId, final String unicodeEmoji) {
        try {
            return addReaction(Long.parseLong(channelId), Long.parseLong(messageId), unicodeEmoji);
        } catch (final NumberFormatException e) {
            final CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Override
    public CompletableFuture<Void> addReaction(final long channelId, final long messageId, final Emoji emoji) {
        final String value = emoji.asUnicodeEmoji().orElseGet(() ->
                emoji.asCustomEmoji()
                        .map(CustomEmoji::getReactionTag)
                        .orElse("UNKNOWN")
        );
        return new RestRequest<Void>(api, RestMethod.PUT, RestEndpoint.REACTION)
                .setUrlParameters(Long.toUnsignedString(channelId), Long.toUnsignedString(messageId), value, "@me")
                .execute(result -> null);
    }

    @Override
    public CompletableFuture<Void> addReaction(final String channelId, final String messageId, final Emoji emoji) {
        try {
            return addReaction(Long.parseLong(channelId), Long.parseLong(messageId), emoji);
        } catch (final NumberFormatException e) {
            final CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Override
    public CompletableFuture<Void> removeAllReactions(final long channelId, final long messageId) {
        return new RestRequest<Void>(api, RestMethod.DELETE, RestEndpoint.REACTION)
                .setUrlParameters(Long.toUnsignedString(channelId), Long.toUnsignedString(messageId))
                .execute(result -> null);
    }

    @Override
    public CompletableFuture<Void> removeAllReactions(final String channelId, final String messageId) {
        try {
            return removeAllReactions(Long.parseLong(channelId), Long.parseLong(messageId));
        } catch (final NumberFormatException e) {
            final CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Override
    public CompletableFuture<Void> pin(final long channelId, final long messageId) {
        return new RestRequest<Void>(api, RestMethod.PUT, RestEndpoint.PINS)
                .setUrlParameters(Long.toUnsignedString(channelId), Long.toUnsignedString(messageId))
                .execute(result -> null);
    }

    @Override
    public CompletableFuture<Void> pin(final String channelId, final String messageId) {
        try {
            return pin(Long.parseLong(channelId), Long.parseLong(messageId));
        } catch (final NumberFormatException e) {
            final CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Override
    public CompletableFuture<Void> unpin(final long channelId, final long messageId) {
        return new RestRequest<Void>(api, RestMethod.DELETE, RestEndpoint.PINS)
                .setUrlParameters(Long.toUnsignedString(channelId), Long.toUnsignedString(messageId))
                .execute(result -> null);
    }

    @Override
    public CompletableFuture<Void> unpin(final String channelId, final String messageId) {
        try {
            return unpin(Long.parseLong(channelId), Long.parseLong(messageId));
        } catch (final NumberFormatException e) {
            final CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Override
    public CompletableFuture<List<User>> getUsersWhoReactedWithEmoji(final long channelId, final long messageId, final Emoji emoji) {
        final CompletableFuture<List<User>> future = new CompletableFuture<>();
        api.getThreadPool().getExecutorService().submit(() -> {
            try {
                final String value = emoji.asUnicodeEmoji().orElseGet(() -> emoji.asCustomEmoji()
                        .map(CustomEmoji::getReactionTag).orElse("UNKNOWN"));
                final List<User> users = new ArrayList<>();
                boolean requestMore = true;
                while (requestMore) {
                    final RestRequest<List<User>> request =
                            new RestRequest<List<User>>(api, RestMethod.GET, RestEndpoint.REACTION)
                                    .setUrlParameters(
                                            Long.toUnsignedString(channelId), Long.toUnsignedString(messageId), value)
                                    .addQueryParameter("limit", "100");
                    if (!users.isEmpty()) {
                        request.addQueryParameter("after", users.get(users.size() - 1).getIdAsString());
                    }
                    final List<User> incompleteUsers = request.execute(result -> {
                        final List<User> paginatedUsers = new ArrayList<>();
                        for (final JsonNode userJson : result.getJsonBody()) {
                            paginatedUsers.add(new UserImpl(api, userJson, (MemberImpl) null, null));
                        }
                        return Collections.unmodifiableList(paginatedUsers);
                    }).join();
                    users.addAll(incompleteUsers);
                    requestMore = incompleteUsers.size() >= 100;
                }
                future.complete(Collections.unmodifiableList(users));
            } catch (final Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<List<User>> getUsersWhoReactedWithEmoji(final String channelId, final String messageId, final Emoji emoji) {
        try {
            return getUsersWhoReactedWithEmoji(Long.parseLong(channelId), Long.parseLong(messageId), emoji);
        } catch (final NumberFormatException e) {
            final CompletableFuture<List<User>> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Override
    public CompletableFuture<Void> removeUserReactionByEmoji(final long channelId, final long messageId, final Emoji emoji, final long userId) {
        final String value = emoji.asUnicodeEmoji().orElseGet(() ->
                emoji.asCustomEmoji().map(CustomEmoji::getReactionTag).orElse("UNKNOWN"));
        return new RestRequest<Void>(api, RestMethod.DELETE, RestEndpoint.REACTION)
                .setUrlParameters(
                        Long.toUnsignedString(channelId),
                        Long.toUnsignedString(messageId),
                        value,
                        api.getYourself().getId() == userId ? "@me" : String.valueOf(userId))
                .execute(result -> null);
    }

    @Override
    public CompletableFuture<Void> removeUserReactionByEmoji(final String channelId, final String messageId, final Emoji emoji,
                                                             final String userId) {
        try {
            return removeUserReactionByEmoji(
                    Long.parseLong(channelId), Long.parseLong(messageId), emoji, Long.parseLong(userId));
        } catch (final NumberFormatException e) {
            final CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Override
    public DiscordApi getApi() {
        return api;
    }

}
