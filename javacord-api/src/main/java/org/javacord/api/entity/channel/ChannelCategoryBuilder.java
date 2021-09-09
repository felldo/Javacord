package org.javacord.api.entity.channel;

import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.Permissionable;
import org.javacord.api.entity.channel.internal.ChannelCategoryBuilderDelegate;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.server.Server;
import org.javacord.api.util.internal.DelegateFactory;

import java.util.concurrent.CompletableFuture;

/**
 * This class is used to create new channel categories.
 */
public class ChannelCategoryBuilder extends ServerChannelBuilder {

    /**
     * The channel category delegate used by this instance.
     */
    private final ChannelCategoryBuilderDelegate delegate;

    /**
     * Creates a new channel category builder.
     *
     * @param server The server of the channel category.
     */
    public ChannelCategoryBuilder(final Server server) {
        delegate = DelegateFactory.createChannelCategoryBuilderDelegate(server);
    }

    @Override
    public ChannelCategoryBuilder setAuditLogReason(final String reason) {
        delegate.setAuditLogReason(reason);
        return this;
    }

    @Override
    public ChannelCategoryBuilder setName(final String name) {
        delegate.setName(name);
        return this;
    }

    @Override
    public <T extends Permissionable & DiscordEntity> ChannelCategoryBuilder addPermissionOverwrite(
            final T permissionable, final Permissions permissions) {
        delegate.addPermissionOverwrite(permissionable, permissions);
        return this;
    }

    @Override
    public <T extends Permissionable & DiscordEntity> ChannelCategoryBuilder removePermissionOverwrite(
            final T permissionable) {
        delegate.removePermissionOverwrite(permissionable);
        return this;
    }

    /**
     * Creates the channel category.
     *
     * @return The created channel category.
     */
    public CompletableFuture<ChannelCategory> create() {
        return delegate.create();
    }

}
