package com.mythicalcreaturesoftware.splash.filereader;

import com.mythicalcreaturesoftware.splash.exception.UnsupportedFileTypeException;
import com.mythicalcreaturesoftware.splash.filereader.impl.CbrFileReader;
import com.mythicalcreaturesoftware.splash.filereader.impl.CbzFileReader;

public class FileReaderFactory {

    public static FileReader buildFileReader(FileReaderType type, String path) throws UnsupportedFileTypeException {

        FileReader fileReader;

        switch (type) {
            case CBR:
                fileReader = new CbrFileReader(path);
                break;

            case CBZ:
                fileReader = new CbzFileReader(path);
                break;

            default:
                throw new UnsupportedFileTypeException("Unsupported File Type: " + type);
        }

        return fileReader;
    }
}
