package org.javacord.core.interaction;

import org.javacord.api.entity.Icon;
import org.javacord.api.entity.Mentionable;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageDecoration;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.HighLevelComponent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.internal.InteractionMessageBuilderDelegate;
import org.javacord.api.entity.message.mention.AllowedMentions;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;

public class InteractionOriginalResponseUpdaterImpl
        extends ExtendedInteractionMessageBuilderBaseImpl<InteractionOriginalResponseUpdater>
        implements InteractionOriginalResponseUpdater {

    private final InteractionImpl interaction;

    /**
     * Class constructor.
     *
     * @param interaction The interaction to use.
     * @param delegate    An already used delegate if the caller just sent the initial response
     */
    public InteractionOriginalResponseUpdaterImpl(final InteractionBase interaction,
                                                  final InteractionMessageBuilderDelegate delegate) {
        super(InteractionOriginalResponseUpdater.class, delegate);
        this.interaction = (InteractionImpl) interaction;
    }

    /**
     * Class constructor.
     *
     * @param interaction The interaction to use.
     */
    public InteractionOriginalResponseUpdaterImpl(final InteractionBase interaction) {
        super(InteractionOriginalResponseUpdater.class);
        this.interaction = (InteractionImpl) interaction;
    }

    @Override
    public CompletableFuture<Message> update() {
        return this.delegate.editOriginalResponse(interaction);
    }

    @Override
    public CompletableFuture<Void> delete() {
        return this.delegate.deleteInitialResponse(interaction);
    }

    @Override
    public InteractionOriginalResponseUpdater appendCode(final String language, final String code) {
        delegate.appendCode(language, code);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater append(final String message, final MessageDecoration... decorations) {
        delegate.append(message, decorations);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater append(final Mentionable entity) {
        delegate.append(entity);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater append(final Object object) {
        delegate.append(object);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater appendNewLine() {
        delegate.appendNewLine();
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater setContent(final String content) {
        delegate.setContent(content);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addEmbed(final EmbedBuilder embed) {
        delegate.addEmbed(embed);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addEmbeds(final EmbedBuilder... embeds) {
        delegate.addEmbeds(Arrays.asList(embeds));
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addComponents(final HighLevelComponent... components) {
        delegate.addComponents(components);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater copy(final Message message) {
        delegate.copy(message);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater copy(final InteractionBase interaction) {
        delegate.copy(interaction);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater removeAllComponents() {
        delegate.removeAllComponents();
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater removeComponent(final int index) {
        delegate.removeComponent(index);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater removeComponent(final HighLevelComponent component) {
        delegate.removeComponent(component);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater removeEmbed(final EmbedBuilder embed) {
        delegate.removeEmbed(embed);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater removeEmbeds(final EmbedBuilder... embeds) {
        delegate.removeEmbeds(embeds);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater removeAllEmbeds() {
        delegate.removeAllEmbeds();
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater setTts(final boolean tts) {
        delegate.setTts(tts);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addFile(final BufferedImage image, final String fileName) {
        delegate.addFile(image, fileName);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addFile(final File file) {
        delegate.addFile(file);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addFile(final Icon icon) {
        delegate.addFile(icon);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addFile(final URL url) {
        delegate.addFile(url);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addFile(final byte[] bytes, final String fileName) {
        delegate.addFile(bytes, fileName);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addFile(final InputStream stream, final String fileName) {
        delegate.addFile(stream, fileName);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addFileAsSpoiler(final BufferedImage image, final String fileName) {
        delegate.addFile(image, "SPOILER_" + fileName);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addFileAsSpoiler(final File file) {
        delegate.addFileAsSpoiler(file);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addFileAsSpoiler(final Icon icon) {
        delegate.addFileAsSpoiler(icon);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addFileAsSpoiler(final URL url) {
        delegate.addFileAsSpoiler(url);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addFileAsSpoiler(final byte[] bytes, final String fileName) {
        delegate.addFile(bytes, "SPOILER_" + fileName);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addFileAsSpoiler(final InputStream stream, final String fileName) {
        delegate.addFile(stream, "SPOILER_" + fileName);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addAttachment(final BufferedImage image, final String fileName) {
        delegate.addAttachment(image, fileName);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addAttachment(final File file) {
        delegate.addAttachment(file);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addAttachment(final Icon icon) {
        delegate.addAttachment(icon);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addAttachment(final URL url) {
        delegate.addAttachment(url);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addAttachment(final byte[] bytes, final String fileName) {
        delegate.addAttachment(bytes, fileName);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addAttachment(final InputStream stream, final String fileName) {
        delegate.addAttachment(stream, fileName);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addAttachmentAsSpoiler(final BufferedImage image, final String fileName) {
        delegate.addAttachment(image, "SPOILER_" + fileName);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addAttachmentAsSpoiler(final File file) {
        delegate.addAttachmentAsSpoiler(file);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addAttachmentAsSpoiler(final Icon icon) {
        delegate.addAttachmentAsSpoiler(icon);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addAttachmentAsSpoiler(final URL url) {
        delegate.addAttachmentAsSpoiler(url);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addAttachmentAsSpoiler(final byte[] bytes, final String fileName) {
        delegate.addAttachment(bytes, "SPOILER_" + fileName);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater addAttachmentAsSpoiler(final InputStream stream, final String fileName) {
        delegate.addAttachment(stream, "SPOILER_" + fileName);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater setAllowedMentions(final AllowedMentions allowedMentions) {
        delegate.setAllowedMentions(allowedMentions);
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater setFlags(final MessageFlag... messageFlags) {
        setFlags(EnumSet.copyOf(Arrays.asList(messageFlags)));
        return this;
    }

    @Override
    public InteractionOriginalResponseUpdater setFlags(final EnumSet<MessageFlag> messageFlags) {
        delegate.setFlags(messageFlags);
        return this;
    }
}
