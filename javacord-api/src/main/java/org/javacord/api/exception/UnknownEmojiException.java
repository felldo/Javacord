package org.javacord.api.exception;

import org.javacord.api.util.rest.RestRequestInformation;
import org.javacord.api.util.rest.RestRequestResponseInformation;

/**
 * When the emoji that was sent, for example as reaction, is not an emoji Discord knows about.
 */
public class UnknownEmojiException extends BadRequestException {

    /**
     * Creates a new instance of this class.
     *
     * @param origin The origin of the exception.
     * @param message The message of the exception.
     * @param request The information about the request.
     * @param response The information about the response.
     */
    public UnknownEmojiException(final Exception origin, final String message, final RestRequestInformation request,
                                 final RestRequestResponseInformation response) {
        super(origin, message, request, response);
    }

}
