package org.javacord.api.entity.message.embed;

import org.javacord.api.entity.Icon;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.internal.EmbedBuilderDelegate;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.internal.DelegateFactory;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * This class is used to create embeds.
 */
public class EmbedBuilder {

    /**
     * The embed delegate used by this instance.
     */
    private final EmbedBuilderDelegate delegate = DelegateFactory.createEmbedBuilderDelegate();

    /**
     * Gets the delegate used by this embed builder internally.
     *
     * @return The delegate used by this embed builder internally.
     */
    public EmbedBuilderDelegate getDelegate() {
        return delegate;
    }

    /**
     * Sets the title of the embed.
     *
     * @param title The title of the embed.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setTitle(final String title) {
        delegate.setTitle(title);
        return this;
    }

    /**
     * Sets the description of the embed.
     *
     * @param description The description of the embed.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setDescription(final String description) {
        delegate.setDescription(description);
        return this;
    }

    /**
     * Sets the url of the embed.
     *
     * @param url The url of the embed.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setUrl(final String url) {
        delegate.setUrl(url);
        return this;
    }

    /**
     * Sets the current time as timestamp of the embed.
     *
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setTimestampToNow() {
        delegate.setTimestampToNow();
        return this;
    }

    /**
     * Sets the timestamp of the embed.
     *
     * @param timestamp The timestamp to set.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setTimestamp(final Instant timestamp) {
        delegate.setTimestamp(timestamp);
        return this;
    }

    /**
     * Sets the color of the embed.
     *
     * @param color The color of the embed.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setColor(final Color color) {
        delegate.setColor(color);
        return this;
    }

    /**
     * Sets the footer of the embed.
     *
     * @param text The text of the footer.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setFooter(final String text) {
        delegate.setFooter(text);
        return this;
    }

    /**
     * Sets the footer of the embed.
     *
     * @param text The text of the footer.
     * @param iconUrl The url of the footer's icon.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setFooter(final String text, final String iconUrl) {
        delegate.setFooter(text, iconUrl);
        return this;
    }

    /**
     * Sets the footer of the embed.
     *
     * @param text The text of the footer.
     * @param icon The footer's icon.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setFooter(final String text, final Icon icon) {
        delegate.setFooter(text, icon);
        return this;
    }

    /**
     * Sets the footer of the embed.
     *
     * @param text The text of the footer.
     * @param icon The footer's icon.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setFooter(final String text, final File icon) {
        delegate.setFooter(text, icon);
        return this;
    }

    /**
     * Sets the footer of the embed.
     * This method assumes the file type is "png"!
     *
     * @param text The text of the footer.
     * @param icon The footer's icon.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setFooter(final String text, final InputStream icon) {
        delegate.setFooter(text, icon);
        return this;
    }

    /**
     * Sets the footer of the embed.
     *
     * @param text The text of the footer.
     * @param icon The footer's icon.
     * @param fileType The type of the file, e.g. "png" or "gif".
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setFooter(final String text, final InputStream icon, final String fileType) {
        delegate.setFooter(text, icon, fileType);
        return this;
    }

    /**
     * Sets the footer of the embed.
     * This method assumes the file type is "png"!
     *
     * @param text The text of the footer.
     * @param icon The footer's icon.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setFooter(final String text, final byte[] icon) {
        delegate.setFooter(text, icon);
        return this;
    }

    /**
     * Sets the footer of the embed.
     *
     * @param text The text of the footer.
     * @param icon The footer's icon.
     * @param fileType The type of the file, e.g. "png" or "gif".
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setFooter(final String text, final byte[] icon, final String fileType) {
        delegate.setFooter(text, icon, fileType);
        return this;
    }

    /**
     * Sets the footer of the embed.
     * This method assumes the file type is "png"!
     *
     * @param text The text of the footer.
     * @param icon The footer's icon.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setFooter(final String text, final BufferedImage icon) {
        delegate.setFooter(text, icon);
        return this;
    }

    /**
     * Sets the footer of the embed.
     *
     * @param text The text of the footer.
     * @param icon The footer's icon.
     * @param fileType The type of the file, e.g. "png" or "gif".
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setFooter(final String text, final BufferedImage icon, final String fileType) {
        delegate.setFooter(text, icon, fileType);
        return this;
    }

    /**
     * Sets the image of the embed.
     *
     * @param url The url of the image.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setImage(final String url) {
        delegate.setImage(url);
        return this;
    }

    /**
     * Sets the image of the embed.
     *
     * @param image The image.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setImage(final Icon image) {
        delegate.setImage(image);
        return this;
    }

    /**
     * Sets the image of the embed.
     *
     * @param image The image.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setImage(final File image) {
        delegate.setImage(image);
        return this;
    }

    /**
     * Sets the image of the embed.
     * This method assumes the file type is "png"!
     *
     * @param image The image.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setImage(final InputStream image) {
        delegate.setImage(image);
        return this;
    }

    /**
     * Sets the image of the embed.
     *
     * @param image The image.
     * @param fileType The type of the file, e.g. "png" or "gif".
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setImage(final InputStream image, final String fileType) {
        delegate.setImage(image, fileType);
        return this;
    }

    /**
     * Sets the image of the embed.
     * This method assumes the file type is "png"!
     *
     * @param image The image.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setImage(final byte[] image) {
        delegate.setImage(image);
        return this;
    }

    /**
     * Sets the image of the embed.
     *
     * @param image The image.
     * @param fileType The type of the file, e.g. "png" or "gif".
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setImage(final byte[] image, final String fileType) {
        delegate.setImage(image, fileType);
        return this;
    }

    /**
     * Sets the image of the embed.
     * This method assumes the file type is "png"!
     *
     * @param image The image.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setImage(final BufferedImage image) {
        delegate.setImage(image);
        return this;
    }

    /**
     * Sets the image of the embed.
     *
     * @param image The image.
     * @param fileType The type of the file, e.g. "png" or "gif".
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setImage(final BufferedImage image, final String fileType) {
        delegate.setImage(image, fileType);
        return this;
    }

    /**
     * Sets the author of the embed.
     *
     * @param author The message author which should be used as author.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setAuthor(final MessageAuthor author) {
        delegate.setAuthor(author);
        return this;
    }

    /**
     * Sets the author of the embed.
     *
     * @param author The user which should be used as author.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setAuthor(final User author) {
        delegate.setAuthor(author);
        return this;
    }

    /**
     * Sets the author of the embed.
     *
     * @param name The name of the author.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setAuthor(final String name) {
        delegate.setAuthor(name);
        return this;
    }

    /**
     * Sets the author of the embed.
     *
     * @param name The name of the author.
     * @param url The url of the author.
     * @param iconUrl The url of the author's icon.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setAuthor(final String name, final String url, final String iconUrl) {
        delegate.setAuthor(name, url, iconUrl);
        return this;
    }

    /**
     * Sets the author of the embed.
     *
     * @param name The name of the author.
     * @param url The url of the author.
     * @param icon The author's icon.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setAuthor(final String name, final String url, final Icon icon) {
        delegate.setAuthor(name, url, icon);
        return this;
    }

    /**
     * Sets the author of the embed.
     *
     * @param name The name of the author.
     * @param url The url of the author.
     * @param icon The author's icon.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setAuthor(final String name, final String url, final File icon) {
        delegate.setAuthor(name, url, icon);
        return this;
    }

    /**
     * Sets the author of the embed.
     * This method assumes the file type is "png"!
     *
     * @param name The name of the author.
     * @param url The url of the author.
     * @param icon The author's icon.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setAuthor(final String name, final String url, final InputStream icon) {
        delegate.setAuthor(name, url, icon);
        return this;
    }

    /**
     * Sets the author of the embed.
     *
     * @param name The name of the author.
     * @param url The url of the author.
     * @param icon The author's icon.
     * @param fileType The type of the file, e.g. "png" or "gif".
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setAuthor(final String name, final String url, final InputStream icon, final String fileType) {
        delegate.setAuthor(name, url, icon, fileType);
        return this;
    }

    /**
     * Sets the author of the embed.
     * This method assumes the file type is "png"!
     *
     * @param name The name of the author.
     * @param url The url of the author.
     * @param icon The author's icon.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setAuthor(final String name, final String url, final byte[] icon) {
        delegate.setAuthor(name, url, icon);
        return this;
    }

    /**
     * Sets the author of the embed.
     *
     * @param name The name of the author.
     * @param url The url of the author.
     * @param icon The author's icon.
     * @param fileType The type of the file, e.g. "png" or "gif".
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setAuthor(final String name, final String url, final byte[] icon, final String fileType) {
        delegate.setAuthor(name, url, icon, fileType);
        return this;
    }

    /**
     * Sets the author of the embed.
     * This method assumes the file type is "png"!
     *
     * @param name The name of the author.
     * @param url The url of the author.
     * @param icon The author's icon.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setAuthor(final String name, final String url, final BufferedImage icon) {
        delegate.setAuthor(name, url, icon);
        return this;
    }

    /**
     * Sets the author of the embed.
     *
     * @param name The name of the author.
     * @param url The url of the author.
     * @param icon The author's icon.
     * @param fileType The type of the file, e.g. "png" or "gif".
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setAuthor(final String name, final String url, final BufferedImage icon, final String fileType) {
        delegate.setAuthor(name, url, icon, fileType);
        return this;
    }

    /**
     * Sets the thumbnail of the embed.
     *
     * @param url The url of the thumbnail.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setThumbnail(final String url) {
        delegate.setThumbnail(url);
        return this;
    }

    /**
     * Sets the thumbnail of the embed.
     *
     * @param thumbnail The thumbnail.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setThumbnail(final Icon thumbnail) {
        delegate.setThumbnail(thumbnail);
        return this;
    }

    /**
     * Sets the thumbnail of the embed.
     *
     * @param thumbnail The thumbnail.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setThumbnail(final File thumbnail) {
        delegate.setThumbnail(thumbnail);
        return this;
    }

    /**
     * Sets the thumbnail of the embed.
     * This method assumes the file type is "png"!
     *
     * @param thumbnail The thumbnail.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setThumbnail(final InputStream thumbnail) {
        delegate.setThumbnail(thumbnail);
        return this;
    }

    /**
     * Sets the thumbnail of the embed.
     *
     * @param thumbnail The thumbnail.
     * @param fileType The type of the file, e.g. "png" or "gif".
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setThumbnail(final InputStream thumbnail, final String fileType) {
        delegate.setThumbnail(thumbnail, fileType);
        return this;
    }

    /**
     * Sets the thumbnail of the embed.
     * This method assumes the file type is "png"!
     *
     * @param thumbnail The thumbnail.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setThumbnail(final byte[] thumbnail) {
        delegate.setThumbnail(thumbnail);
        return this;
    }

    /**
     * Sets the thumbnail of the embed.
     *
     * @param thumbnail The thumbnail.
     * @param fileType The type of the file, e.g. "png" or "gif".
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setThumbnail(final byte[] thumbnail, final String fileType) {
        delegate.setThumbnail(thumbnail, fileType);
        return this;
    }

    /**
     * Sets the thumbnail of the embed.
     * This method assumes the file type is "png"!
     *
     * @param thumbnail The thumbnail.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setThumbnail(final BufferedImage thumbnail) {
        delegate.setThumbnail(thumbnail);
        return this;
    }

    /**
     * Sets the thumbnail of the embed.
     *
     * @param thumbnail The thumbnail.
     * @param fileType The type of the file, e.g. "png" or "gif".
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder setThumbnail(final BufferedImage thumbnail, final String fileType) {
        delegate.setThumbnail(thumbnail, fileType);
        return this;
    }

    /**
     * Adds an inline field to the embed.
     *
     * @param name The name of the field.
     * @param value The value of the field.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder addInlineField(final String name, final String value) {
        delegate.addField(name, value, true);
        return this;
    }

    /**
     * Adds a non-inline field to the embed.
     *
     * @param name The name of the field.
     * @param value The value of the field.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder addField(final String name, final String value) {
        delegate.addField(name, value, false);
        return this;
    }

    /**
     * Adds a field to the embed.
     *
     * @param name The name of the field.
     * @param value The value of the field.
     * @param inline Whether the field should be inline or not.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder addField(final String name, final String value, final boolean inline) {
        delegate.addField(name, value, inline);
        return this;
    }

    /**
     * Updates all fields of the embed that satisfy the given predicate using the given updater.
     *
     * @param predicate The predicate that fields have to satisfy to get updated.
     * @param updater The updater for the fields; the {@code EditableEmbedField} is only valid during the run of the
     *                updater; any try to save it in a variable and reuse it later after this method call will fail
     *                with exceptions.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder updateFields(final Predicate<EmbedField> predicate, final Consumer<EditableEmbedField> updater) {
        delegate.updateFields(predicate, updater);
        return this;
    }

    /**
     * Updates all fields of the embed using the given updater.
     *
     * @param updater The updater for the fields; the {@code EditableEmbedField} is only valid during the run of the
     *                updater; any try to save it in a variable and reuse it later after this method call will fail
     *                with exceptions.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder updateAllFields(final Consumer<EditableEmbedField> updater) {
        delegate.updateFields(field -> true, updater);
        return this;
    }

    /**
     * Removes all fields of the embed that satisfy the given predicate.
     *
     * @param predicate The predicate that fields have to satisfy to get removed.
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder removeFields(final Predicate<EmbedField> predicate) {
        delegate.removeFields(predicate);
        return this;
    }

    /**
     * Removes all fields of the embed.
     *
     * @return The current instance in order to chain call methods.
     */
    public EmbedBuilder removeAllFields() {
        delegate.removeFields(field -> true);
        return this;
    }

    /**
     * Checks if this embed requires any attachments.
     *
     * @return Whether the embed requires attachments or not.
     */
    public boolean requiresAttachments() {
        return delegate.requiresAttachments();
    }

}
