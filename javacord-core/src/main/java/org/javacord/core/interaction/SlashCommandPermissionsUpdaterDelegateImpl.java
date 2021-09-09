package org.javacord.core.interaction;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.ServerSlashCommandPermissions;
import org.javacord.api.interaction.SlashCommandPermissions;
import org.javacord.api.interaction.internal.SlashCommandPermissionsUpdaterDelegate;
import org.javacord.core.util.rest.RestEndpoint;
import org.javacord.core.util.rest.RestMethod;
import org.javacord.core.util.rest.RestRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SlashCommandPermissionsUpdaterDelegateImpl implements SlashCommandPermissionsUpdaterDelegate {

    private final Server server;
    private List<SlashCommandPermissions> permissions;

    /**
     * Creates an instance of this class.
     *
     * @param server The server where the update should be performed on.
     */
    public SlashCommandPermissionsUpdaterDelegateImpl(final Server server) {
        this.server = server;
        this.permissions = new ArrayList<>();
    }

    @Override
    public void setPermissions(final List<SlashCommandPermissions> permission) {
        this.permissions = permission;
    }

    @Override
    public void addPermissions(final List<SlashCommandPermissions> permissions) {
        this.permissions.addAll(permissions);
    }

    @Override
    public void addPermission(final SlashCommandPermissions permission) {
        this.permissions.add(permission);
    }

    @Override
    public CompletableFuture<ServerSlashCommandPermissions> update(final long commandId) {
        final ObjectNode body = JsonNodeFactory.instance.objectNode();
        final ArrayNode array = body.putArray("permissions");
        for (final SlashCommandPermissions permission : permissions) {
            array.add(((SlashCommandPermissionsImpl) permission).toJsonNode());
        }

        return new RestRequest<ServerSlashCommandPermissions>(server.getApi(), RestMethod.PUT,
                RestEndpoint.SLASH_COMMAND_PERMISSIONS)
                .setUrlParameters(String.valueOf(server.getApi().getClientId()),
                        server.getIdAsString(), String.valueOf(commandId))
                .setBody(body)
                .execute(result -> new ServerSlashCommandPermissionsImpl(result.getJsonBody()));
    }
}
