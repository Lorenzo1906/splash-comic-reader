package com.mythicalcreaturesoftware.splash;

import com.mythicalcreaturesoftware.splash.app.ComicReaderApp;
import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Initializing comic reader application");

        Application.launch(ComicReaderApp.class, args);
    }
}
