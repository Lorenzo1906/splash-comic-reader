package com.mythicalcreaturesoftware.splash.service.impl;

import com.mythicalcreaturesoftware.splash.exception.UnsupportedFileTypeException;
import com.mythicalcreaturesoftware.splash.filereader.FileReader;
import com.mythicalcreaturesoftware.splash.filereader.FileReaderFactory;
import com.mythicalcreaturesoftware.splash.filereader.FileReaderType;
import com.mythicalcreaturesoftware.splash.model.Spread;
import com.mythicalcreaturesoftware.splash.service.FileService;
import com.mythicalcreaturesoftware.splash.ui.Keys;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;

public class FileServiceImpl implements FileService {

    private FileReader fileReader;

    @Override
    public String loadFile(String path) {
        try {
            getFileReaderTypeFromPath(path);
            fileReader = FileReaderFactory.buildFileReader(getFileReaderTypeFromPath(path), path);
        } catch (UnsupportedFileTypeException e) {
            e.printStackTrace();
        }

        return getFilenameFromPath(path);
    }

    @Override
    public String getCurrentRecto() {
        String result = Keys.DEFAULT_IMAGE_PATH;

        if (fileReader != null) {

            Spread spread = fileReader.getCurrentPath();

            if (spread != null && spread.getRecto() != null) {
                result = spread.getRecto();
            }
        }

        return result;
    }

    @Override
    public String getCurrentVerso() {
        String result = Keys.DEFAULT_IMAGE_PATH;

        if (fileReader != null) {

            Spread spread = fileReader.getCurrentPath();

            if (spread != null && spread.getVerso() != null) {
                result = spread.getVerso();
            }
        }

        return result;
    }

    @Override
    public String getCurrentPage() {
        String result = Keys.DEFAULT_IMAGE_PATH;

        if (fileReader != null) {
            Spread spread = fileReader.getPath(fileReader.getIndex());

            if (spread != null && spread.getVersoPageNumber() != null && spread.getVersoPageNumber().equals(fileReader.getIndex())) {
                result = spread.getVerso();
            }

            if (spread != null && spread.getRectoPageNumber() != null && spread.getRectoPageNumber().equals(fileReader.getIndex())) {
                result = spread.getRecto();
            }
        }

        return result;
    }

    @Override
    public Dimension getCurrentRectoSize() {
        Dimension result = new Dimension(1, 1);

        if (fileReader != null) {

            Spread spread = fileReader.getCurrentPath();

            if (spread != null && spread.getRecto() != null) {
                result = spread.getRectoSize();
            }
        }

        return result;
    }

    @Override
    public Dimension getCurrentVersoSize() {
        Dimension result = new Dimension(1, 1);

        if (fileReader != null) {

            Spread spread = fileReader.getCurrentPath();

            if (spread != null && spread.getRecto() != null) {
                result = spread.getVersoSize();
            }
        }

        return result;
    }

    @Override
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

    @Override
    public Integer getTotalPages() {
        if (fileReader == null) {
            return 1;
        }

        return fileReader.getTotalPages();
    }

    @Override
    public Integer getCurrentPageNumber() {
        if (fileReader == null) {
            return 1;
        }

        return fileReader.getIndex();
    }

    @Override
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

    @Override
    public void setCurrentPage(Integer pageNumber) {
        if (fileReader == null) {
            return;
        }

        fileReader.setIndex(pageNumber);
    }

    @Override
    public void updateNextPage(boolean isTwoPage) {
        if (!isTwoPage && fileReader.getIndex() + 1 <= fileReader.getTotalPages()) {

            fileReader.setIndex(fileReader.getIndex() + 1);
        } else {

            Spread currentSpread = fileReader.getCurrentPath();
            Spread nextSpread = fileReader.getPath(fileReader.getIndex() + 1);

            if (currentSpread == nextSpread && fileReader.getIndex() + 2 <= fileReader.getTotalPages()) {
                fileReader.setIndex(fileReader.getIndex() + 2);
            } else if (fileReader.getIndex() + 1 <= fileReader.getTotalPages()) {
                fileReader.setIndex(fileReader.getIndex() + 1);
            }
        }
    }

    @Override
    public void updatePreviousPage(boolean isTwoPage) {
        if (!isTwoPage && fileReader.getIndex() - 1 == 1) {

            fileReader.setIndex(fileReader.getIndex() - 1);
        } else {

            Spread currentSpread = fileReader.getCurrentPath();
            Spread nextSpread = fileReader.getPath(fileReader.getIndex() - 1);

            if (currentSpread == nextSpread) {
                fileReader.setIndex(fileReader.getIndex() - 2);
            } else {
                fileReader.setIndex(fileReader.getIndex() - 1);
            }
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
        }

        return type;
    }
}
