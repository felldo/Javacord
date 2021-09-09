package org.javacord.core.util.handler.guild;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.GroupChannel;
import org.javacord.api.entity.channel.PrivateChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.VoiceChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.channel.server.voice.ServerVoiceChannelMemberJoinEvent;
import org.javacord.api.event.channel.server.voice.ServerVoiceChannelMemberLeaveEvent;
import org.javacord.api.event.server.VoiceStateUpdateEvent;
import org.javacord.core.audio.AudioConnectionImpl;
import org.javacord.core.entity.channel.GroupChannelImpl;
import org.javacord.core.entity.channel.PrivateChannelImpl;
import org.javacord.core.entity.channel.ServerVoiceChannelImpl;
import org.javacord.core.entity.server.ServerImpl;
import org.javacord.core.entity.user.Member;
import org.javacord.core.entity.user.MemberImpl;
import org.javacord.core.event.channel.server.voice.ServerVoiceChannelMemberJoinEventImpl;
import org.javacord.core.event.channel.server.voice.ServerVoiceChannelMemberLeaveEventImpl;
import org.javacord.core.event.server.VoiceStateUpdateEventImpl;
import org.javacord.core.event.user.UserChangeDeafenedEventImpl;
import org.javacord.core.event.user.UserChangeMutedEventImpl;
import org.javacord.core.event.user.UserChangeSelfDeafenedEventImpl;
import org.javacord.core.event.user.UserChangeSelfMutedEventImpl;
import org.javacord.core.util.event.DispatchQueueSelector;
import org.javacord.core.util.gateway.PacketHandler;
import org.javacord.core.util.logging.LoggerUtil;

import java.util.Optional;

/**
 * Handles the voice state update packet.
 */
public class VoiceStateUpdateHandler extends PacketHandler {

    private static final Logger logger = LoggerUtil.getLogger(VoiceStateUpdateHandler.class);

    /**
     * Creates a new instance of this class.
     *
     * @param api The api.
     */
    public VoiceStateUpdateHandler(final DiscordApi api) {
        super(api, true, "VOICE_STATE_UPDATE");
    }

    @Override
    public void handle(final JsonNode packet) {
        if (!packet.hasNonNull("user_id")) {
            return;
        }

        final long userId = packet.get("user_id").asLong();
        if (packet.hasNonNull("guild_id")) {
            handleServerVoiceChannel(packet, userId);
        } else if (packet.hasNonNull("channel_id")) {
            final long channelId = packet.get("channel_id").asLong();
            final Optional<VoiceChannel> optionalChannel = api.getVoiceChannelById(channelId);
            if (optionalChannel.isPresent()) {
                final VoiceChannel voiceChannel = optionalChannel.get();
                if (voiceChannel instanceof PrivateChannel) {
                    handlePrivateChannel(userId, ((PrivateChannelImpl) voiceChannel));
                } else if (voiceChannel instanceof GroupChannel) {
                    handleGroupChannel(userId, ((GroupChannelImpl) voiceChannel));
                }
            } else {
                LoggerUtil.logMissingChannel(logger, channelId);
            }
        }

        if (api.getYourself().getId() == userId) {
            handleSelf(packet);
        }
    }

    private void handleSelf(final JsonNode packet) {
        // We need the session id to connect to an audio websocket
        final String sessionId = packet.get("session_id").asText();
        final long channelId = packet.get("channel_id").asLong();
        final Optional<ServerVoiceChannel> optionalChannel = api.getServerVoiceChannelById(channelId);
        if (optionalChannel.isPresent()) {
            final ServerVoiceChannel channel = optionalChannel.get();
            dispatchVoiceStateUpdateEvent(
                    channel, channel.getServer(), packet.get("session_id").asText());

            final AudioConnectionImpl pendingAudioConnection =
                    api.getPendingAudioConnectionByServerId(channel.getServer().getId());
            if (pendingAudioConnection != null) {
                pendingAudioConnection.setSessionId(sessionId);
                pendingAudioConnection.tryConnect();
            }

            channel.getServer().getAudioConnection().ifPresent(connection -> {
                ((AudioConnectionImpl) connection).setSessionId(sessionId);
                ((AudioConnectionImpl) connection).tryConnect();
            });
        } else {
            LoggerUtil.logMissingChannel(logger, channelId);
        }
    }

