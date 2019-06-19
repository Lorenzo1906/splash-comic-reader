package com.mythicalcreaturesoftware.splash.service;

public interface FileService {
    String loadFile(String path);
    String getCurrentRecto();
    String getCurrentVerso();
    String getPath(Integer pageNumber);
    Integer getTotalPages();
    Integer getCurrentPage();
    void setCurrentPage(Integer pageNumber);
}
