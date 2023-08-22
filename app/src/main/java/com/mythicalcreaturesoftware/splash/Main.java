package com.mythicalcreaturesoftware.splash;

import com.mythicalcreaturesoftware.splash.app.ComicReaderApp;
import javafx.application.Application;
import library.service.LibraryService;
import library.service.impl.LibraryServiceImpl;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Initializing comic reader application");

        for (String arg : args) {
            processArg(arg);
        }

        //LibraryService libraryService = new LibraryServiceImpl();
        //libraryService.indexFolder("/home/lorenzo/Downloads/Spawn 001-010 (1992-1993) (Digital)");

        Application.launch(ComicReaderApp.class, args);
    }

    private static void setDebugLoggerLevel() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(Level.DEBUG);
        context.updateLoggers();
    }

    private static void processArg(String arg) {
        if ("--debug".equals(arg)) {
            setDebugLoggerLevel();
        }
    }
}
