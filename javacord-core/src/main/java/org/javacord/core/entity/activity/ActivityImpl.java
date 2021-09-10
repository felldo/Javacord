package org.javacord.core.entity.activity;

import com.fasterxml.jackson.databind.JsonNode;
import org.javacord.api.entity.activity.Activity;
import org.javacord.api.entity.activity.ActivityAssets;
import org.javacord.api.entity.activity.ActivityParty;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.core.DiscordApiImpl;
import org.javacord.core.entity.emoji.UnicodeEmojiImpl;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * The implementation of {@link Activity}.
 */
public class ActivityImpl implements Activity {

    private final ActivityType type;
    private final String name;
    private final String streamingUrl;
    private final String details;
    private final String state;
    private final ActivityParty party;
    private final ActivityAssets assets;
    private final Long applicationId;
    private final Long startTime;
    private final Long endTime;
    private final Emoji emoji;

    /**
     * Creates a new activity object.
     *
     * @param api The discord api instance.
     * @param data The json data of the activity.
     */
    public ActivityImpl(final DiscordApiImpl api, final JsonNode data) {
        this.type = ActivityType.getActivityTypeById(data.get("type").asInt());
        this.name = data.get("name").asText();
        this.streamingUrl = data.has("url") ? data.get("url").asText(null) : null;
        this.details = data.has("details") ? data.get("details").asText(null) : null;
        this.state = data.has("state") ? data.get("state").asText(null) : null;
        this.party = data.has("party") ? new ActivityPartyImpl(data.get("party")) : null;
        this.assets = data.has("assets") ? new ActivityAssetsImpl(this, data.get("assets")) : null;
        this.applicationId = data.has("application_id") ? data.get("application_id").asLong() : null;
        if (data.has("timestamps")) {
            final JsonNode timestamps = data.get("timestamps");
            this.startTime = timestamps.has("start") ? timestamps.get("start").asLong() : null;
            this.endTime = timestamps.has("end") ? timestamps.get("end").asLong() : null;
        } else {
            this.startTime = null;
            this.endTime = null;
        }
        if (data.has("emoji")) {
            final JsonNode emoji = data.get("emoji");
            if (emoji.has("id")) {
                this.emoji = api.getKnownCustomEmojiOrCreateCustomEmoji(emoji);
            } else {
                this.emoji = UnicodeEmojiImpl.fromString(emoji.get("name").asText());
            }
        } else {
            this.emoji = null;
        }
    }

    /**
     * Creates a new activity object.
     *
     * @param type The type of the activity.
     * @param name The name of the activity.
     * @param streamingUrl The streamingUrl of the activity.
     */
    public ActivityImpl(final ActivityType type, final String name, final String streamingUrl) {
        this.type = type;
        this.name = name;
        this.streamingUrl = streamingUrl;
        this.details = null;
        this.state = null;
        this.party = null;
        this.assets = null;
        this.applicationId = null;
        this.startTime = null;
        this.endTime = null;
        this.emoji = null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Optional<String> getStreamingUrl() {
        return Optional.ofNullable(streamingUrl);
    }

    @Override
    public ActivityType getType() {
        return type;
    }

    @Override
    public Optional<String> getDetails() {
        return Optional.ofNullable(details);
    }

    @Override
    public Optional<String> getState() {
        return Optional.ofNullable(state);
    }

    @Override
    public Optional<ActivityParty> getParty() {
        return Optional.ofNullable(party);
    }

    @Override
    public Optional<ActivityAssets> getAssets() {
        return Optional.ofNullable(assets);
    }

    @Override
    public Optional<Long> getApplicationId() {
        return Optional.ofNullable(applicationId);
    }

    @Override
    public Optional<Instant> getStartTime() {
        return Optional.ofNullable(startTime).map(Instant::ofEpochMilli);
    }

    @Override
    public Optional<Instant> getEndTime() {
        return Optional.ofNullable(endTime).map(Instant::ofEpochMilli);
    }

    @Override
    public Optional<Emoji> getEmoji() {
        return Optional.ofNullable(emoji);
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ActivityImpl)) {
            return false;
        }
        final ActivityImpl otherActivity = (ActivityImpl) obj;
        return Objects.deepEquals(type, otherActivity.type)
                && Objects.deepEquals(name, otherActivity.name)
                && Objects.deepEquals(streamingUrl, otherActivity.streamingUrl)
                && Objects.deepEquals(details, otherActivity.details)
                && Objects.deepEquals(state, otherActivity.state)
                && Objects.deepEquals(party, otherActivity.party)
                && Objects.deepEquals(assets, otherActivity.assets)
                && Objects.deepEquals(applicationId, otherActivity.applicationId)
                && Objects.deepEquals(startTime, otherActivity.startTime)
                && Objects.deepEquals(endTime, otherActivity.endTime);
    }

    @Override
    public int hashCode() {
        int hash = 42;
        final int typeHash = type.hashCode();
        final int nameHash = name == null ? 0 : name.hashCode();
        final int streamingUrlHash = streamingUrl == null ? 0 : streamingUrl.hashCode();
        final int detailsHash = details == null ? 0 : details.hashCode();
        final int stateHash = state == null ? 0 : state.hashCode();
        final int partyHash = party == null ? 0 : party.hashCode();
        final int assetsHash = assets == null ? 0 : assets.hashCode();
        final int applicationIdHash = applicationId == null ? 0 : applicationId.toString().hashCode();
        final int startTimeHash = startTime == null ? 0 : startTime.toString().hashCode();
        final int endTimeHash = endTime == null ? 0 : endTime.toString().hashCode();
        final int emojiHash = emoji == null ? 0 : emoji.hashCode();

        hash = hash * 11 + typeHash;
        hash = hash * 13 + nameHash;
        hash = hash * 17 + streamingUrlHash;
        hash = hash * 19 + detailsHash;
        hash = hash * 23 + stateHash;
        hash = hash * 29 + partyHash;
        hash = hash * 31 + assetsHash;
        hash = hash * 37 + applicationIdHash;
        hash = hash * 41 + startTimeHash;
        hash = hash * 43 + endTimeHash;
        hash = hash * 47 + emojiHash;
        return hash;
    }
}
