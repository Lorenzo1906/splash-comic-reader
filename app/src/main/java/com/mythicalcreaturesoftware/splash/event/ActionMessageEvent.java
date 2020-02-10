package com.mythicalcreaturesoftware.splash.event;

import javafx.event.EventType;

public class ActionMessageEvent extends MessageEvent {

    public ActionMessageEvent(EventType<MessageEvent> type) {
        super(type);
    }

    @Override
    public void invokeHandler(MessageEventHandler handler) {
        handler.onActionDone();
    }
}
