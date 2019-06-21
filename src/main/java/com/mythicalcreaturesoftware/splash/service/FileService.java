package com.mythicalcreaturesoftware.splash.service;

public interface FileService {
    String loadFile(String path);
    String getCurrentRecto();
    String getCurrentVerso();
    String getCurrentPage();
    Integer getTotalPages();
    Integer getCurrentPageNumber();
    boolean canChangeToNextPage(boolean isTwoPage);
    void setCurrentPage(Integer pageNumber);
    void updateNextPage(boolean isTwoPage);
    void updatePreviousPage(boolean isTwoPage);
}
