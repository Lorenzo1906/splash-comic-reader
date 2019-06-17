package com.mythicalcreaturesoftware.splash.service;

public interface FileService {
    String loadFile(String path);
    String getCurrentPath();
    Integer getTotalPages();
    Integer getCurrentPage();
}
