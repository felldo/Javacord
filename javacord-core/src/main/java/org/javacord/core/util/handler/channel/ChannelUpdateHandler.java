package org.javacord.core.util.handler.channel;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.channel.Categorizable;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.channel.group.GroupChannelChangeNameEvent;
import org.javacord.api.event.channel.server.ServerChannelChangeNameEvent;
import org.javacord.api.event.channel.server.ServerChannelChangeNsfwFlagEvent;
import org.javacord.api.event.channel.server.ServerChannelChangeOverwrittenPermissionsEvent;
import org.javacord.api.event.channel.server.ServerChannelChangePositionEvent;
import org.javacord.api.event.channel.server.text.ServerTextChannelChangeSlowmodeEvent;
import org.javacord.api.event.channel.server.text.ServerTextChannelChangeTopicEvent;
import org.javacord.api.event.channel.server.voice.ServerStageVoiceChannelChangeTopicEvent;
import org.javacord.api.event.channel.server.voice.ServerVoiceChannelChangeBitrateEvent;
import org.javacord.api.event.channel.server.voice.ServerVoiceChannelChangeUserLimitEvent;
import org.javacord.core.entity.channel.ChannelCategoryImpl;
import org.javacord.core.entity.channel.GroupChannelImpl;
import org.javacord.core.entity.channel.ServerChannelImpl;
import org.javacord.core.entity.channel.ServerStageVoiceChannelImpl;
import org.javacord.core.entity.channel.ServerTextChannelImpl;
import org.javacord.core.entity.channel.ServerVoiceChannelImpl;
import org.javacord.core.entity.permission.PermissionsImpl;
import org.javacord.core.entity.server.ServerImpl;
import org.javacord.core.event.channel.group.GroupChannelChangeNameEventImpl;
import org.javacord.core.event.channel.server.ServerChannelChangeNameEventImpl;
import org.javacord.core.event.channel.server.ServerChannelChangeNsfwFlagEventImpl;
import org.javacord.core.event.channel.server.ServerChannelChangeOverwrittenPermissionsEventImpl;
import org.javacord.core.event.channel.server.ServerChannelChangePositionEventImpl;
import org.javacord.core.event.channel.server.text.ServerTextChannelChangeSlowmodeEventImpl;
import org.javacord.core.event.channel.server.text.ServerTextChannelChangeTopicEventImpl;
import org.javacord.core.event.channel.server.voice.ServerStageVoiceChannelChangeTopicEventImpl;
import org.javacord.core.event.channel.server.voice.ServerVoiceChannelChangeBitrateEventImpl;
import org.javacord.core.event.channel.server.voice.ServerVoiceChannelChangeUserLimitEventImpl;
import org.javacord.core.util.event.DispatchQueueSelector;
import org.javacord.core.util.gateway.PacketHandler;
import org.javacord.core.util.logging.LoggerUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handles the channel update packet.
 */
public class ChannelUpdateHandler extends PacketHandler {

    private static final Logger logger = LoggerUtil.getLogger(ChannelUpdateHandler.class);

    /**
     * Creates a new instance of this class.
     *
     * @param api The api.
     */
    public ChannelUpdateHandler(final DiscordApi api) {
        super(api, true, "CHANNEL_UPDATE");
    }

    @Override
    public void handle(final JsonNode packet) {
        final ChannelType type = ChannelType.fromId(packet.get("type").asInt());
        switch (type) {
            case SERVER_TEXT_CHANNEL:
                handleServerChannel(packet);
                handleServerTextChannel(packet);
                break;
            case PRIVATE_CHANNEL:
                handlePrivateChannel(packet);
                break;
            case SERVER_VOICE_CHANNEL:
                handleServerChannel(packet);
                handleServerVoiceChannel(packet);
                break;
            case SERVER_STAGE_VOICE_CHANNEL:
                handleServerChannel(packet);
                handleServerVoiceChannel(packet);
                handleServerStageVoiceChannel(packet);
                break;
            case GROUP_CHANNEL:
                handleGroupChannel(packet);
                break;
            case CHANNEL_CATEGORY:
                handleServerChannel(packet);
                handleChannelCategory(packet);
                break;
            case SERVER_NEWS_CHANNEL:
                logger.debug("Received CHANNEL_UPDATE packet for a news channel. In this Javacord version it is "
                        + "treated as a normal text channel!");
                handleServerChannel(packet);
                handleServerTextChannel(packet);
                break;
            case SERVER_STORE_CHANNEL:
                // TODO Handle store channels
                logger.debug("Received CHANNEL_UPDATE packet for a store channel. These are not supported in this"
                        + " Javacord version and get ignored!");
                break;
            default:
                logger.warn("Unknown or unexpected channel type. Your Javacord version might be out of date!");
        }
    }

