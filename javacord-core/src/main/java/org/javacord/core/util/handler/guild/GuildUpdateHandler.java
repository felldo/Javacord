package org.javacord.core.util.handler.guild;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.Region;
import org.javacord.api.entity.VanityUrlCode;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.server.BoostLevel;
import org.javacord.api.entity.server.DefaultMessageNotificationLevel;
import org.javacord.api.entity.server.ExplicitContentFilterLevel;
import org.javacord.api.entity.server.MultiFactorAuthenticationLevel;
import org.javacord.api.entity.server.NsfwLevel;
import org.javacord.api.entity.server.ServerFeature;
import org.javacord.api.entity.server.VerificationLevel;
import org.javacord.api.event.server.ServerChangeAfkChannelEvent;
import org.javacord.api.event.server.ServerChangeAfkTimeoutEvent;
import org.javacord.api.event.server.ServerChangeBoostCountEvent;
import org.javacord.api.event.server.ServerChangeBoostLevelEvent;
import org.javacord.api.event.server.ServerChangeDefaultMessageNotificationLevelEvent;
import org.javacord.api.event.server.ServerChangeDescriptionEvent;
import org.javacord.api.event.server.ServerChangeDiscoverySplashEvent;
import org.javacord.api.event.server.ServerChangeExplicitContentFilterLevelEvent;
import org.javacord.api.event.server.ServerChangeIconEvent;
import org.javacord.api.event.server.ServerChangeModeratorsOnlyChannelEvent;
import org.javacord.api.event.server.ServerChangeMultiFactorAuthenticationLevelEvent;
import org.javacord.api.event.server.ServerChangeNameEvent;
import org.javacord.api.event.server.ServerChangeNsfwLevelEvent;
import org.javacord.api.event.server.ServerChangeOwnerEvent;
import org.javacord.api.event.server.ServerChangePreferredLocaleEvent;
import org.javacord.api.event.server.ServerChangeRegionEvent;
import org.javacord.api.event.server.ServerChangeRulesChannelEvent;
import org.javacord.api.event.server.ServerChangeServerFeaturesEvent;
import org.javacord.api.event.server.ServerChangeSplashEvent;
import org.javacord.api.event.server.ServerChangeSystemChannelEvent;
import org.javacord.api.event.server.ServerChangeVanityUrlCodeEvent;
import org.javacord.api.event.server.ServerChangeVerificationLevelEvent;
import org.javacord.core.entity.VanityUrlCodeImpl;
import org.javacord.core.entity.server.ServerImpl;
import org.javacord.core.event.server.ServerChangeAfkChannelEventImpl;
import org.javacord.core.event.server.ServerChangeAfkTimeoutEventImpl;
import org.javacord.core.event.server.ServerChangeBoostCountEventImpl;
import org.javacord.core.event.server.ServerChangeBoostLevelEventImpl;
import org.javacord.core.event.server.ServerChangeDefaultMessageNotificationLevelEventImpl;
import org.javacord.core.event.server.ServerChangeDescriptionEventImpl;
import org.javacord.core.event.server.ServerChangeDiscoverySplashEventImpl;
import org.javacord.core.event.server.ServerChangeExplicitContentFilterLevelEventImpl;
import org.javacord.core.event.server.ServerChangeIconEventImpl;
import org.javacord.core.event.server.ServerChangeModeratorsOnlyChannelEventImpl;
import org.javacord.core.event.server.ServerChangeMultiFactorAuthenticationLevelEventImpl;
import org.javacord.core.event.server.ServerChangeNameEventImpl;
import org.javacord.core.event.server.ServerChangeNsfwLevelEventImpl;
import org.javacord.core.event.server.ServerChangeOwnerEventImpl;
import org.javacord.core.event.server.ServerChangePreferredLocaleEventImpl;
import org.javacord.core.event.server.ServerChangeRegionEventImpl;
import org.javacord.core.event.server.ServerChangeRulesChannelEventImpl;
import org.javacord.core.event.server.ServerChangeServerFeaturesEventImpl;
import org.javacord.core.event.server.ServerChangeSplashEventImpl;
import org.javacord.core.event.server.ServerChangeSystemChannelEventImpl;
import org.javacord.core.event.server.ServerChangeVanityUrlCodeEventImpl;
import org.javacord.core.event.server.ServerChangeVerificationLevelEventImpl;
import org.javacord.core.util.gateway.PacketHandler;
import org.javacord.core.util.logging.LoggerUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

