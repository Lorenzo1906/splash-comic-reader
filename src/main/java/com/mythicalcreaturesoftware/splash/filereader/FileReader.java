package com.mythicalcreaturesoftware.splash.filereader;

import com.mythicalcreaturesoftware.splash.model.Spread;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
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


    protected Dimension getPageSize(Path file) {
        Dimension dimension = new Dimension();

        try {

            ImageInputStream iis = ImageIO.createImageInputStream(file.toFile());
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if (readers.hasNext()) {

                ImageReader reader = readers.next();
                reader.setInput(iis, true);
                int width = reader.getWidth(reader.getMinIndex());
                int height = reader.getHeight(reader.getMinIndex());

                dimension.setSize(width, height);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dimension;
    }
}
