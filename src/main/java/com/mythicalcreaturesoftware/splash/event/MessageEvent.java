package com.mythicalcreaturesoftware.splash.event;

import javafx.event.Event;
import javafx.event.EventType;

public abstract class MessageEvent extends Event {

    public static final EventType<MessageEvent> PAGE_EVENT = new EventType<>(ANY, "PAGE_EVENT");

    public MessageEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    public abstract void invokeHandler(MessageEventHandler handler);
}
