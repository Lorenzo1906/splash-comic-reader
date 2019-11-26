package com.mythicalcreaturesoftware.splash.service.impl;

import com.mythicalcreaturesoftware.splash.exception.UnsupportedFileTypeException;
import com.mythicalcreaturesoftware.splash.filereader.FileReader;
import com.mythicalcreaturesoftware.splash.filereader.FileReaderFactory;
import com.mythicalcreaturesoftware.splash.filereader.FileReaderType;
import com.mythicalcreaturesoftware.splash.model.Spread;
import com.mythicalcreaturesoftware.splash.utils.DefaultValuesHelper;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Dimension;

public class FileServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    private static volatile FileServiceImpl instance = null;

    private FileReader fileReader;

    private FileServiceImpl () {}

    public static FileServiceImpl getInstance() {
        if (instance == null) {
            synchronized (FileServiceImpl.class) {
                if (instance == null) {
                    instance = new FileServiceImpl();
                }
            }
        }

        return instance;
    }

    public void unloadFile() {
        logger.info("Unloading file");
        fileReader = null;
    }

    public String loadFile(String path) {
        logger.info("Loading file");

        try {
            fileReader = FileReaderFactory.buildFileReader(getFileReaderTypeFromPath(path), path);
        } catch (UnsupportedFileTypeException e) {
            logger.error(e.getMessage());
        }

        return getFilenameFromPath(path);
    }

    public String getCurrentRecto() {
        return getCurrentPageValue(false);
    }

    public String getCurrentVerso() {
        return getCurrentPageValue(true);
    }

    private String getCurrentPageValue(boolean verso) {
        String result = "";

        if (fileReader != null) {

            Spread spread = fileReader.getCurrentPath();

            if (spread != null && spread.getVerso() != null) {
                result = verso ? spread.getVerso() : spread.getRecto();
            }
        }

        if (result == null || result.equals("")) {
            result = DefaultValuesHelper.DEFAULT_IMAGE_PATH;
        }

        return result;
    }

    public String getCurrentPage() {
        return getPageByPageNumber(fileReader.getIndex());
    }

    public String getCurrentPreviewByPageNumber(Integer pageNumber) {
        return getPageValueByNumber(pageNumber, true);
    }

    private String getPageByPageNumber(Integer pageNumber) {
        return getPageValueByNumber(pageNumber, false);
    }

    private String getPageValueByNumber(Integer pageNumber, boolean preview) {
        String result = DefaultValuesHelper.DEFAULT_IMAGE_PATH;

        if (fileReader != null) {
            Spread spread = fileReader.getPath(pageNumber);

            if (spread != null && spread.getVersoPageNumber() != null && spread.getVersoPageNumber().equals(pageNumber)) {
                result = preview ? spread.getVersoPreview() : spread.getVerso();
            }

            if (spread != null && spread.getRectoPageNumber() != null && spread.getRectoPageNumber().equals(pageNumber)) {
                result = preview ? spread.getRectoPreview() : spread.getRecto();
            }
        }

        return result;
    }

    public Dimension getCurrentRectoSize() {
        return getSize(false);
    }

    public Dimension getCurrentVersoSize() {
        return getSize(true);
    }

    private Dimension getSize(boolean verso) {
        Dimension result = null;

        if (fileReader != null) {

            Spread spread = fileReader.getCurrentPath();

            if (spread != null && spread.getVerso() != null) {
                result = verso ? spread.getVersoSize() : spread.getRectoSize();
            }
        }

        if (result == null) {
            result = new Dimension(1, 1);
        }

        return result;
    }

    public Dimension getCurrentPageSize() {
        Dimension result = new Dimension(1, 1);

        if (fileReader != null) {
            Spread spread = fileReader.getPath(fileReader.getIndex());

            if (spread != null && spread.getVersoPageNumber() != null && spread.getVersoPageNumber().equals(fileReader.getIndex())) {
                result = spread.getVersoSize();
            }

            if (spread != null && spread.getRectoPageNumber() != null && spread.getRectoPageNumber().equals(fileReader.getIndex())) {
                result = spread.getRectoSize();
            }
        }

        return result;
    }

    public Integer getTotalPages() {
        if (fileReader == null) {
            return 1;
        }

        return fileReader.getTotalPages();
    }

    public Integer getCurrentPageNumber() {
        if (fileReader == null) {
            return 1;
        }

        return fileReader.getIndex();
    }

    public boolean canChangeToNextPage(boolean isTwoPage) {
        if (fileReader == null) {
            return false;
        }

        boolean result = true;

        if (isTwoPage) {

            Spread spread = fileReader.getPath(fileReader.getIndex());

            if (spread != null && spread.getRectoPageNumber() != null && spread.getRectoPageNumber() == fileReader.getTotalPages()) {
                result = false;
            }

            if (spread != null && spread.getVersoPageNumber() != null && spread.getVersoPageNumber() == fileReader.getTotalPages()) {
                result = false;
            }
        } else {
            if (fileReader.getIndex() >= fileReader.getTotalPages()) {
                result = false;
            }
        }

        return result;
    }

    public boolean getMangaMode() {
        return fileReader.getIsMangaMode();
    }

    public boolean changeMangaMode() {
        fileReader.changeMangaMode();
        return getMangaMode();
    }

    public void setCurrentPage(Integer pageNumber) {
        if (fileReader == null) {
            return;
        }

        fileReader.setIndex(pageNumber);
    }

    public void updateNextPage(boolean isTwoPage) {
        if (!isTwoPage && fileReader.getIndex() + 1 <= fileReader.getTotalPages()) {

            fileReader.setIndex(fileReader.getIndex() + 1);
        } else {
            updateSpreadPage(1);
        }
    }

    public void updatePreviousPage(boolean isTwoPage) {
        if (!isTwoPage && fileReader.getIndex() - 1 == 1) {

            fileReader.setIndex(fileReader.getIndex() - 1);
        } else {
            updateSpreadPage(-1);
        }
    }

    private void updateSpreadPage(int sign) {
        Spread currentSpread = fileReader.getCurrentPath();
        Spread nextSpread = fileReader.getPath(fileReader.getIndex() + sign);

        if (currentSpread.equals(nextSpread) && fileReader.getIndex() + 2 <= fileReader.getTotalPages()) {
            fileReader.setIndex(fileReader.getIndex() + (2 * sign));
        } else if (fileReader.getIndex() + sign <= fileReader.getTotalPages()) {
            fileReader.setIndex(fileReader.getIndex() + sign);
        }
    }

    private String getFilenameFromPath(String path) {
        return FilenameUtils.getName(path);
    }

    private FileReaderType getFileReaderTypeFromPath (String path) {

        String extension = FilenameUtils.getExtension(path).toLowerCase();

        FileReaderType type = null;

        switch (extension) {
            case "cbr":
                type = FileReaderType.CBR;
                break;
            case "cbz":
                type = FileReaderType.CBZ;
                break;
            default:
        }

        return type;
    }
}
