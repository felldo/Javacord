package org.javacord.core.util.logging;

import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class WebSocketLogger implements WebSocketListener {
    private static final Logger logger = LoggerUtil.getLogger(WebSocketLogger.class);

    @Override
    public void onStateChanged(final WebSocket websocket, final WebSocketState newState) {
        logger.trace("onStateChanged: newState='{}'", newState);
    }

    @Override
    public void onConnectError(final WebSocket websocket, final WebSocketException cause) {
        logger.trace("onConnectError", cause);
    }

    @Override
    public void onDisconnected(final WebSocket websocket, final WebSocketFrame serverCloseFrame, final WebSocketFrame clientCloseFrame,
                               final boolean closedByServer) {
        logger.trace("onDisconnected: closedByServer='{}' serverCloseFrame='{}' clientCloseFrame='{}'",
                closedByServer, serverCloseFrame, clientCloseFrame);
    }

    @Override
    public void onFrame(final WebSocket websocket, final WebSocketFrame frame) {
        logger.trace("onFrame: frame='{}'", frame);
    }

    @Override
    public void onContinuationFrame(final WebSocket websocket, final WebSocketFrame frame) {
        logger.trace("onContinuationFrame: frame='{}'", frame);
    }

    @Override
    public void onTextFrame(final WebSocket websocket, final WebSocketFrame frame) {
        logger.trace("onTextFrame: frame='{}'", frame);
    }

    @Override
    public void onBinaryFrame(final WebSocket websocket, final WebSocketFrame frame) {
        logger.trace("onBinaryFrame: frame='{}'", frame);
    }

    @Override
    public void onCloseFrame(final WebSocket websocket, final WebSocketFrame frame) {
        logger.trace("onCloseFrame: frame='{}'", frame);
    }

    @Override
    public void onPingFrame(final WebSocket websocket, final WebSocketFrame frame) {
        logger.trace("onPingFrame: frame='{}'", frame);
    }

    @Override
    public void onPongFrame(final WebSocket websocket, final WebSocketFrame frame) {
        logger.trace("onPongFrame: frame='{}'", frame);
    }

    @Override
    public void onTextMessage(final WebSocket websocket, final String text) {
        logger.trace("onTextMessage: text='{}'", text);
    }

    @Override
    public void onTextMessage(final WebSocket websocket, final byte[] data) {
        logger.trace("onTextFrame: data='{}'", data);
    }

    @Override
    public void onBinaryMessage(final WebSocket websocket, final byte[] binary) {
        logger.trace("onBinaryMessage: binary='{}'", binary);
    }

    @Override
    public void onSendingFrame(final WebSocket websocket, final WebSocketFrame frame) {
        logger.trace("onSendingFrame: frame='{}'", frame);
    }

    @Override
    public void onFrameSent(final WebSocket websocket, final WebSocketFrame frame) {
        logger.trace("onFrameSent: frame='{}'", frame);
    }

    @Override
    public void onFrameUnsent(final WebSocket websocket, final WebSocketFrame frame) {
        logger.trace("onFrameUnsent: frame='{}'", frame);
    }

    @Override
    public void onThreadCreated(final WebSocket websocket, final ThreadType threadType, final Thread thread) {
        logger.trace("onThreadCreated: threadType='{}' thread='{}'", threadType, thread);
    }

    @Override
    public void onThreadStarted(final WebSocket websocket, final ThreadType threadType, final Thread thread) {
        logger.trace("onThreadStarted: threadType='{}' thread='{}'", threadType, thread);
    }

    @Override
    public void onThreadStopping(final WebSocket websocket, final ThreadType threadType, final Thread thread) {
        logger.trace("onThreadStopping: threadType='{}' thread='{}'", threadType, thread);
    }

    @Override
    public void onConnected(final WebSocket websocket, final Map<String, List<String>> headers) {
        logger.trace("onConnected: headers='{}'", headers);
    }

    @Override
    public void onError(final WebSocket websocket, final WebSocketException cause) {
        logger.trace("onError", cause);
    }

    @Override
    public void onFrameError(final WebSocket websocket, final WebSocketException cause, final WebSocketFrame frame) {
        logger.trace("onFrameError: frame='{}'", frame, cause);
    }

    @Override
    public void onMessageError(final WebSocket websocket, final WebSocketException cause, final List<WebSocketFrame> frames) {
        logger.trace("onMessageError: frames='{}'", frames, cause);
    }

    @Override
    public void onMessageDecompressionError(final WebSocket websocket, final WebSocketException cause, final byte[] compressed) {
        logger.trace("onMessageDecompressionError: compressed='{}'", compressed, cause);
    }

    @Override
    public void onTextMessageError(final WebSocket websocket, final WebSocketException cause, final byte[] data) {
        logger.trace("onTextMessageError: data='{}'", data, cause);
    }

    @Override
    public void onSendError(final WebSocket websocket, final WebSocketException cause, final WebSocketFrame frame) {
        logger.trace("onSendError: frame='{}'", frame, cause);
    }

    @Override
    public void onUnexpectedError(final WebSocket websocket, final WebSocketException cause) {
        logger.trace("onUnexpectedError", cause);
    }

    @Override
    public void handleCallbackError(final WebSocket websocket, final Throwable cause) {
        logger.trace("handleCallbackError", cause);
    }

    @Override
    public void onSendingHandshake(final WebSocket websocket, final String requestLine, final List<String[]> headers) {
        logger.trace("onSendingHandshake: requestLine='{}' headers='{}'", requestLine, headers);
    }
}
