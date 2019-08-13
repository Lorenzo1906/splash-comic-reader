package com.mythicalcreaturesoftware.splash.filereader;

import com.mythicalcreaturesoftware.splash.exception.InsufficientDataException;
import com.mythicalcreaturesoftware.splash.model.Spread;
import com.mythicalcreaturesoftware.splash.utils.ImageHelper;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class FileReader {

    private Map<Integer, Spread> fileEntries;
    private Map<Integer, String> previews;
    protected Map<Integer, String> pages;
    protected Map<Integer, Dimension> dimensions;
    protected Path tempFolderPath;
    protected String filePath;
    protected int totalPages;
    private Integer index;

    private FileReaderType type = null;

    protected abstract void construct();

    public FileReader (FileReaderType type, String filePath) {
        this.type = type;
        this.filePath = filePath;
        this.index = 1;

        try {
            construct();
            generatePreviewImages();
            fileEntries = groupPages();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        }
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

    private Map<Integer, Spread> groupPages() throws InsufficientDataException {
        if (pages == null || dimensions == null) {
            throw new InsufficientDataException("Insufficient data to group pages");
        }

        Map<Integer, Spread> spreads = new HashMap<>();

        boolean isFirst = true;
        Spread spread = null;

        for (Integer index : pages.keySet()) {

            //The first one is always alone on the spread
            if (isFirst || shouldBeAlone(dimensions.get(index))) {
                spread = new Spread();
                spread.setVerso(pages.get(index));
                spread.setVersoPageNumber(index);
                spread.setVersoSize(dimensions.get(index));
                spread.setVersoPreview(previews.get(index));

                spreads.put(index, spread);

                isFirst = false;
                spread = null;
            } else {
                if (spread == null) {
                    spread = new Spread();
                    spread.setVerso(pages.get(index));
                    spread.setVersoPageNumber(index);
                    spread.setVersoSize(dimensions.get(index));
                    spread.setVersoPreview(previews.get(index));

                    spreads.put(index, spread);
                } else {
                    spread.setRecto(pages.get(index));
                    spread.setRectoPageNumber(index);
                    spread.setRectoSize(dimensions.get(index));
                    spread.setRectoPreview(previews.get(index));

                    spreads.put(index, spread);
                    spread = null;
                }
            }
        }

        return spreads;
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

    private boolean shouldBeAlone (Dimension dimension) {
        boolean result = false;

        if (dimension.width > dimension.height) {
            result = true;
        }

        return result;
    }

    private void generatePreviewImages() throws InsufficientDataException {
        if (pages == null || pages.size() == 0) {
            throw new InsufficientDataException("Insufficient data to generate previews");
        }

        previews = new HashMap<>();

        Path previewFolderPath = generatePreviewPath(tempFolderPath);
        for (Integer index : pages.keySet()) {
            try {
                String previewPath = generatePreviewImagePath(pages.get(index), previewFolderPath);
                previews.put(index, previewPath);
                ImageHelper.resize(pages.get(index), previewPath, 10);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String generatePreviewImagePath(String path, Path folderPath) {
        if (path == null || path.equals("")) {
            return "";
        }
        if (folderPath == null) {
            return "";
        }

        String result = "";
        Path imagePath = Paths.get(path);

        try {
            Path previewImagePath = Files.createTempFile(folderPath, imagePath.getFileName().toString(), "." + FilenameUtils.getExtension(imagePath.getFileName().toString()));
            previewImagePath.toFile().deleteOnExit();

            result = previewImagePath.toUri().toURL().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private Path generatePreviewPath(Path path) {
        if (path == null) {
            return null;
        }

        Path previewFolderPath = null;
        try {

            previewFolderPath = Files.createTempDirectory(path, "preview");
            previewFolderPath.toFile().deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return previewFolderPath;
    }
}
