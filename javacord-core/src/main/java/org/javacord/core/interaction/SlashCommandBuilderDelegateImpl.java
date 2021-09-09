package org.javacord.core.interaction;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.internal.SlashCommandBuilderDelegate;
import org.javacord.core.DiscordApiImpl;
import org.javacord.core.util.rest.RestEndpoint;
import org.javacord.core.util.rest.RestMethod;
import org.javacord.core.util.rest.RestRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SlashCommandBuilderDelegateImpl implements SlashCommandBuilderDelegate {

    private String name;
    private String description;
    private List<SlashCommandOption> options = new ArrayList<>();
    private Boolean defaultPermission;

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public void addOption(final SlashCommandOption option) {
        options.add(option);
    }

    @Override
    public void setOptions(final List<SlashCommandOption> options) {
        if (options == null) {
            this.options.clear();
        } else {
            this.options = new ArrayList<>(options);
        }
    }

    @Override
    public void setDefaultPermission(final Boolean defaultPermission) {
        this.defaultPermission = defaultPermission;
    }

    @Override
    public CompletableFuture<SlashCommand> createGlobal(final DiscordApi api) {
        return new RestRequest<SlashCommand>(api, RestMethod.POST, RestEndpoint.SLASH_COMMANDS)
                .setUrlParameters(String.valueOf(api.getClientId()))
                .setBody(getJsonBodyForSlashCommand())
                .execute(result -> new SlashCommandImpl((DiscordApiImpl) api, result.getJsonBody()));
    }

    @Override
    public CompletableFuture<SlashCommand> createForServer(final Server server) {
        return new RestRequest<SlashCommand>(
                server.getApi(), RestMethod.POST, RestEndpoint.SERVER_SLASH_COMMANDS)
                .setUrlParameters(String.valueOf(server.getApi().getClientId()), server.getIdAsString())
                .setBody(getJsonBodyForSlashCommand())
                .execute(result -> new SlashCommandImpl((DiscordApiImpl) server.getApi(), result.getJsonBody()));
    }

    /**
     * Gets the JSON body for this slash command.

     * @return The JSON of this slash command.
     */
    public ObjectNode getJsonBodyForSlashCommand() {
        final ObjectNode jsonBody = JsonNodeFactory.instance.objectNode()
                .put("name", name)
                .put("description", description);

        if (!options.isEmpty()) {
            final ArrayNode jsonOptions = jsonBody.putArray("options");
            options.stream()
                    .map(SlashCommandOptionImpl.class::cast)
                    .map(SlashCommandOptionImpl::toJsonNode)
                    .forEach(jsonOptions::add);
        }

        if (defaultPermission != null) {
            jsonBody.put("default_permission", defaultPermission.booleanValue());
        }

        return jsonBody;
    }
}
