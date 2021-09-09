package org.javacord.core.entity.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.Mentionable;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.MessageDecoration;
import org.javacord.api.entity.message.Messageable;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.ActionRowBuilder;
import org.javacord.api.entity.message.component.ComponentType;
import org.javacord.api.entity.message.component.HighLevelComponent;
import org.javacord.api.entity.message.component.LowLevelComponent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.internal.MessageBuilderBaseDelegate;
import org.javacord.api.entity.message.mention.AllowedMentions;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.webhook.IncomingWebhook;
import org.javacord.core.DiscordApiImpl;
import org.javacord.core.entity.message.component.ComponentImpl;
import org.javacord.core.entity.message.embed.EmbedBuilderDelegateImpl;
import org.javacord.core.entity.message.mention.AllowedMentionsImpl;
import org.javacord.core.entity.user.Member;
import org.javacord.core.util.FileContainer;
import org.javacord.core.util.logging.LoggerUtil;
import org.javacord.core.util.rest.RestEndpoint;
import org.javacord.core.util.rest.RestMethod;
import org.javacord.core.util.rest.RestRequest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The implementation of {@link MessageBuilderBaseDelegate}.
 */
public class MessageBuilderBaseDelegateImpl implements MessageBuilderBaseDelegate {

    /**
     * The logger of this class.
     */
    private static final Logger logger = LoggerUtil.getLogger(MessageBuilderBaseDelegateImpl.class);

    /**
     * The string builder used to create the message.
     */
    protected final StringBuilder strBuilder = new StringBuilder();

    /**
     * True if the content has been changed by the user.
     */
    protected boolean contentChanged = false;

    /**
     * The list of embeds of the message.
     */
    protected List<EmbedBuilder> embeds = new ArrayList<>();

    /**
     * True if embeds have been changed by the user.
     */
    protected boolean embedsChanged = false;

    /**
     * If the message should be text to speech or not.
     */
    protected boolean tts = false;

    /**
     * The nonce of the message.
     */
    protected String nonce = null;

    /**
     * A list with all attachments which should be added to the message.
     */
    protected final List<FileContainer> attachments = new ArrayList<>();

    /**
     * True if the attachments have been changed by the user.
     */
    protected boolean attachmentsChanged = false;

    /**
     * A list with all the components which should be added to the message.
     */
    protected final List<HighLevelComponent> components = new ArrayList<>();

    /**
     * True if the components have been changed by the user.
     */
    protected boolean componentsChanged = false;

    /**
     * The MentionsBuilder used to control mention behavior.
     */
    protected AllowedMentions allowedMentions = null;

    /**
     * The message to reply to.
     */
    protected Long replyingTo = null;


    @Override
    public void addComponents(final HighLevelComponent... highLevelComponents) {
        this.components.addAll(Arrays.asList(highLevelComponents));
        componentsChanged = true;
    }

    @Override
    public void addActionRow(final LowLevelComponent... lowLevelComponents) {
        this.addComponents(ActionRow.of(lowLevelComponents));
        componentsChanged = true;
    }

    @Override
    public void appendCode(final String language, final String code) {
        strBuilder
                .append("\n")
                .append(MessageDecoration.CODE_LONG.getPrefix())
                .append(language)
                .append("\n")
                .append(code)
                .append(MessageDecoration.CODE_LONG.getSuffix());
        contentChanged = true;
    }

    @Override
    public void append(final String message, final MessageDecoration... decorations) {
        for (final MessageDecoration decoration : decorations) {
            strBuilder.append(decoration.getPrefix());
        }
        strBuilder.append(message);
        for (int i = decorations.length - 1; i >= 0; i--) {
            strBuilder.append(decorations[i].getSuffix());
        }
        contentChanged = true;
    }

    @Override
    public void append(final Mentionable entity) {
        strBuilder.append(entity.getMentionTag());
        contentChanged = true;
    }

    @Override
    public void append(final Object object) {
        strBuilder.append(object);
        contentChanged = true;
    }

    @Override
    public void appendNewLine() {
        strBuilder.append("\n");
        contentChanged = true;
    }

