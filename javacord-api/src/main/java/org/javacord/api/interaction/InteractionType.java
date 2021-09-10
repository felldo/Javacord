package org.javacord.api.interaction;

public enum InteractionType {

    PING(1),
    SLASH_COMMAND(2),
    MESSAGE_COMPONENT(3),
    UNKNOWN(-1);

    private final int value;

    InteractionType(final int value) {
        this.value = value;
    }

    /**
     * Gets integer value that represents this type.
     *
     * @return The integer value that represents this type.
     */
    public int getValue() {
        return value;
    }

    /**
     * Gets an {@code InteractionType} by its value.
     *
     * @param value The value of the interaction type.
     * @return The interaction type for the given value,
     *         or {@link InteractionType#UNKNOWN} if there's none with the given value.
     */
    public static InteractionType fromValue(final int value) {
        for (final InteractionType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
