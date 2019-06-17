package com.mythicalcreaturesoftware.splash.service.impl;

import com.mythicalcreaturesoftware.splash.exception.UnsupportedFileTypeException;
import com.mythicalcreaturesoftware.splash.filereader.FileReader;
import com.mythicalcreaturesoftware.splash.filereader.FileReaderFactory;
import com.mythicalcreaturesoftware.splash.filereader.FileReaderType;
import com.mythicalcreaturesoftware.splash.service.FileService;
import org.apache.commons.io.FilenameUtils;

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
    public String getCurrentPath() {
        if (fileReader == null) {
            return "";
        }

        return fileReader.getCurrentPath();
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