    @Override
    public void setContent(final String content) {
        strBuilder.setLength(0);
        strBuilder.append(content);
        contentChanged = true;
    }

    @Override
    public void addEmbed(final EmbedBuilder embed) {
        if (embed != null) {
            embeds.add(embed);
            embedsChanged = true;
        }
    }

    /**
     * Fill the builder's values with a message.
     *
     * @param message The message to copy.
     */
    @Override
    public void copy(final Message message) {
        this.getStringBuilder().append(message.getContent());

        message.getEmbeds().forEach(embed -> addEmbed(embed.toBuilder()));

        for (final MessageAttachment attachment : message.getAttachments()) {
            // Since spoiler status is encoded in the file name, it is copied automatically.
            this.addAttachment(attachment.getUrl());
        }

        for (final HighLevelComponent component : message.getComponents()) {
            if (component.getType() == ComponentType.ACTION_ROW) {
                final ActionRowBuilder builder = new ActionRowBuilder();
                builder.copy((ActionRow) component);
                this.addComponents(builder.build());
            }
        }

        contentChanged = false;
        componentsChanged = false;
        attachmentsChanged = false;
        embedsChanged = false;
    }

    @Override
    public void removeAllEmbeds() {
        embeds.clear();
        embedsChanged = true;
    }

    @Override
    public void addEmbeds(final List<EmbedBuilder> embeds) {
        this.embeds.addAll(embeds);
        embedsChanged = true;
    }

    @Override
    public void removeEmbed(final EmbedBuilder embed) {
        this.embeds.remove(embed);
        embedsChanged = true;
    }

    @Override
    public void removeEmbeds(final EmbedBuilder... embeds) {
        this.embeds.removeAll(Arrays.asList(embeds));
        embedsChanged = true;
    }

    @Override
    public void removeComponent(final int index) {
        components.remove(index);
        componentsChanged = true;
    }

    @Override
    public void removeComponent(final HighLevelComponent component) {
        components.remove(component);
        componentsChanged = true;
    }

    @Override
    public void removeAllComponents() {
        components.clear();
        componentsChanged = true;
    }

    @Override
    public void setTts(final boolean tts) {
        this.tts = tts;
    }

    @Override
    public void addFile(final BufferedImage image, final String fileName) {
        addAttachment(image, fileName);
    }

    @Override
    public void addFile(final File file) {
        addAttachment(file);
    }

    @Override
    public void addFile(final Icon icon) {
        addAttachment(icon);
    }

    @Override
    public void addFile(final URL url) {
        addAttachment(url);
    }

    @Override
    public void addFile(final byte[] bytes, final String fileName) {
        addAttachment(bytes, fileName);
    }

    @Override
    public void addFile(final InputStream stream, final String fileName) {
        addAttachment(stream, fileName);
    }

    @Override
    public void addFileAsSpoiler(final File file) {
        addAttachmentAsSpoiler(file);
    }

    @Override
    public void addFileAsSpoiler(final Icon icon) {
        addAttachmentAsSpoiler(icon);
    }

    @Override
    public void addFileAsSpoiler(final URL url) {
        addAttachmentAsSpoiler(url);
    }

    @Override
    public void addAttachment(final BufferedImage image, final String fileName) {
        if (image == null || fileName == null) {
            throw new IllegalArgumentException("image and fileName cannot be null!");
        }
        attachments.add(new FileContainer(image, fileName));
        attachmentsChanged = true;
    }

