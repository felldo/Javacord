package org.javacord.core.interaction;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.internal.SlashCommandUpdaterDelegate;
import org.javacord.core.DiscordApiImpl;
import org.javacord.core.util.rest.RestEndpoint;
import org.javacord.core.util.rest.RestMethod;
import org.javacord.core.util.rest.RestRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The implementation of {@link SlashCommandUpdaterDelegate}.
 */
public class SlashCommandUpdaterDelegateImpl implements SlashCommandUpdaterDelegate {

    /**
     * The slash command id.
     */
    private final long commandId;

    /**
     * The slash command name.
     */
    private String name = null;

    /**
     * The slash command description.
     */
    private String description = null;

    /**
     * The slash command options.
     */
    private List<SlashCommandOption> slashCommandOptions = new ArrayList<>();

    /**
     * The slash command default permission.
     */
    private boolean defaultPermission = true;

    /**
     * Creates a new account updater delegate.
     *
     * @param commandId The discord api instance.
     */
    public SlashCommandUpdaterDelegateImpl(final long commandId) {
        this.commandId = commandId;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public void setOptions(final List<SlashCommandOption> slashCommandOptions) {
        this.slashCommandOptions = slashCommandOptions;
    }

    @Override
    public void setDefaultPermission(final boolean defaultPermission) {
        this.defaultPermission = defaultPermission;
    }

    private void prepareBody(final ObjectNode body) {
        if (name != null && !name.isEmpty()) {
            body.put("name", name);
        }

        if (description != null && !description.isEmpty()) {
            body.put("description", description);
        }

        if (!slashCommandOptions.isEmpty()) {
            final ArrayNode array = body.putArray("options");
            for (final SlashCommandOption slashCommandOption : slashCommandOptions) {
                array.add(((SlashCommandOptionImpl) slashCommandOption).toJsonNode());
            }
        }

        body.put("default_permission", defaultPermission);
    }

    @Override
    public CompletableFuture<SlashCommand> updateGlobal(final DiscordApi api) {
        final ObjectNode body = JsonNodeFactory.instance.objectNode();
        prepareBody(body);

        return new RestRequest<SlashCommand>(api, RestMethod.PATCH, RestEndpoint.SLASH_COMMANDS)
                .setUrlParameters(String.valueOf(api.getClientId()), String.valueOf(commandId))
                .setBody(body)
                .execute(result -> new SlashCommandImpl((DiscordApiImpl) api, result.getJsonBody()));
    }

    @Override
    public CompletableFuture<SlashCommand> updateForServer(final Server server) {
        final ObjectNode body = JsonNodeFactory.instance.objectNode();
        prepareBody(body);

        return new RestRequest<SlashCommand>(server.getApi(), RestMethod.PATCH,
                RestEndpoint.SERVER_SLASH_COMMANDS)
                .setUrlParameters(String.valueOf(server.getApi().getClientId()),
                        server.getIdAsString(), String.valueOf(commandId))
                .setBody(body)
                .execute(result -> new SlashCommandImpl((DiscordApiImpl) server.getApi(), result.getJsonBody()));
    }
}
