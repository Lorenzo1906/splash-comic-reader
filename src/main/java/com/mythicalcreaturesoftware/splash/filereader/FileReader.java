package com.mythicalcreaturesoftware.splash.filereader;

import com.mythicalcreaturesoftware.splash.model.Spread;

import java.util.Map;

public abstract class FileReader {

    protected Map<Integer, Spread> fileEntries;
    protected String filePath;
    protected int totalPages;
    protected Integer index;

    protected abstract void construct();

    private FileReaderType type = null;

    public FileReader (FileReaderType type, String filePath) {
        this.type = type;
        this.filePath = filePath;
        this.index = 1;
    }

    public Spread getCurrentPath (){
        return fileEntries.get(index);
    }

    public Spread getPath (Integer pageNumber){
        return fileEntries.get(pageNumber);
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

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
