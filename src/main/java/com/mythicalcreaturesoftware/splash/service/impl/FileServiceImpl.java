package com.mythicalcreaturesoftware.splash.service.impl;

import com.mythicalcreaturesoftware.splash.exception.UnsupportedFileTypeException;
import com.mythicalcreaturesoftware.splash.filereader.FileReader;
import com.mythicalcreaturesoftware.splash.filereader.FileReaderFactory;
import com.mythicalcreaturesoftware.splash.filereader.FileReaderType;
import com.mythicalcreaturesoftware.splash.model.Spread;
import com.mythicalcreaturesoftware.splash.service.FileService;
import com.mythicalcreaturesoftware.splash.utils.DefaultValuesHelper;
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
        return getCurrentPageValue(false);
    }

    @Override
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

    @Override
    public String getCurrentPage() {
        return getPageByPageNumber(fileReader.getIndex());
    }

    @Override
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

    @Override
    public Dimension getCurrentRectoSize() {
        return getSize(false);
    }

    @Override
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
    public boolean getMangaMode() {
        return fileReader.getIsMangaMode();
    }

    @Override
    public boolean changeMangaMode() {
        fileReader.changeMangaMode();
        return getMangaMode();
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
            updateSpreadPage(1);
        }
    }

    @Override
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
        } else if (fileReader.getIndex() + 1 <= fileReader.getTotalPages()) {
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
