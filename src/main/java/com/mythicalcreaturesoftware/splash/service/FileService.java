package com.mythicalcreaturesoftware.splash.service;

import java.awt.*;

public interface FileService {
    String loadFile(String path);
    String getCurrentRecto();
    String getCurrentVerso();
    String getCurrentPage();
    String getCurrentPageByPageNumber(Integer pageNumber);
    Dimension getCurrentRectoSize();
    Dimension getCurrentVersoSize();
    Dimension getCurrentPageSize();
    Integer getTotalPages();
    Integer getCurrentPageNumber();
    boolean canChangeToNextPage(boolean isTwoPage);
    void setCurrentPage(Integer pageNumber);
    void updateNextPage(boolean isTwoPage);
    void updatePreviousPage(boolean isTwoPage);
}
