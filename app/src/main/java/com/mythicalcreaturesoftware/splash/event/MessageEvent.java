package com.mythicalcreaturesoftware.splash.event;

import javafx.event.Event;
import javafx.event.EventType;

public abstract class MessageEvent extends Event {

    public static final EventType<MessageEvent> OPEN_FILE_EVENT = new EventType<>(ANY, "OPEN_FILE_EVENT");
    public static final EventType<MessageEvent> PREVIOUS_PAGE_EVENT = new EventType<>(ANY, "PREVIOUS_PAGE_EVENT");
    public static final EventType<MessageEvent> NEXT_PAGE_EVENT = new EventType<>(ANY, "NEXT_PAGE_EVENT");
    public static final EventType<MessageEvent> CHANGE_READING_DIRECTION_EVENT = new EventType<>(ANY, "CHANGE_READING_DIRECTION_EVENT");
    public static final EventType<MessageEvent> CHANGE_PAGES_PER_VIEW_EVENT = new EventType<>(ANY, "CHANGE_PAGES_PER_VIEW_EVENT");
    public static final EventType<MessageEvent> ZOOM_IN_EVENT = new EventType<>(ANY, "ZOOM_IN_EVENT");
    public static final EventType<MessageEvent> ZOOM_OUT_EVENT = new EventType<>(ANY, "ZOOM_OUT_EVENT");
    public static final EventType<MessageEvent> SET_DEFAULT_SCALE_EVENT = new EventType<>(ANY, "SET_DEFAULT_SCALE_EVENT");
    public static final EventType<MessageEvent> FULLSCREEN_EVENT = new EventType<>(ANY, "FULLSCREEN_EVENT");
    public static final EventType<MessageEvent> ENTERED_FULLSCREEN_EVENT = new EventType<>(ANY, "ENTERED_FULLSCREEN_EVENT");
    public static final EventType<MessageEvent> EXITED_FULLSCREEN_EVENT = new EventType<>(ANY, "EXITED_FULLSCREEN_EVENT");

    public MessageEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    public abstract void invokeHandler(MessageEventHandler handler);
}
