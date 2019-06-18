package com.mythicalcreaturesoftware.splash.filereader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileReader {

    private static Logger logger = LoggerFactory.getLogger(FileReader.class);

    protected String[] fileEntries;
    protected String filePath;
    private int index;
    protected int totalPages;

    protected abstract void construct();

    private FileReaderType type = null;

    public FileReader (FileReaderType type, String filePath) {
        this.type = type;
        this.filePath = filePath;
        setIndex(0);
    }

    public String getCurrentPath (){
        return fileEntries[index];
    }

    public String getPath (Integer pageNumber){
        return fileEntries[pageNumber];
    }

    public FileReaderType getType() {
        return type;
    }

    public void setType(FileReaderType type) {
        this.type = type;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