    /**
     * Handles a server channel update.
     *
     * @param jsonChannel The channel data.
     */
    private void handleServerChannel(final JsonNode jsonChannel) {
        final long channelId = jsonChannel.get("id").asLong();
        final long guildId = jsonChannel.get("guild_id").asLong();
        final ServerImpl server = api.getPossiblyUnreadyServerById(guildId).map(ServerImpl.class::cast).orElse(null);
        if (server == null) {
            return;
        }
        final ServerChannelImpl channel = server.getChannelById(channelId).map(ServerChannelImpl.class::cast).orElse(null);
        if (channel == null) {
            return;
        }
        final String oldName = channel.getName();
        final String newName = jsonChannel.get("name").asText();
        if (!Objects.deepEquals(oldName, newName)) {
            channel.setName(newName);
            final ServerChannelChangeNameEvent event =
                    new ServerChannelChangeNameEventImpl(channel, newName, oldName);

            if (server.isReady()) {
                api.getEventDispatcher().dispatchServerChannelChangeNameEvent(
                        (DispatchQueueSelector) channel.getServer(), channel.getServer(), channel, event);
            }
        }

        final AtomicBoolean areYouAffected = new AtomicBoolean(false);
        final ChannelCategory oldCategory = channel.asCategorizable().flatMap(Categorizable::getCategory).orElse(null);
        final ChannelCategory newCategory = jsonChannel.hasNonNull("parent_id")
                ? channel.getServer().getChannelCategoryById(jsonChannel.get("parent_id").asLong(-1)).orElse(null)
                : null;
        final int oldRawPosition = channel.getRawPosition();
        final int newRawPosition = jsonChannel.get("position").asInt();
        if (oldRawPosition != newRawPosition || !Objects.deepEquals(oldCategory, newCategory)) {
            final int oldPosition = channel.getPosition();
            if (channel instanceof ServerTextChannelImpl) {
                ((ServerTextChannelImpl) channel).setParentId(newCategory == null ? -1 : newCategory.getId());
            } else if (channel instanceof ServerVoiceChannelImpl) {
                ((ServerVoiceChannelImpl) channel).setParentId(newCategory == null ? -1 : newCategory.getId());
            }
            channel.setRawPosition(newRawPosition);

            final int newPosition = channel.getPosition();

            final ServerChannelChangePositionEvent event = new ServerChannelChangePositionEventImpl(
                    channel, newPosition, oldPosition, newRawPosition, oldRawPosition, newCategory, oldCategory);

            if (server.isReady()) {
                api.getEventDispatcher().dispatchServerChannelChangePositionEvent(
                        (DispatchQueueSelector) channel.getServer(), channel.getServer(), channel, event);
            }
        }

        final Collection<Long> rolesWithOverwrittenPermissions = new HashSet<>();
        final Collection<Long> usersWithOverwrittenPermissions = new HashSet<>();
        if (jsonChannel.has("permission_overwrites") && !jsonChannel.get("permission_overwrites").isNull()) {
            for (final JsonNode permissionOverwriteJson : jsonChannel.get("permission_overwrites")) {
                final Permissions oldOverwrittenPermissions;
                final ConcurrentHashMap<Long, Permissions> overwrittenPermissions;
                final long entityId = permissionOverwriteJson.get("id").asLong();
                final Optional<DiscordEntity> entity;
                switch (permissionOverwriteJson.get("type").asInt()) {
                    case 0:
                        final Role role = server.getRoleById(entityId).orElseThrow(() ->
                                new IllegalStateException("Received channel update event with unknown role!"));
                        entity = Optional.of(role);
                        oldOverwrittenPermissions = channel.getOverwrittenPermissions(role);
                        overwrittenPermissions = channel.getInternalOverwrittenRolePermissions();
                        rolesWithOverwrittenPermissions.add(entityId);
                        break;
                    case 1:
                        oldOverwrittenPermissions = channel.getOverwrittenUserPermissions()
                                .getOrDefault(entityId, PermissionsImpl.EMPTY_PERMISSIONS);
                        entity = api.getCachedUserById(entityId).map(DiscordEntity.class::cast);
                        overwrittenPermissions = channel.getInternalOverwrittenUserPermissions();
                        usersWithOverwrittenPermissions.add(entityId);
                        break;
                    default:
                        throw new IllegalStateException("Permission overwrite object with unknown type: "
                                + permissionOverwriteJson);
                }
                final long allow = permissionOverwriteJson.get("allow").asLong(0);
                final long deny = permissionOverwriteJson.get("deny").asLong(0);
                final Permissions newOverwrittenPermissions = new PermissionsImpl(allow, deny);
                if (!newOverwrittenPermissions.equals(oldOverwrittenPermissions)) {
                    overwrittenPermissions.put(entityId, newOverwrittenPermissions);
                    if (server.isReady()) {
                        dispatchServerChannelChangeOverwrittenPermissionsEvent(
                                channel, newOverwrittenPermissions, oldOverwrittenPermissions, entityId,
                                entity.orElse(null));
                        areYouAffected.compareAndSet(false, entityId == api.getYourself().getId());
                        entity.filter(e -> e instanceof Role)
                                .map(Role.class::cast)
                                .ifPresent(role -> areYouAffected
                                        .compareAndSet(false, role.getUsers().stream().anyMatch(User::isYourself)));
                    }
                }
            }
        }
        final ConcurrentHashMap<Long, Permissions> overwrittenRolePermissions;
        final ConcurrentHashMap<Long, Permissions> overwrittenUserPermissions;
        overwrittenRolePermissions = channel.getInternalOverwrittenRolePermissions();
        overwrittenUserPermissions = channel.getInternalOverwrittenUserPermissions();

        final Iterator<Map.Entry<Long, Permissions>> userIt = overwrittenUserPermissions.entrySet().iterator();
        while (userIt.hasNext()) {
            final Map.Entry<Long, Permissions> entry = userIt.next();
            if (usersWithOverwrittenPermissions.contains(entry.getKey())) {
                continue;
            }
            final Permissions oldPermissions = entry.getValue();
            userIt.remove();
            if (server.isReady()) {
                dispatchServerChannelChangeOverwrittenPermissionsEvent(
                        channel, PermissionsImpl.EMPTY_PERMISSIONS, oldPermissions, entry.getKey(),
                        api.getCachedUserById(entry.getKey()).orElse(null));
                areYouAffected.compareAndSet(false, entry.getKey() == api.getYourself().getId());
            }
        }

        final Iterator<Map.Entry<Long, Permissions>> roleIt = overwrittenRolePermissions.entrySet().iterator();
        while (roleIt.hasNext()) {
            final Map.Entry<Long, Permissions> entry = roleIt.next();
            if (rolesWithOverwrittenPermissions.contains(entry.getKey())) {
                continue;
            }
            api.getRoleById(entry.getKey()).ifPresent(role -> {
                final Permissions oldPermissions = entry.getValue();
                roleIt.remove();
                if (server.isReady()) {
                    dispatchServerChannelChangeOverwrittenPermissionsEvent(
                            channel, PermissionsImpl.EMPTY_PERMISSIONS, oldPermissions, role.getId(), role);
                    areYouAffected.compareAndSet(false, role.getUsers().stream().anyMatch(User::isYourself));
                }
            });
        }

        if (areYouAffected.get() && !channel.canYouSee()) {
            api.forEachCachedMessageWhere(
                    msg -> msg.getChannel().getId() == channelId,
                    msg -> api.removeMessageFromCache(msg.getId())
            );
        }
    }

