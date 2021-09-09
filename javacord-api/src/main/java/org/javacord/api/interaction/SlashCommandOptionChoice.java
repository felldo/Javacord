package org.javacord.api.interaction;

import java.util.Optional;

/**
 * A choice for a slash command option.
 */
public interface SlashCommandOptionChoice {

    /**
     * Gets the name of this choice.
     *
     * @return The name of the choice.
     */
    String getName();

    /**
     * Gets the string value of this choice.
     *
     * <p>If this option is an integer choice, the optional will be empty.
     *
     * @return The string value of this choice.
     */
    Optional<String> getStringValue();

    /**
     * Gets the integer value of this choice.
     *
     * <p>If this option is a string choice, the optional will be empty.
     *
     * @return The integer value of this choice.
     */
    Optional<Integer> getIntValue();

    /**
     * Gets the value of this choice as a string.
     *
     * <p>If the value is an integer, its string representation will be returned.
     *
     * @return The value of the choice as a string.
     */
    default String getValueAsString() {
        return getStringValue().orElseGet(() -> getIntValue()
            .map(String::valueOf)
            .orElseThrow(() ->
                new AssertionError("slash command option choice value that's neither a string nor int")));
    }

    /**
     * Create a new option choice builder to be used with a command option builder.
     *
     * @param name The name of the choice.
     * @param value The value of the choice.
     * @return The new choice builder.
     */
    static SlashCommandOptionChoice create(final String name, final String value) {
        return new SlashCommandOptionChoiceBuilder()
            .setName(name)
            .setValue(value)
            .build();
    }

    /**
     * Create a new option choice builder to be used with a command option builder.
     *
     * @param name The name of the choice.
     * @param value The value of the choice.
     * @return The new choice builder.
     */
    static SlashCommandOptionChoice create(final String name, final int value) {
        return new SlashCommandOptionChoiceBuilder()
            .setName(name)
            .setValue(value)
            .build();
    }
}