/**
 * Handles the guild update packet.
 */
public class GuildUpdateHandler extends PacketHandler {

    /**
     * The logger of this class.
     */
    private static final Logger logger = LoggerUtil.getLogger(GuildUpdateHandler.class);

    /**
     * Creates a new instance of this class.
     *
     * @param api The api.
     */
    public GuildUpdateHandler(final DiscordApi api) {
        super(api, true, "GUILD_UPDATE");
    }

    @Override
    public void handle(final JsonNode packet) {
        if (packet.has("unavailable") && packet.get("unavailable").asBoolean()) {
            return;
        }
        final long id = packet.get("id").asLong();
        api.getPossiblyUnreadyServerById(id).map(server -> (ServerImpl) server).ifPresent(server -> {
            final long oldApplicationId = server.getApplicationId().orElse(-1L);
            final long newApplicationId = packet.hasNonNull("application_id") ? packet.get("application_id").asLong() : -1L;
            if (oldApplicationId != newApplicationId) {
                server.setApplicationId(newApplicationId);
            }

            final String newName = packet.get("name").asText();
            final String oldName = server.getName();
            if (!Objects.deepEquals(oldName, newName)) {
                server.setName(newName);
                final ServerChangeNameEvent event = new ServerChangeNameEventImpl(server, newName, oldName);

                api.getEventDispatcher().dispatchServerChangeNameEvent(server, server, event);
            }

            final String newIconHash = packet.get("icon").asText(null);
            final String oldIconHash = server.getIconHash();
            if (!Objects.deepEquals(oldIconHash, newIconHash)) {
                server.setIconHash(newIconHash);
                final ServerChangeIconEvent event = new ServerChangeIconEventImpl(server, newIconHash, oldIconHash);

                api.getEventDispatcher().dispatchServerChangeIconEvent(server, server, event);
            }

            final String newSplashHash = packet.get("splash").asText(null);
            final String oldSplashHash = server.getSplashHash();
            if (!Objects.deepEquals(oldSplashHash, newSplashHash)) {
                server.setSplashHash(newSplashHash);
                final ServerChangeSplashEvent event = new ServerChangeSplashEventImpl(server, newSplashHash, oldSplashHash);

                api.getEventDispatcher().dispatchServerChangeSplashEvent(server, server, event);
            }

            final VerificationLevel newVerificationLevel = VerificationLevel.fromId(packet.get("verification_level").asInt());
            final VerificationLevel oldVerificationLevel = server.getVerificationLevel();
            if (newVerificationLevel != oldVerificationLevel) {
                server.setVerificationLevel(newVerificationLevel);
                final ServerChangeVerificationLevelEvent event = new ServerChangeVerificationLevelEventImpl(
                        server, newVerificationLevel, oldVerificationLevel);

                api.getEventDispatcher().dispatchServerChangeVerificationLevelEvent(server, server, event);
            }

            final Region newRegion = Region.getRegionByKey(packet.get("region").asText());
            final Region oldRegion = server.getRegion();
            if (oldRegion != newRegion) {
                server.setRegion(newRegion);
                final ServerChangeRegionEvent event = new ServerChangeRegionEventImpl(server, newRegion, oldRegion);

                api.getEventDispatcher().dispatchServerChangeRegionEvent(server, server, event);
            }

            final DefaultMessageNotificationLevel newDefaultMessageNotificationLevel =
                    DefaultMessageNotificationLevel.fromId(packet.get("default_message_notifications").asInt());
            final DefaultMessageNotificationLevel oldDefaultMessageNotificationLevel =
                    server.getDefaultMessageNotificationLevel();
            if (newDefaultMessageNotificationLevel != oldDefaultMessageNotificationLevel) {
                server.setDefaultMessageNotificationLevel(newDefaultMessageNotificationLevel);
                final ServerChangeDefaultMessageNotificationLevelEvent event =
                        new ServerChangeDefaultMessageNotificationLevelEventImpl(
                                server, newDefaultMessageNotificationLevel, oldDefaultMessageNotificationLevel);

                api.getEventDispatcher().dispatchServerChangeDefaultMessageNotificationLevelEvent(
                        server, server, event);
            }

            final long newOwnerId = packet.get("owner_id").asLong();
            final long oldOwnerId = server.getOwnerId();
            if (newOwnerId != oldOwnerId) {
                server.setOwnerId(newOwnerId);
                final ServerChangeOwnerEvent event = new ServerChangeOwnerEventImpl(server, newOwnerId, oldOwnerId);

                api.getEventDispatcher().dispatchServerChangeOwnerEvent(server, server, event);
            }

            if (packet.has("system_channel_id")) {
                final ServerTextChannel newSystemChannel = packet.get("system_channel_id").isNull()
                        ? null
                        : server.getTextChannelById(packet.get("system_channel_id").asLong()).orElse(null);
                final ServerTextChannel oldSystemChannel = server.getSystemChannel().orElse(null);
                if (oldSystemChannel != newSystemChannel) {
                    server.setSystemChannelId(newSystemChannel == null ? -1 : newSystemChannel.getId());
                    final ServerChangeSystemChannelEvent event =
                            new ServerChangeSystemChannelEventImpl(server, newSystemChannel, oldSystemChannel);

                    api.getEventDispatcher().dispatchServerChangeSystemChannelEvent(server, server, event);
                }
            }

            if (packet.has("afk_channel_id")) {
                final ServerVoiceChannel newAfkChannel = packet.get("afk_channel_id").isNull()
                        ? null
                        : server.getVoiceChannelById(packet.get("afk_channel_id").asLong()).orElse(null);
                final ServerVoiceChannel oldAfkChannel = server.getAfkChannel().orElse(null);
                if (oldAfkChannel != newAfkChannel) {
                    server.setAfkChannelId(newAfkChannel == null ? -1 : newAfkChannel.getId());
                    final ServerChangeAfkChannelEvent event =
                            new ServerChangeAfkChannelEventImpl(server, newAfkChannel, oldAfkChannel);

                    api.getEventDispatcher().dispatchServerChangeAfkChannelEvent(server, server, event);
                }
            }

            final int newAfkTimeout = packet.get("afk_timeout").asInt();
            final int oldAfkTimeout = server.getAfkTimeoutInSeconds();
            if (oldAfkTimeout != newAfkTimeout) {
                server.setAfkTimeout(newAfkTimeout);
                final ServerChangeAfkTimeoutEvent event =
                        new ServerChangeAfkTimeoutEventImpl(server, newAfkTimeout, oldAfkTimeout);

                api.getEventDispatcher().dispatchServerChangeAfkTimeoutEvent(server, server, event);
            }

            final ExplicitContentFilterLevel newExplicitContentFilterLevel =
                    ExplicitContentFilterLevel.fromId(packet.get("explicit_content_filter").asInt());
            final ExplicitContentFilterLevel oldExplicitContentFilterLevel = server.getExplicitContentFilterLevel();
            if (oldExplicitContentFilterLevel != newExplicitContentFilterLevel) {
                server.setExplicitContentFilterLevel(newExplicitContentFilterLevel);
                final ServerChangeExplicitContentFilterLevelEvent event = new ServerChangeExplicitContentFilterLevelEventImpl(
                        server, newExplicitContentFilterLevel, oldExplicitContentFilterLevel);

                api.getEventDispatcher().dispatchServerChangeExplicitContentFilterLevelEvent(server, server, event);
            }

            final MultiFactorAuthenticationLevel newMultiFactorAuthenticationLevel =
                    MultiFactorAuthenticationLevel.fromId(packet.get("mfa_level").asInt());
            final MultiFactorAuthenticationLevel oldMultiFactorAuthenticationLevel =
                    server.getMultiFactorAuthenticationLevel();
            if (oldMultiFactorAuthenticationLevel != newMultiFactorAuthenticationLevel) {
                server.setMultiFactorAuthenticationLevel(newMultiFactorAuthenticationLevel);
                final ServerChangeMultiFactorAuthenticationLevelEvent event =
                        new ServerChangeMultiFactorAuthenticationLevelEventImpl(
                                server, newMultiFactorAuthenticationLevel, oldMultiFactorAuthenticationLevel);

                api.getEventDispatcher().dispatchServerChangeMultiFactorAuthenticationLevelEvent(server, server, event);
            }

            if (packet.has("rules_channel_id")) {
                final ServerTextChannel newRulesChannel = packet.get("rules_channel_id").isNull()
                        ? null
                        : server.getTextChannelById(packet.get("rules_channel_id").asLong()).orElse(null);
                final ServerTextChannel oldRulesChannel = server.getRulesChannel().orElse(null);
                if (oldRulesChannel != newRulesChannel) {
                    server.setRulesChannelId(newRulesChannel == null ? -1 : newRulesChannel.getId());
                    final ServerChangeRulesChannelEvent event =
                            new ServerChangeRulesChannelEventImpl(server, newRulesChannel, oldRulesChannel);

                    api.getEventDispatcher().dispatchServerChangeRulesChannelEvent(server, server, event);
                }
            }

            if (packet.has("public_updates_channel_id")) {
                final ServerTextChannel newModeratorsOnlyChannel = packet.get("public_updates_channel_id").isNull()
                        ? null
                        : server.getTextChannelById(packet.get("public_updates_channel_id").asLong()).orElse(null);
                final ServerTextChannel oldModeratorsOnlyChannel = server.getModeratorsOnlyChannel().orElse(null);
                if (oldModeratorsOnlyChannel != newModeratorsOnlyChannel) {
                    server.setModeratorsOnlyChannelId(
                            newModeratorsOnlyChannel == null ? -1 : newModeratorsOnlyChannel.getId());
                    final ServerChangeModeratorsOnlyChannelEvent event =
                            new ServerChangeModeratorsOnlyChannelEventImpl(
                                    server, newModeratorsOnlyChannel, oldModeratorsOnlyChannel);

                    api.getEventDispatcher().dispatchServerChangeModeratorsOnlyChannelEvent(server, server, event);
                }
            }

            final BoostLevel oldBoostLevel = server.getBoostLevel();
            final BoostLevel newBoostLevel = BoostLevel.fromId(packet.get("premium_tier").asInt());
            if (oldBoostLevel != newBoostLevel) {
                server.setBoostLevel(newBoostLevel);
                final ServerChangeBoostLevelEvent event =
                        new ServerChangeBoostLevelEventImpl(server, newBoostLevel, oldBoostLevel);

                api.getEventDispatcher().dispatchServerChangeBoostLevelEvent(server, server, event);
            }

            final NsfwLevel oldNsfwLevel = server.getNsfwLevel();
            final NsfwLevel newNsfwLevel = NsfwLevel.fromId(packet.get("nsfw_level").asInt());
            if (oldNsfwLevel != newNsfwLevel) {
                server.setNsfwLevel(newNsfwLevel);
                final ServerChangeNsfwLevelEvent event =
                        new ServerChangeNsfwLevelEventImpl(server, newNsfwLevel, oldNsfwLevel);

                api.getEventDispatcher().dispatchServerChangeNsfwLevelEvent(server, server, event);
            }

            final Locale newPreferredLocale =
                    new Locale.Builder().setLanguageTag(packet.get("preferred_locale").asText()).build();
            final Locale oldPreferredLocale = server.getPreferredLocale();
            if (!oldPreferredLocale.equals(newPreferredLocale)) {
                server.setPreferredLocale(newPreferredLocale);
                final ServerChangePreferredLocaleEvent event =
                        new ServerChangePreferredLocaleEventImpl(server, newPreferredLocale, oldPreferredLocale);

                api.getEventDispatcher().dispatchServerChangePreferredLocaleEvent(server, server, event);
            }

            final int oldBoostCount = server.getBoostCount();
            final int newBoostCount = packet.has("premium_subscription_count")
                    ? packet.get("premium_subscription_count").asInt() : 0;
            if (oldBoostCount != newBoostCount) {
                server.setServerBoostCount(newBoostCount);
                final ServerChangeBoostCountEvent event =
                        new ServerChangeBoostCountEventImpl(server, newBoostCount, oldBoostCount);

                api.getEventDispatcher().dispatchServerChangeBoostCountEvent(server, server, event);
            }

            final String oldDescription = server.getDescription().isPresent() ? server.getDescription().get() : null;
            final String newDescription = packet.hasNonNull("description") ? packet.get("description").asText() : null;
            if (!Objects.deepEquals(oldDescription, newDescription)) {
                server.setDescription(newDescription);
                final ServerChangeDescriptionEvent event =
                        new ServerChangeDescriptionEventImpl(server, newDescription, oldDescription);

                api.getEventDispatcher().dispatchServerChangeDescriptionEvent(server, server, event);
            }

            final String newDiscoverySplashHash = packet.get("discovery_splash").asText(null);
            final String oldDiscoverySplashHash = server.getDiscoverySplashHash();
            if (!Objects.deepEquals(oldDiscoverySplashHash, newDiscoverySplashHash)) {
                server.setDiscoverySplashHash(newDiscoverySplashHash);
                final ServerChangeDiscoverySplashEvent event = new ServerChangeDiscoverySplashEventImpl(
                        server, newDiscoverySplashHash, oldDiscoverySplashHash);

                api.getEventDispatcher().dispatchServerChangeDiscoverySplashEvent(server, server, event);
            }

            final String oldVanityCode = server.getVanityUrlCode().map(VanityUrlCode::getCode).orElse(null);
            final String newVanityCode = packet.hasNonNull("vanity_url_code") ? packet.get("vanity_url_code").asText() : null;
            if (!Objects.deepEquals(oldVanityCode, newVanityCode)) {
                server.setVanityUrlCode(new VanityUrlCodeImpl(packet.get("vanity_url_code").asText()));
                final ServerChangeVanityUrlCodeEvent event =
                        new ServerChangeVanityUrlCodeEventImpl(server, newVanityCode, oldVanityCode);

                api.getEventDispatcher().dispatchServerChangeVanityUrlCodeEvent(server, server, event);
            }

            final Collection<ServerFeature> oldServerFeature = server.getFeatures();
            final Collection<ServerFeature> newServerFeature = new ArrayList<>();
            if (packet.has("features")) {
                packet.get("features").forEach(jsonNode -> {
                    try {
                        newServerFeature.add(ServerFeature.valueOf(jsonNode.asText()));
                    } catch (final Exception ignored) {
                        logger.debug("Encountered server with unknown feature {}. Please update to the latest "
                                + "Javacord version or create an issue on the Javacord GitHub page if you are already "
                                + "on the latest version.", jsonNode.asText());
                    }
                });
            }
            if (!(oldServerFeature.containsAll(newServerFeature) && newServerFeature.containsAll(oldServerFeature))) {
                server.setServerFeatures(newServerFeature);
                final ServerChangeServerFeaturesEvent event =
                        new ServerChangeServerFeaturesEventImpl(server, newServerFeature, oldServerFeature);

                api.getEventDispatcher().dispatchServerChangeServerFeaturesEvent(server, server, event);
            }

            if (packet.has("system_channel_flags")) {
                server.setSystemChannelFlag(packet.get("system_channel_flags").asInt());
            }
        });
    }

}
