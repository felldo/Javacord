package org.javacord.core.util.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.spi.AbstractLogger;

import java.util.HashSet;
import java.util.Set;

/**
 * This logger is used to wrap another logger and replace configured sensitive data by asterisks.
 */
public class PrivacyProtectionLogger extends AbstractLogger {

    private static final String PRIVATE_DATA_REPLACEMENT = "**********";
    private static final Set<String> privateDataSet = new HashSet<>();

    private final Logger delegate;

    /**
     * Class constructor.
     * It's recommended to use {@link LoggerUtil#getLogger(String)}.
     *
     * @param delegate The delegate logger that gets the cleaned messages.
     */
    PrivacyProtectionLogger(final Logger delegate) {
        this.delegate = delegate;
    }

    /**
     * Adds a private data to be asterisked out in log messages.
     * A {@code null} argument is simply ignored.
     *
     * @param privateData The private data.
     */
    public static void addPrivateData(final String privateData) {
        if (privateData != null && !privateData.trim().isEmpty()) {
            privateDataSet.add(privateData);
        }
    }

    @Override
    public void logMessage(final String fqcn, final Level level, final Marker marker, final Message message, final Throwable t) {
        final String formattedMessage = message.getFormattedMessage();
        if (privateDataSet.stream().anyMatch(formattedMessage::contains)) {
            delegate.log(level, marker, privateDataSet.stream().reduce(
                    formattedMessage, (s, privateData) -> s.replace(privateData, PRIVATE_DATA_REPLACEMENT)), t);
        } else {
            delegate.log(level, marker, message, t);
        }
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final Message message, final Throwable t) {
        return delegate.isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final CharSequence message, final Throwable t) {
        return delegate.isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final Object message, final Throwable t) {
        return delegate.isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Throwable t) {
        return delegate.isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message) {
        return delegate.isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object... params) {
        return delegate.isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0) {
        return delegate.isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1) {
        return delegate.isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
        return delegate.isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        return delegate.isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
                             final Object p4) {
        return delegate.isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
                             final Object p4, final Object p5) {
        return delegate.isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
                             final Object p4, final Object p5, final Object p6) {
        return delegate.isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
                             final Object p4, final Object p5, final Object p6, final Object p7) {
        return delegate.isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
                             final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        return delegate.isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
                             final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        return delegate.isEnabled(level, marker);
    }

    @Override
    public Level getLevel() {
        return delegate.getLevel();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }
}
