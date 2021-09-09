package org.javacord.core.entity.message.component.internal;

import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.entity.message.component.ComponentType;
import org.javacord.api.entity.message.component.internal.ButtonBuilderDelegate;
import org.javacord.core.entity.emoji.UnicodeEmojiImpl;
import org.javacord.core.entity.message.component.ButtonImpl;

import java.util.Optional;

public class ButtonBuilderDelegateImpl implements ButtonBuilderDelegate {
    private final ComponentType type = ComponentType.BUTTON;

    private ButtonStyle style = ButtonStyle.SECONDARY;

    private String label = null;

    private String customId = null;

    private String url = null;

    private Boolean disabled = null;

    private Emoji emoji = null;

    @Override
    public ComponentType getType() {
        return type;
    }

    @Override
    public void copy(final Button button) {
        final Optional<String> customId = button.getCustomId();
        final Optional<String> url = button.getUrl();
        final Optional<String> label = button.getLabel();
        final Optional<Emoji> emoji = button.getEmoji();
        final Optional<Boolean> isDisabled = button.isDisabled();
        final ButtonStyle style = button.getStyle();

        this.setStyle(style);
        customId.ifPresent(this::setCustomId);
        url.ifPresent(this::setUrl);
        label.ifPresent(this::setLabel);
        emoji.ifPresent(this::setEmoji);
        isDisabled.ifPresent(this::setDisabled);
    }

    @Override
    public void setStyle(final ButtonStyle style) {
        this.style = style;
    }

    @Override
    public void setLabel(final String label) {
        this.label = label;
    }

    @Override
    public void setCustomId(final String customId) {
        this.customId = customId;
    }

    @Override
    public void setUrl(final String url) {
        this.url = url;
    }

    @Override
    public void setDisabled(final Boolean isDisabled) {
        this.disabled = isDisabled;
    }

    @Override
    public Button build() {
        return new ButtonImpl(style, label, customId, url, disabled, emoji);
    }

    @Override
    public void setEmoji(final Emoji emoji) {
        this.emoji = emoji;
    }

    @Override
    public void setEmoji(final CustomEmoji emoji) {
        this.emoji = emoji;
    }

    @Override
    public void setEmoji(final String unicode) {
        this.emoji = UnicodeEmojiImpl.fromString(unicode);
    }

    @Override
    public ButtonStyle getStyle() {
        return style;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getCustomId() {
        return customId;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Boolean isDisabled() {
        return disabled;
    }

    @Override
    public Emoji getEmoji() {
        return emoji;
    }

}
