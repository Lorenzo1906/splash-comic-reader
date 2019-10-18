package com.mythicalcreaturesoftware.splash.ui.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FullscreenViewModel implements ViewModel {

    private static Logger logger = LoggerFactory.getLogger(LoadingViewModel.class);

    public FullscreenViewModel() {
        logger.info("Initializing fullscreen view model");
    }
}
