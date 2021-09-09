package org.javacord.core.entity.message;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.internal.InteractionMessageBuilderDelegate;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.core.entity.message.embed.EmbedBuilderDelegateImpl;
import org.javacord.core.interaction.InteractionImpl;
import org.javacord.core.util.FileContainer;
import org.javacord.core.util.rest.RestEndpoint;
import org.javacord.core.util.rest.RestMethod;
import org.javacord.core.util.rest.RestRequest;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

public class InteractionMessageBuilderDelegateImpl extends MessageBuilderBaseDelegateImpl
        implements InteractionMessageBuilderDelegate {

    /**
     * The message flags of the message.
     */
    private EnumSet<MessageFlag> messageFlags = null;

    @Override
    public void setFlags(final EnumSet<MessageFlag> messageFlags) {
        this.messageFlags = messageFlags;
    }

    @Override
    public CompletableFuture<Void> sendInitialResponse(final InteractionBase interaction) {
        final ObjectNode topBody = JsonNodeFactory.instance.objectNode();
        topBody.put("type", InteractionCallbackType.CHANNEL_MESSAGE_WITH_SOURCE.getId());
        final ObjectNode body = topBody.putObject("data");
        prepareInteractionWebhookBodyParts(body);

        return new RestRequest<Void>(interaction.getApi(),
                RestMethod.POST, RestEndpoint.INTERACTION_RESPONSE)
                .setUrlParameters(interaction.getIdAsString(), interaction.getToken())
                .setBody(topBody)
                .execute(result -> null);
    }

    @Override
    public CompletableFuture<Void> deleteInitialResponse(final InteractionBase interaction) {
        return new RestRequest<Void>(interaction.getApi(),
                RestMethod.DELETE, RestEndpoint.ORIGINAL_INTERACTION_RESPONSE)
                .setUrlParameters(Long.toUnsignedString(interaction.getApplicationId()), interaction.getToken())
                .execute(result -> null);
    }

    @Override
    public CompletableFuture<Message> editOriginalResponse(final InteractionBase interaction) {
        final RestRequest<Message> request = new RestRequest<Message>(interaction.getApi(),
                RestMethod.PATCH, RestEndpoint.ORIGINAL_INTERACTION_RESPONSE)
                .setUrlParameters(Long.toUnsignedString(interaction.getApplicationId()), interaction.getToken());

        return executeResponse(request);
    }

    @Override
    public CompletableFuture<Message> sendFollowupMessage(final InteractionBase interaction) {
        final RestRequest<Message> request = new RestRequest<Message>(interaction.getApi(),
                RestMethod.POST, RestEndpoint.WEBHOOK_SEND)
                .setUrlParameters(Long.toUnsignedString(interaction.getApplicationId()), interaction.getToken());

        return executeResponse(request);
    }

    @Override
    public CompletableFuture<Void> updateOriginalMessage(final InteractionBase interaction) {
        final ObjectNode topBody = JsonNodeFactory.instance.objectNode();
        final ObjectNode data = JsonNodeFactory.instance.objectNode();
        prepareCommonWebhookMessageBodyParts(data);
        prepareComponents(data, true);
        topBody.put("type", InteractionCallbackType.UPDATE_MESSAGE.getId());
        topBody.set("data", data);

        return new RestRequest<Void>(interaction.getApi(),
                RestMethod.POST, RestEndpoint.INTERACTION_RESPONSE)
                .setUrlParameters(interaction.getIdAsString(), interaction.getToken())
                .setBody(topBody)
                .execute(result -> null);
    }

    @Override
    public CompletableFuture<Void> deleteFollowupMessage(final InteractionBase interaction, final String messageId) {
        return new RestRequest<Void>(interaction.getApi(), RestMethod.DELETE,
                RestEndpoint.WEBHOOK_MESSAGE)
                .setUrlParameters(Long.toUnsignedString(interaction.getApplicationId()),
                        interaction.getToken(), messageId)
                .execute(result -> null);
    }

    @Override
    public CompletableFuture<Message> editFollowupMessage(final InteractionBase interaction, final String messageId) {
        final RestRequest<Message> request = new RestRequest<Message>(interaction.getApi(), RestMethod.PATCH,
                RestEndpoint.WEBHOOK_MESSAGE)
                .setUrlParameters(Long.toUnsignedString(interaction.getApplicationId()),
                        interaction.getToken(), messageId);

        return executeResponse(request);
    }

    @Override
    public void copy(final InteractionBase interaction) {
        ((InteractionImpl) interaction).asMessageComponentInteraction()
                .map(MessageComponentInteraction::getMessage)
                .ifPresent(this::copy);
    }

    private CompletableFuture<Message> executeResponse(final RestRequest<Message> request) {
        final ObjectNode body = JsonNodeFactory.instance.objectNode();
        prepareInteractionWebhookBodyParts(body);

        return checkForAttachmentsAndExecuteRequest(request, body);
    }

    private void prepareInteractionWebhookBodyParts(final ObjectNode body) {
        prepareCommonWebhookMessageBodyParts(body);
        prepareComponents(body, true);
        if (null != messageFlags) {
            body.put("flags", messageFlags.stream().mapToInt(MessageFlag::getId).sum());
        }
    }

    private CompletableFuture<Message> checkForAttachmentsAndExecuteRequest(final RestRequest<Message> request,
                                                                            final ObjectNode body) {
        if (!attachments.isEmpty() || embeds.stream().anyMatch(EmbedBuilder::requiresAttachments)) {
            final CompletableFuture<Message> future = new CompletableFuture<>();
            // We access files etc. so this should be async
            request.getApi().getThreadPool().getExecutorService().submit(() -> {
                try {
                    final List<FileContainer> tempAttachments = new ArrayList<>(attachments);
                    // Add the attachments required for the embed
                    for (final EmbedBuilder embed : embeds) {
                        tempAttachments.addAll(
                                ((EmbedBuilderDelegateImpl) embed.getDelegate()).getRequiredAttachments());
                    }

                    addMultipartBodyToRequest(request, body, tempAttachments, request.getApi());

                    request.execute(result -> request.getApi().getOrCreateMessage(
                            request.getApi().getTextChannelById(result.getJsonBody().get("channel_id").asLong())
                                    .orElseThrow(() -> new NoSuchElementException("TextChannel is not cached")),
                            result.getJsonBody()))
                            .whenComplete((message, throwable) -> {
                                if (throwable != null) {
                                    future.completeExceptionally(throwable);
                                } else {
                                    future.complete(message);
                                }
                            });
                } catch (final Throwable t) {
                    future.completeExceptionally(t);
                }
            });
            return future;
        } else {
            request.setBody(body);
            return request.execute(result -> request.getApi().getOrCreateMessage(
                    request.getApi().getTextChannelById(result.getJsonBody().get("channel_id").asLong())
                            .orElseThrow(() -> new NoSuchElementException("TextChannel is not cached")),
                    result.getJsonBody()));
        }
    }
}