    /**
     * Handles a channel category update.
     *
     * @param jsonChannel The channel data.
     */
    private void handleChannelCategory(final JsonNode jsonChannel) {
        final long channelCategoryId = jsonChannel.get("id").asLong();
        api.getChannelCategoryById(channelCategoryId).map(ChannelCategoryImpl.class::cast).ifPresent(channel -> {
            final boolean oldNsfwFlag = channel.isNsfw();
            final boolean newNsfwFlag = jsonChannel.get("nsfw").asBoolean();
            if (oldNsfwFlag != newNsfwFlag) {
                channel.setNsfwFlag(newNsfwFlag);
                final ServerChannelChangeNsfwFlagEvent event =
                        new ServerChannelChangeNsfwFlagEventImpl(channel, newNsfwFlag, oldNsfwFlag);

                api.getEventDispatcher().dispatchServerChannelChangeNsfwFlagEvent(
                        (DispatchQueueSelector) channel.getServer(), channel, channel.getServer(), null, event);
            }
        });
    }

    /**
     * Handles a server text channel update.
     *
     * @param jsonChannel The json channel data.
     */
    private void handleServerTextChannel(final JsonNode jsonChannel) {
        final long channelId = jsonChannel.get("id").asLong();
        final Optional<ServerTextChannel> optionalChannel = api.getServerTextChannelById(channelId);
        if (!optionalChannel.isPresent()) {
            LoggerUtil.logMissingChannel(logger, channelId);
            return;
        }

        final ServerTextChannelImpl channel = (ServerTextChannelImpl) optionalChannel.get();

        final String oldTopic = channel.getTopic();
        final String newTopic = jsonChannel.has("topic") && !jsonChannel.get("topic").isNull()
                ? jsonChannel.get("topic").asText() : "";
        if (!oldTopic.equals(newTopic)) {
            channel.setTopic(newTopic);

            final ServerTextChannelChangeTopicEvent event =
                    new ServerTextChannelChangeTopicEventImpl(channel, newTopic, oldTopic);

            api.getEventDispatcher().dispatchServerTextChannelChangeTopicEvent(
                    (DispatchQueueSelector) channel.getServer(), channel.getServer(), channel, event);
        }

        final boolean oldNsfwFlag = channel.isNsfw();
        final boolean newNsfwFlag = jsonChannel.get("nsfw").asBoolean();
        if (oldNsfwFlag != newNsfwFlag) {
            channel.setNsfwFlag(newNsfwFlag);
            final ServerChannelChangeNsfwFlagEvent event =
                    new ServerChannelChangeNsfwFlagEventImpl(channel, newNsfwFlag, oldNsfwFlag);

            api.getEventDispatcher().dispatchServerChannelChangeNsfwFlagEvent(
                    (DispatchQueueSelector) channel.getServer(), null, channel.getServer(), channel, event);
        }

        final int oldSlowmodeDelay = channel.getSlowmodeDelayInSeconds();
        //Check if "rate_limit_per_user" exists as a temporary fix until SERVER_NEWS_CHANNEL is handled separately.
        final int newSlowmodeDelay = jsonChannel.has("rate_limit_per_user")
                ? jsonChannel.get("rate_limit_per_user").asInt(0) : 0;
        if (oldSlowmodeDelay != newSlowmodeDelay) {
            channel.setSlowmodeDelayInSeconds(newSlowmodeDelay);
            final ServerTextChannelChangeSlowmodeEvent event =
                    new ServerTextChannelChangeSlowmodeEventImpl(channel, oldSlowmodeDelay, newSlowmodeDelay);

            api.getEventDispatcher().dispatchServerTextChannelChangeSlowmodeEvent(
                    (DispatchQueueSelector) channel.getServer(), channel.getServer(), channel, event
            );
        }
    }

