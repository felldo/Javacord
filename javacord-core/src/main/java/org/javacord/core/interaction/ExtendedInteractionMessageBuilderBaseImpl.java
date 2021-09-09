package org.javacord.core.interaction;

import org.javacord.api.entity.Icon;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.internal.InteractionMessageBuilderDelegate;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.callback.ExtendedInteractionMessageBuilderBase;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

public abstract class ExtendedInteractionMessageBuilderBaseImpl<T>
        extends InteractionMessageBuilderBaseImpl<T>
        implements ExtendedInteractionMessageBuilderBase<T> {


    /**
     * Class constructor.
     *
     * @param myClass The interface to cast to for call chaining.
     */
    public ExtendedInteractionMessageBuilderBaseImpl(final Class<T> myClass) {
        super(myClass);
    }

    /**
     * Class constructor.
     *
     * @param myClass The interface to cast to for call chaining.
     * @param delegate The delegate to use if required.
     */
    public ExtendedInteractionMessageBuilderBaseImpl(final Class<T> myClass, final InteractionMessageBuilderDelegate delegate) {
        super(myClass, delegate);
    }

    @Override
    public T copy(final Message message) {
        delegate.copy(message);
        return myClass.cast(this);
    }

    @Override
    public T copy(final InteractionBase interaction) {
        delegate.copy(interaction);
        return myClass.cast(this);
    }

    @Override
    public T addFile(final BufferedImage image, final String fileName) {
        delegate.addFile(image, fileName);
        return myClass.cast(this);
    }

    @Override
    public T addFile(final File file) {
        delegate.addFile(file);
        return myClass.cast(this);
    }

    @Override
    public T addFile(final Icon icon) {
        delegate.addFile(icon);
        return myClass.cast(this);
    }

    @Override
    public T addFile(final URL url) {
        delegate.addFile(url);
        return myClass.cast(this);
    }

    @Override
    public T addFile(final byte[] bytes, final String fileName) {
        delegate.addFile(bytes, fileName);
        return myClass.cast(this);
    }

    @Override
    public T addFile(final InputStream stream, final String fileName) {
        delegate.addFile(stream, fileName);
        return myClass.cast(this);
    }

    @Override
    public T addFileAsSpoiler(final BufferedImage image, final String fileName) {
        delegate.addFile(image, "SPOILER_" + fileName);
        return myClass.cast(this);
    }

    @Override
    public T addFileAsSpoiler(final File file) {
        delegate.addFileAsSpoiler(file);
        return myClass.cast(this);
    }

    @Override
    public T addFileAsSpoiler(final Icon icon) {
        delegate.addFileAsSpoiler(icon);
        return myClass.cast(this);
    }

    @Override
    public T addFileAsSpoiler(final URL url) {
        delegate.addFileAsSpoiler(url);
        return myClass.cast(this);
    }

    @Override
    public T addFileAsSpoiler(final byte[] bytes, final String fileName) {
        delegate.addFile(bytes, "SPOILER_" + fileName);
        return myClass.cast(this);
    }

    @Override
    public T addFileAsSpoiler(final InputStream stream, final String fileName) {
        delegate.addFile(stream, "SPOILER_" + fileName);
        return myClass.cast(this);
    }

    @Override
    public T addAttachment(final BufferedImage image, final String fileName) {
        delegate.addAttachment(image, fileName);
        return myClass.cast(this);
    }

    @Override
    public T addAttachment(final File file) {
        delegate.addAttachment(file);
        return myClass.cast(this);
    }

    @Override
    public T addAttachment(final Icon icon) {
        delegate.addAttachment(icon);
        return myClass.cast(this);
    }

    @Override
    public T addAttachment(final URL url) {
        delegate.addAttachment(url);
        return myClass.cast(this);
    }

    @Override
    public T addAttachment(final byte[] bytes, final String fileName) {
        delegate.addAttachment(bytes, fileName);
        return myClass.cast(this);
    }

    @Override
    public T addAttachment(final InputStream stream, final String fileName) {
        delegate.addAttachment(stream, fileName);
        return myClass.cast(this);
    }

    @Override
    public T addAttachmentAsSpoiler(final BufferedImage image, final String fileName) {
        delegate.addAttachment(image, "SPOILER_" + fileName);
        return myClass.cast(this);
    }

    @Override
    public T addAttachmentAsSpoiler(final File file) {
        delegate.addAttachmentAsSpoiler(file);
        return myClass.cast(this);
    }

    @Override
    public T addAttachmentAsSpoiler(final Icon icon) {
        delegate.addAttachmentAsSpoiler(icon);
        return myClass.cast(this);
    }

    @Override
    public T addAttachmentAsSpoiler(final URL url) {
        delegate.addAttachmentAsSpoiler(url);
        return myClass.cast(this);
    }

    @Override
    public T addAttachmentAsSpoiler(final byte[] bytes, final String fileName) {
        delegate.addAttachment(bytes, "SPOILER_" + fileName);
        return myClass.cast(this);
    }

    @Override
    public T addAttachmentAsSpoiler(final InputStream stream, final String fileName) {
        delegate.addAttachment(stream, "SPOILER_" + fileName);
        return myClass.cast(this);
    }
}
