package com.mythicalcreaturesoftware.splash.event;

import javafx.event.EventHandler;

public abstract class MessageEventHandler implements EventHandler<MessageEvent> {

    public abstract void onActionDone();

    @Override
    public void handle(MessageEvent event) {
        event.invokeHandler(this);
    }
}
