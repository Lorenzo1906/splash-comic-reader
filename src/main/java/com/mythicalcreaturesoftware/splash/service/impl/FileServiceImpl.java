package com.mythicalcreaturesoftware.splash.service.impl;

import com.mythicalcreaturesoftware.splash.exception.UnsupportedFileTypeException;
import com.mythicalcreaturesoftware.splash.filereader.FileReader;
import com.mythicalcreaturesoftware.splash.filereader.FileReaderFactory;
import com.mythicalcreaturesoftware.splash.filereader.FileReaderType;
import com.mythicalcreaturesoftware.splash.model.Spread;
import com.mythicalcreaturesoftware.splash.service.FileService;
import com.mythicalcreaturesoftware.splash.ui.Keys;
import org.apache.commons.io.FilenameUtils;

import java.security.Key;

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
    public String getPath(Integer pageNumber) {
        String result = Keys.DEFAULT_IMAGE_PATH;

        if (fileReader != null) {
            Spread spread = fileReader.getPath(pageNumber);

            if (spread != null && spread.getVersoPageNumber() != null && spread.getVersoPageNumber().equals(pageNumber)) {
                result = spread.getVerso();
            }

            if (spread != null && spread.getRectoPageNumber() != null && spread.getRectoPageNumber().equals(pageNumber)) {
                result = spread.getRecto();
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
    public Integer getCurrentPage() {
        if (fileReader == null) {
            return 1;
        }

        return fileReader.getIndex();
    }

    @Override
    public void setCurrentPage(Integer pageNumber) {
        if (fileReader == null) {
            return;
        }

        fileReader.setIndex(pageNumber);
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
