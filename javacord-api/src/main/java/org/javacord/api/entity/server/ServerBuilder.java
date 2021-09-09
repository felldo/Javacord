package org.javacord.api.entity.server;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.Region;
import org.javacord.api.entity.server.internal.ServerBuilderDelegate;
import org.javacord.api.util.internal.DelegateFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

/**
 * This class can be used to create a server.
 */
public class ServerBuilder {

    /**
     * The server delegate used by this instance.
     */
    private final ServerBuilderDelegate delegate;

    /**
     * Creates a new server builder.
     *
     * @param api The discord api instance.
     */
    public ServerBuilder(final DiscordApi api) {
        delegate = DelegateFactory.createServerBuilderDelegate(api);
    }

    /**
     * Sets the server's name.
     *
     * @param name The name of the server.
     * @return The current instance in order to chain call methods.
     */
    public ServerBuilder setName(final String name) {
        delegate.setName(name);
        return this;
    }

    /**
     * Sets the server's region.
     *
     * @param region The region of the server.
     * @return The current instance in order to chain call methods.
     */
    public ServerBuilder setRegion(final Region region) {
        delegate.setRegion(region);
        return this;
    }

    /**
     * Sets the server's explicit content filter level.
     *
     * @param explicitContentFilterLevel The explicit content filter level of the server.
     * @return The current instance in order to chain call methods.
     */
    public ServerBuilder setExplicitContentFilterLevel(final ExplicitContentFilterLevel explicitContentFilterLevel) {
        delegate.setExplicitContentFilterLevel(explicitContentFilterLevel);
        return this;
    }

    /**
     * Sets the server's verification level.
     *
     * @param verificationLevel The verification level of the server.
     * @return The current instance in order to chain call methods.
     */
    public ServerBuilder setVerificationLevel(final VerificationLevel verificationLevel) {
        delegate.setVerificationLevel(verificationLevel);
        return this;
    }

    /**
     * Sets the server's default message notification level.
     *
     * @param defaultMessageNotificationLevel The default message notification level of the server.
     * @return The current instance in order to chain call methods.
     */
    public ServerBuilder setDefaultMessageNotificationLevel(
            final DefaultMessageNotificationLevel defaultMessageNotificationLevel) {
        delegate.setDefaultMessageNotificationLevel(defaultMessageNotificationLevel);
        return this;
    }

    /**
     * Sets the server's afk timeout in seconds.
     *
     * @param afkTimeout The afk timeout in seconds of the server.
     * @return The current instance in order to chain call methods.
     */
    public ServerBuilder setAfkTimeoutInSeconds(final int afkTimeout) {
        delegate.setAfkTimeoutInSeconds(afkTimeout);
        return this;
    }

    /**
     * Sets the server's icon.
     * This method assumes the file type is "png"!
     *
     * @param icon The icon of the server.
     * @return The current instance in order to chain call methods.
     */
    public ServerBuilder setIcon(final BufferedImage icon) {
        delegate.setIcon(icon);
        return this;
    }

    /**
     * Sets the server's icon.
     *
     * @param icon The icon of the server.
     * @param fileType The type of the icon, e.g. "png" or "jpg".
     * @return The current instance in order to chain call methods.
     */
    public ServerBuilder setIcon(final BufferedImage icon, final String fileType) {
        delegate.setIcon(icon, fileType);
        return this;
    }

    /**
     * Sets the server's icon.
     *
     * @param icon The icon of the server.
     * @return The current instance in order to chain call methods.
     */
    public ServerBuilder setIcon(final File icon) {
        delegate.setIcon(icon);
        return this;
    }

    /**
     * Sets the server's icon.
     *
     * @param icon The icon of the server.
     * @return The current instance in order to chain call methods.
     */
    public ServerBuilder setIcon(final Icon icon) {
        delegate.setIcon(icon);
        return this;
    }

    /**
     * Sets the server's icon.
     *
     * @param icon The icon of the server.
     * @return The current instance in order to chain call methods.
     */
    public ServerBuilder setIcon(final URL icon) {
        delegate.setIcon(icon);
        return this;
    }

    /**
     * Sets the server's icon.
     * This method assumes the file type is "png"!
     *
     * @param icon The icon of the server.
     * @return The current instance in order to chain call methods.
     */
    public ServerBuilder setIcon(final byte[] icon) {
        delegate.setIcon(icon);
        return this;
    }

    /**
     * Sets the server's icon.
     *
     * @param icon The icon of the server.
     * @param fileType The type of the icon, e.g. "png" or "jpg".
     * @return The current instance in order to chain call methods.
     */
    public ServerBuilder setIcon(final byte[] icon, final String fileType) {
        delegate.setIcon(icon, fileType);
        return this;
    }

    /**
     * Sets the server's icon.
     * This method assumes the file type is "png"!
     *
     * @param icon The icon of the server.
     * @return The current instance in order to chain call methods.
     */
    public ServerBuilder setIcon(final InputStream icon) {
        delegate.setIcon(icon);
        return this;
    }

    /**
     * Sets the server's icon.
     *
     * @param icon The icon of the server.
     * @param fileType The type of the icon, e.g. "png" or "jpg".
     * @return The current instance in order to chain call methods.
     */
    public ServerBuilder setIcon(final InputStream icon, final String fileType) {
        delegate.setIcon(icon, fileType);
        return this;
    }

    /**
     * Creates the server.
     *
     * @return The id of the server.
     */
    public CompletableFuture<Long> create() {
        return delegate.create();
    }

}