    private void handleServerVoiceChannel(final JsonNode packet, final long userId) {
        api.getPossiblyUnreadyServerById(packet.get("guild_id").asLong())
                .map(ServerImpl.class::cast).ifPresent(server -> {
                    final Member member = new MemberImpl(api, server, packet.get("member"), null);
                    final Optional<ServerVoiceChannelImpl> oldChannel = server
                            .getConnectedVoiceChannel(userId)
                            .map(ServerVoiceChannelImpl.class::cast);

                    final Optional<ServerVoiceChannelImpl> newChannel;
                    if (packet.hasNonNull("channel_id")) {
                        newChannel = server
                                .getVoiceChannelById(packet.get("channel_id").asLong())
                                .map(ServerVoiceChannelImpl.class::cast);
                    } else {
                        newChannel = Optional.empty();
                    }

                    if (!newChannel.equals(oldChannel)) {
                        oldChannel.ifPresent(channel -> {
                            channel.removeConnectedUser(userId);
                            dispatchServerVoiceChannelMemberLeaveEvent(
                                    member, newChannel.orElse(null), channel, server);
                        });

                        newChannel.ifPresent(channel -> {
                            channel.addConnectedUser(userId);
                            dispatchServerVoiceChannelMemberJoinEvent(member, channel, oldChannel.orElse(null), server);
                        });
                    }

                    if (!packet.hasNonNull("member")) {
                        logger.warn("Received VOICE_STATE_UPDATE packet without non-null member field: {}", packet);
                        return;
                    }
                    final MemberImpl newMember = new MemberImpl(api, server, packet.get("member"), null);
                    final Member oldMember = server.getRealMemberById(packet.get("user_id").asLong()).orElse(null);

                    final boolean newSelfMuted = packet.get("self_mute").asBoolean();
                    final boolean oldSelfMuted = server.isSelfMuted(userId);
                    if (newSelfMuted != oldSelfMuted) {
                        final UserChangeSelfMutedEventImpl event = new UserChangeSelfMutedEventImpl(newMember, oldMember);
                        api.getEventDispatcher()
                                .dispatchUserChangeSelfMutedEvent(server, server, newMember.getUser(), event);
                    }

                    final boolean newSelfDeafened = packet.get("self_deaf").asBoolean();
                    final boolean oldSelfDeafened = server.isSelfDeafened(userId);
                    if (newSelfDeafened != oldSelfDeafened) {
                        final UserChangeSelfDeafenedEventImpl event = new UserChangeSelfDeafenedEventImpl(
                                newMember, oldMember);
                        api.getEventDispatcher()
                                .dispatchUserChangeSelfDeafenedEvent(server, server, newMember.getUser(), event);
                    }

                    final boolean newMuted = packet.get("mute").asBoolean();
                    final boolean oldMuted = server.isMuted(userId);
                    if (newMuted != oldMuted) {
                        server.setMuted(userId, newMuted);
                        final UserChangeMutedEventImpl event = new UserChangeMutedEventImpl(newMember, oldMember);
                        api.getEventDispatcher()
                                .dispatchUserChangeMutedEvent(server, server, newMember.getUser(), event);
                    }

                    final boolean newDeafened = packet.get("deaf").asBoolean();
                    final boolean oldDeafened = server.isDeafened(userId);
                    if (newDeafened != oldDeafened) {
                        server.setDeafened(userId, newDeafened);
                        final UserChangeDeafenedEventImpl event = new UserChangeDeafenedEventImpl(newMember, oldMember);
                        api.getEventDispatcher()
                                .dispatchUserChangeDeafenedEvent(server, server, newMember.getUser(), event);
                    }
                });
    }

    private void handlePrivateChannel(final long userId, final PrivateChannelImpl channel) {
        //channel.addConnectedUser(user);
        //dispatchVoiceChannelMemberJoinEvent(user, channel);
    }

    private void handleGroupChannel(final long userId, final GroupChannelImpl channel) {
        //channel.addConnectedUser(user);
        //dispatchVoiceChannelMemberJoinEvent(user, channel);
    }

    private void dispatchVoiceStateUpdateEvent(final ServerVoiceChannel newChannel, final Server server, final String sessionId) {
        final VoiceStateUpdateEvent event = new VoiceStateUpdateEventImpl(newChannel, sessionId);

        api.getEventDispatcher().dispatchVoiceStateUpdateEvent((DispatchQueueSelector) server, newChannel, event);
    }

    private void dispatchServerVoiceChannelMemberJoinEvent(
            final Member member, final ServerVoiceChannel newChannel, final ServerVoiceChannel oldChannel, final Server server) {
        final ServerVoiceChannelMemberJoinEvent event = new ServerVoiceChannelMemberJoinEventImpl(
                member, newChannel, oldChannel);

        api.getEventDispatcher().dispatchServerVoiceChannelMemberJoinEvent(
                (DispatchQueueSelector) server, server, newChannel, member.getUser(), event);
    }

    private void dispatchServerVoiceChannelMemberLeaveEvent(
            final Member member, final ServerVoiceChannel newChannel, final ServerVoiceChannel oldChannel, final Server server) {
        final ServerVoiceChannelMemberLeaveEvent event = new ServerVoiceChannelMemberLeaveEventImpl(
                member, newChannel, oldChannel);

        api.getEventDispatcher().dispatchServerVoiceChannelMemberLeaveEvent(
                (DispatchQueueSelector) server, server, oldChannel, member.getUser(), event);
    }

}