    /**
     * Handles a server voice channel update.
     *
     * @param jsonChannel The channel data.
     */
    private void handleServerVoiceChannel(final JsonNode jsonChannel) {
        final long channelId = jsonChannel.get("id").asLong();
        final Optional<ServerVoiceChannel> optionalChannel = api.getServerVoiceChannelById(channelId);
        if (!optionalChannel.isPresent()) {
            LoggerUtil.logMissingChannel(logger, channelId);
            return;
        }

        final ServerVoiceChannelImpl channel = (ServerVoiceChannelImpl) optionalChannel.get();

        final int oldBitrate = channel.getBitrate();
        final int newBitrate = jsonChannel.get("bitrate").asInt();
        if (oldBitrate != newBitrate) {
            channel.setBitrate(newBitrate);
            final ServerVoiceChannelChangeBitrateEvent event =
                    new ServerVoiceChannelChangeBitrateEventImpl(channel, newBitrate, oldBitrate);

            api.getEventDispatcher().dispatchServerVoiceChannelChangeBitrateEvent(
                    (DispatchQueueSelector) channel.getServer(), channel.getServer(), channel, event);
        }

        final int oldUserLimit = channel.getUserLimit().orElse(0);
        final int newUserLimit = jsonChannel.get("user_limit").asInt();
        if (oldUserLimit != newUserLimit) {
            channel.setUserLimit(newUserLimit);
            final ServerVoiceChannelChangeUserLimitEvent event =
                    new ServerVoiceChannelChangeUserLimitEventImpl(channel, newUserLimit, oldUserLimit);

            api.getEventDispatcher().dispatchServerVoiceChannelChangeUserLimitEvent(
                    (DispatchQueueSelector) channel.getServer(), channel.getServer(), channel, event);
        }

    }