    @Override
    public void addAttachment(final File file) {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null!");
        }
        attachments.add(new FileContainer(file));
        attachmentsChanged = true;
    }

    @Override
    public void addAttachment(final Icon icon) {
        if (icon == null) {
            throw new IllegalArgumentException("icon cannot be null!");
        }
        attachments.add(new FileContainer(icon));
        attachmentsChanged = true;
    }

    @Override
    public void addAttachment(final URL url) {
        if (url == null) {
            throw new IllegalArgumentException("url cannot be null!");
        }
        attachments.add(new FileContainer(url));
        attachmentsChanged = true;
    }

    @Override
    public void addAttachment(final byte[] bytes, final String fileName) {
        if (bytes == null || fileName == null) {
            throw new IllegalArgumentException("bytes and fileName cannot be null!");
        }
        attachments.add(new FileContainer(bytes, fileName));
        attachmentsChanged = true;
    }

    @Override
    public void addAttachment(final InputStream stream, final String fileName) {
        if (stream == null || fileName == null) {
            throw new IllegalArgumentException("stream and fileName cannot be null!");
        }
        attachments.add(new FileContainer(stream, fileName));
        attachmentsChanged = true;
    }

    @Override
    public void addAttachmentAsSpoiler(final File file) {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null!");
        }
        attachments.add(new FileContainer(file, true));
        attachmentsChanged = true;
    }

    @Override
    public void addAttachmentAsSpoiler(final Icon icon) {
        if (icon == null) {
            throw new IllegalArgumentException("icon cannot be null!");
        }
        attachments.add(new FileContainer(icon, true));
        attachmentsChanged = true;
    }

    @Override
    public void addAttachmentAsSpoiler(final URL url) {
        if (url == null) {
            throw new IllegalArgumentException("url cannot be null!");
        }
        attachments.add(new FileContainer(url, true));
        attachmentsChanged = true;
    }

    @Override
    public void setAllowedMentions(final AllowedMentions allowedMentions) {
        if (allowedMentions == null) {
            throw new IllegalArgumentException("mention cannot be null!");
        }
        this.allowedMentions = allowedMentions;
    }

    @Override
    public void replyTo(final long messageId) {
        replyingTo = messageId;
    }

    @Override
    public void setNonce(final String nonce) {
        this.nonce = nonce;
    }

    @Override
    public StringBuilder getStringBuilder() {
        return strBuilder;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Send methods
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public CompletableFuture<Message> send(final User user) {
        return send((Messageable) user);
    }

    @Override
    public CompletableFuture<Message> send(final Messageable messageable) {
        if (messageable == null) {
            throw new IllegalStateException("Cannot send message without knowing the receiver");
        }
        if (messageable instanceof TextChannel) {
            return send((TextChannel) messageable);
        } else if (messageable instanceof User) {
            return ((User) messageable).openPrivateChannel().thenCompose(this::send);
        } else if (messageable instanceof Member) {
            return send(((Member) messageable).getUser());
        } else if (messageable instanceof IncomingWebhook) {
            return send((IncomingWebhook) messageable);
        }
        throw new IllegalStateException("Messageable of unknown type");
    }

    @Override
    public CompletableFuture<Message> send(final TextChannel channel) {
        final ObjectNode body = JsonNodeFactory.instance.objectNode()
                .put("content", toString() == null ? "" : toString())
                .put("tts", tts);
        body.putArray("mentions");

        prepareAllowedMentions(body);

        prepareEmbeds(body, false);

        prepareComponents(body, false);

        if (nonce != null) {
            body.put("nonce", nonce);
        }

        if (replyingTo != null) {
            body.putObject("message_reference").put("message_id", replyingTo);
        }

        final RestRequest<Message> request = new RestRequest<Message>(channel.getApi(), RestMethod.POST, RestEndpoint.MESSAGE)
                .setUrlParameters(channel.getIdAsString());
        return checkForAttachmentsAndExecuteRequest(channel, body, request, false);
    }

    @Override
    public CompletableFuture<Message> send(final IncomingWebhook webhook) {
        return send(webhook.getIdAsString(), webhook.getToken(),
                null, null, true, webhook.getApi());
    }

    /**
     * Send a message to an incoming webhook.
     *
     * @param webhookId The id of the webhook to send the message to
     * @param webhookToken The token of the webhook to send the message to
     * @param displayName The display name the webhook should use
     * @param avatarUrl The avatar the webhook should use
     * @param wait If the completable future will be completed
     * @param api The api instance needed to send and return the message
     * @return The sent message
     */
    protected CompletableFuture<Message> send(final String webhookId, final String webhookToken, final String displayName, final URL avatarUrl,
                                              final boolean wait, final DiscordApi api) {
        final ObjectNode body = JsonNodeFactory.instance.objectNode();
        prepareCommonWebhookMessageBodyParts(body);

        if (displayName != null) {
            body.put("username", displayName);
        }
        if (avatarUrl != null) {
            body.put("avatar_url", avatarUrl.toExternalForm());
        }

        prepareComponents(body);

        if (strBuilder.length() != 0) {
            body.put("content", strBuilder.toString());
        }

        final RestRequest<Message> request =
                new RestRequest<Message>(api, RestMethod.POST, RestEndpoint.WEBHOOK_SEND)
                        .addQueryParameter("wait", Boolean.toString(wait))
                        .setUrlParameters(webhookId, webhookToken);
        final CompletableFuture<Message> future = new CompletableFuture<>();
        if (!attachments.isEmpty() || embeds.stream().anyMatch(EmbedBuilder::requiresAttachments)) {
            // We access files etc. so this should be async
            api.getThreadPool().getExecutorService().submit(() -> {
                try {
                    final List<FileContainer> tempAttachments = new ArrayList<>(attachments);
                    // Add the attachments required for the embeds
                    for (final EmbedBuilder embed : embeds) {
                        tempAttachments.addAll(
                                ((EmbedBuilderDelegateImpl) embed.getDelegate()).getRequiredAttachments());
                    }

                    addMultipartBodyToRequest(request, body, tempAttachments, api);

                    executeWebhookRest(request, wait, future, api);
                } catch (final Throwable t) {
                    future.completeExceptionally(t);
                }
            });
        } else {
            request.setBody(body);
            executeWebhookRest(request, wait, future, api);
        }
        return future;
    }

    @Override
    public CompletableFuture<Message> sendWithWebhook(final DiscordApi api, final String webhookId, final String webhookToken) {
        return send(webhookId, webhookToken, null, null, true, api);
    }

    /**
     * Method which executes the webhook rest request.
     *
     * @param request The rest request to execute
     * @param wait If discord sends us a response
     * @param future The future to complete
     * @param api The api instance needed to create the message
     */
    private static void executeWebhookRest(final RestRequest<Message> request, final boolean wait,
                                           final CompletableFuture<Message> future, final DiscordApi api) {
        final CompletableFuture<Message> tmpFuture;
        if (wait) {
            tmpFuture = request.execute(result -> {
                final JsonNode body = result.getJsonBody();
                final TextChannel channel = api.getTextChannelById(body.get("channel_id").asText()).orElseThrow(() ->
                        new IllegalStateException("Cannot return a message when the channel isn't cached!")
                );
                return ((DiscordApiImpl) api).getOrCreateMessage(channel, body);
            });
        } else {
            tmpFuture = request.execute(result -> null);
        }
        tmpFuture.whenComplete((message, throwable) -> {
            if (throwable != null) {
                future.completeExceptionally(throwable);
            } else {
                future.complete(message);
            }
        });
    }

    @Override
    public CompletableFuture<Message> edit(final Message message, final boolean updateAll) {
        final ObjectNode body = JsonNodeFactory.instance.objectNode();

        if (updateAll || contentChanged) {
            body.put("content", strBuilder.toString());
        }

        prepareAllowedMentions(body);

        prepareEmbeds(body, updateAll || embedsChanged);

        prepareComponents(body, updateAll || componentsChanged);

        final RestRequest<Message> request = new RestRequest<Message>(message.getApi(),
                RestMethod.PATCH, RestEndpoint.MESSAGE)
                .setUrlParameters(Long.toUnsignedString(message.getChannel().getId()),
                        Long.toUnsignedString(message.getId()));

        if (updateAll || attachmentsChanged) {
            return checkForAttachmentsAndExecuteRequest(message.getChannel(), body, request, true);
        } else {
            return executeRequestWithoutAttachments(message.getChannel(), body, request);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Internal MessageBuilder utility methods
    ////////////////////////////////////////////////////////////////////////////////

    private CompletableFuture<Message> checkForAttachmentsAndExecuteRequest(final TextChannel channel,
                                                                            final ObjectNode body,
                                                                            final RestRequest<Message> request,
                                                                            final boolean clearAttachmentsIfAppropriate) {
        if (attachments.isEmpty() && embeds.stream().noneMatch(EmbedBuilder::requiresAttachments)) {
            if (clearAttachmentsIfAppropriate) {
                body.set("attachments", JsonNodeFactory.instance.objectNode().arrayNode());
            }
            return executeRequestWithoutAttachments(channel, body, request);
        }

        final CompletableFuture<Message> future = new CompletableFuture<>();
        // We access files etc. so this should be async
        channel.getApi().getThreadPool().getExecutorService().submit(() -> {
            try {
                final List<FileContainer> tempAttachments = new ArrayList<>(attachments);
                // Add the attachments required for the embeds
                for (final EmbedBuilder embed : embeds) {
                    tempAttachments.addAll(
                            ((EmbedBuilderDelegateImpl) embed.getDelegate()).getRequiredAttachments());
                }

                addMultipartBodyToRequest(request, body, tempAttachments, channel.getApi());

                request.execute(result -> ((DiscordApiImpl) channel.getApi())
                                .getOrCreateMessage(channel, result.getJsonBody()))
                        .whenComplete((newMessage, throwable) -> {
                            if (throwable != null) {
                                future.completeExceptionally(throwable);
                            } else {
                                future.complete(newMessage);
                            }
                        });
            } catch (final Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    private CompletableFuture<Message> executeRequestWithoutAttachments(final TextChannel channel,
                                                                        final ObjectNode body,
                                                                        final RestRequest<Message> request) {
        request.setBody(body);
        return request.execute(result -> new MessageImpl((DiscordApiImpl) channel.getApi(), channel,
                result.getJsonBody()));
    }

    private void prepareAllowedMentions(final ObjectNode body) {
        if (allowedMentions != null) {
            ((AllowedMentionsImpl) allowedMentions).toJsonNode(body.putObject("allowed_mentions"));
        }
    }

    /**
     * Method which creates and adds a MultipartBody to a RestRequest.
     *
     * @param request The RestRequest to add the MultipartBody to
     * @param body The body to use as base for the MultipartBody
     * @param attachments The List of FileContainers to add as attachments
     * @param api The api instance needed to add the attachments
     */
    protected void addMultipartBodyToRequest(final RestRequest<?> request, final ObjectNode body,
                                             final List<FileContainer> attachments, final DiscordApi api) {
        final MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("payload_json", body.toString());

        Collections.reverse(attachments);
        for (int i = 0; i < attachments.size(); i++) {
            final byte[] bytes = attachments.get(i).asByteArray(api).join();

            String mediaType = URLConnection
                    .guessContentTypeFromName(attachments.get(i).getFileTypeOrName());
            if (mediaType == null) {
                mediaType = "application/octet-stream";
            }
            multipartBodyBuilder.addFormDataPart("file" + i, attachments.get(i).getFileTypeOrName(),
                    RequestBody.create(MediaType.parse(mediaType), bytes));
        }

        request.setMultipartBody(multipartBodyBuilder.build());
    }

    protected void prepareCommonWebhookMessageBodyParts(final ObjectNode body) {
        body.put("tts", this.tts);
        if (strBuilder.length() != 0) {
            body.put("content", strBuilder.toString());
        }
        prepareAllowedMentions(body);
        prepareEmbeds(body, false);
    }

    private void prepareEmbeds(final ObjectNode body, final boolean evenIfEmpty) {
        if (!embeds.isEmpty() || evenIfEmpty) {
            final ArrayNode embedsNode = JsonNodeFactory.instance.objectNode().arrayNode();
            for (final EmbedBuilder embed : embeds) {
                embedsNode.add(((EmbedBuilderDelegateImpl) embed.getDelegate()).toJsonNode());
            }
            body.set("embeds", embedsNode);
        }
    }

    @Override
    public String toString() {
        return strBuilder.toString();
    }

    protected void prepareComponents(final ObjectNode body) {
        prepareComponents(body, false);
    }

    protected void prepareComponents(final ObjectNode body, final boolean evenIfEmpty) {
        if (evenIfEmpty || !components.isEmpty()) {
            final ArrayNode componentsNode = JsonNodeFactory.instance.objectNode().arrayNode();
            components.forEach(
                    highLevelComponent -> componentsNode.add(((ComponentImpl) highLevelComponent).toJsonNode()));
            body.set("components", componentsNode);
        }
    }
}