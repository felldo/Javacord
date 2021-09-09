package org.javacord.core.interaction;

import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.internal.SlashCommandOptionChoiceBuilderDelegate;

public class SlashCommandOptionChoiceBuilderDelegateImpl
        implements SlashCommandOptionChoiceBuilderDelegate {

    private String name;
    private String stringValue;
    private Integer intValue;

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public void setValue(final String value) {
        stringValue = value;
        intValue = null;
    }

    @Override
    public void setValue(final int value) {
        stringValue = null;
        intValue = value;
    }

    @Override
    public SlashCommandOptionChoice build() {
        return new SlashCommandOptionChoiceImpl(name, stringValue, intValue);
    }
}