    /**
     * Handles a server stage voice channel update.
     *
     * @param jsonChannel The channel data.
     */
    private void handleServerStageVoiceChannel(final JsonNode jsonChannel) {
        final long channelId = jsonChannel.get("id").asLong();
        api.getServerStageVoiceChannelById(channelId)
                .map(ServerStageVoiceChannelImpl.class::cast).ifPresent(channel -> {
                    final String oldTopic = channel.getTopic().orElse(null);
                    final String newTopic = jsonChannel.hasNonNull("topic")
                            ? jsonChannel.get("topic").asText()
                            : null;
                    if (!Objects.equals(oldTopic, newTopic)) {
                        channel.setTopic(newTopic);
                        final ServerStageVoiceChannelChangeTopicEvent event =
                                new ServerStageVoiceChannelChangeTopicEventImpl(channel, newTopic, oldTopic);
                        api.getEventDispatcher().dispatchServerStageVoiceChannelChangeTopicEvent(
                                (DispatchQueueSelector) channel.getServer(), channel.getServer(), channel, event
                        );
                    }
                });
    }

    /**
     * Handles a private channel update.
     *
     * @param channel The channel data.
     */
    private void handlePrivateChannel(final JsonNode channel) {
    }

    /**
     * Handles a group channel update.
     *
     * @param jsonChannel The channel data.
     */
    private void handleGroupChannel(final JsonNode jsonChannel) {
        final long channelId = jsonChannel.get("id").asLong();
        api.getGroupChannelById(channelId).map(GroupChannelImpl.class::cast).ifPresent(channel -> {
            final String oldName = channel.getName().orElseThrow(AssertionError::new);
            final String newName = jsonChannel.get("name").asText();
            if (!Objects.equals(oldName, newName)) {
                channel.setName(newName);

                final GroupChannelChangeNameEvent event =
                        new GroupChannelChangeNameEventImpl(channel, newName, oldName);

                api.getEventDispatcher().dispatchGroupChannelChangeNameEvent(
                        api, Collections.singleton(channel), channel.getMembers(), event);
            }
        });
    }

    /**
     * Dispatches a ServerChannelChangeOverwrittenPermissionsEvent.
     *
     * @param channel The channel of the event.
     * @param newPermissions The new overwritten permissions.
     * @param oldPermissions The old overwritten permissions.
     * @param entityId The id of the entity.
     * @param entity The entity of the event.
     */
    private void dispatchServerChannelChangeOverwrittenPermissionsEvent(
            final ServerChannel channel, final Permissions newPermissions, final Permissions oldPermissions,
            final long entityId, final DiscordEntity entity) {
        if (newPermissions.equals(oldPermissions)) {
            // This can be caused by adding a user/role in a channels overwritten permissions without modifying
            // any of its values. We don't need to dispatch an event for this.
            return;
        }
        final ServerChannelChangeOverwrittenPermissionsEvent event =
                new ServerChannelChangeOverwrittenPermissionsEventImpl(
                        channel, newPermissions, oldPermissions, entityId, entity);

        api.getEventDispatcher().dispatchServerChannelChangeOverwrittenPermissionsEvent(
                (DispatchQueueSelector) channel.getServer(),
                (entity instanceof Role) ? (Role) entity : null,
                channel.getServer(),
                channel,
                (entity instanceof User) ? (User) entity : null,
                event);
    }

}
