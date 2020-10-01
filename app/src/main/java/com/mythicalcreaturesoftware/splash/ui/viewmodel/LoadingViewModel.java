package com.mythicalcreaturesoftware.splash.ui.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadingViewModel implements ViewModel {

    private static final Logger logger = LoggerFactory.getLogger(LoadingViewModel.class);

    public LoadingViewModel() {
        logger.info("Initializing loading view model");
    }
}
