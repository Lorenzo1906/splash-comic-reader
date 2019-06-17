package com.mythicalcreaturesoftware.splash.filereader.impl;

import com.mythicalcreaturesoftware.splash.filereader.FileReader;
import com.mythicalcreaturesoftware.splash.filereader.FileReaderType;

public class CbrFileReader extends FileReader {

    public CbrFileReader(String path) {
        super(FileReaderType.CBR, path);
        construct();
    }

    @Override
    protected void construct() {
        System.out.println("Creating CbrFileReader");
    }
}
