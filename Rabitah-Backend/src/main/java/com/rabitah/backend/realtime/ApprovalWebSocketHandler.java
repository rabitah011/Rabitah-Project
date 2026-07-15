package com.rabitah.backend.realtime;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class ApprovalWebSocketHandler extends TextWebSocketHandler {
    private final ApprovalEvents events;
    public ApprovalWebSocketHandler(ApprovalEvents events) { this.events = events; }
    @Override public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        events.connected(session); session.sendMessage(new TextMessage("CONNECTED"));
    }
    @Override public void afterConnectionClosed(WebSocketSession session, CloseStatus status) { events.disconnected(session); }
    @Override public void handleTransportError(WebSocketSession session, Throwable exception) { events.disconnected(session); }
}
