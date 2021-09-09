package org.javacord.core;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.javacord.api.entity.Icon;
import org.javacord.api.internal.AccountUpdaterDelegate;
import org.javacord.core.util.FileContainer;
import org.javacord.core.util.rest.RestEndpoint;
import org.javacord.core.util.rest.RestMethod;
import org.javacord.core.util.rest.RestRequest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

/**
 * The implementation of {@link AccountUpdaterDelegate}.
 */
public class AccountUpdaterDelegateImpl implements AccountUpdaterDelegate {

    /**
     * The discord api instance.
     */
    private final DiscordApiImpl api;

    /**
     * The username to update.
     */
    private String username = null;

    /**
     * The avatar to update.
     */
    private FileContainer avatar = null;

    /**
     * Creates a new account updater delegate.
     *
     * @param api The discord api instance.
     */
    public AccountUpdaterDelegateImpl(final DiscordApiImpl api) {
        this.api = api;
    }

    @Override
    public void setUsername(final String username) {
        this.username = username;
    }

    @Override
    public void setAvatar(final BufferedImage avatar) {
        this.avatar = (avatar == null) ? null : new FileContainer(avatar, "png");
    }

    @Override
    public void setAvatar(final BufferedImage avatar, final String fileType) {
        this.avatar = (avatar == null) ? null : new FileContainer(avatar, fileType);
    }

    @Override
    public void setAvatar(final File avatar) {
        this.avatar = (avatar == null) ? null : new FileContainer(avatar);
    }

    @Override
    public void setAvatar(final Icon avatar) {
        this.avatar = (avatar == null) ? null : new FileContainer(avatar);
    }

    @Override
    public void setAvatar(final URL avatar) {
        this.avatar = (avatar == null) ? null : new FileContainer(avatar);
    }

    @Override
    public void setAvatar(final byte[] avatar) {
        this.avatar = (avatar == null) ? null : new FileContainer(avatar, "png");
    }

    @Override
    public void setAvatar(final byte[] avatar, final String fileType) {
        this.avatar = (avatar == null) ? null : new FileContainer(avatar, fileType);
    }

    @Override
    public void setAvatar(final InputStream avatar) {
        this.avatar = (avatar == null) ? null : new FileContainer(avatar, "png");
    }

    @Override
    public void setAvatar(final InputStream avatar, final String fileType) {
        this.avatar = (avatar == null) ? null : new FileContainer(avatar, fileType);
    }

    @Override
    public CompletableFuture<Void> update() {
        boolean patchAccount = false;
        final ObjectNode body = JsonNodeFactory.instance.objectNode();
        if (username != null) {
            body.put("username", username);
            patchAccount = true;
        }
        if (avatar != null) {
            patchAccount = true;
        }
        if (patchAccount) {
            if (avatar != null) {
                return avatar.asByteArray(api).thenAccept(bytes -> {
                    final String base64Avatar = "data:image/" + avatar.getFileType() + ";base64,"
                            + Base64.getEncoder().encodeToString(bytes);
                    body.put("avatar", base64Avatar);
                }).thenCompose(aVoid -> new RestRequest<Void>(api, RestMethod.PATCH, RestEndpoint.CURRENT_USER)
                        .setBody(body)
                        .execute(result -> null));
            }
            return new RestRequest<Void>(api, RestMethod.PATCH, RestEndpoint.CURRENT_USER)
                    .setBody(body)
                    .execute(result -> null);
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

}
