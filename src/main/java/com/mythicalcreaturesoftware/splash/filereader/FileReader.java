package com.mythicalcreaturesoftware.splash.filereader;

import com.mythicalcreaturesoftware.splash.exception.InsufficientDataException;
import com.mythicalcreaturesoftware.splash.model.Spread;
import com.mythicalcreaturesoftware.splash.utils.ImageHelper;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class FileReader {
    private static Logger logger = LoggerFactory.getLogger(FileReader.class);

    private Map<Integer, Spread> fileEntries;
    private Map<Integer, String> previews;
    protected Map<Integer, String> pages;
    protected Map<Integer, Dimension> dimensions;
    protected Path tempFolderPath;
    protected String filePath;
    protected int totalPages;
    private Integer index;
    private Boolean isMangaMode;

    private FileReaderType type = null;

    protected abstract void construct();

    public FileReader (FileReaderType type, String filePath) {
        this.type = type;
        this.filePath = filePath;
        this.index = 1;
        this.isMangaMode = false;

        try {
            construct();
            generatePreviewImages();
            fileEntries = groupPages();
        } catch (InsufficientDataException e) {
            logger.error(e.getMessage());
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

    public Boolean getIsMangaMode() {
        return isMangaMode;
    }

    public void changeMangaMode () {
        isMangaMode = !isMangaMode;
        String currPage = getCurrentPage();

        fileEntries = invertPagesOrder();

        index = calculateNewIndex(currPage);
    }

    private String getCurrentPage () {
        String result = "";
        Spread currSpread = getPath(index);
        if (currSpread.getRecto() != null && currSpread.getRectoPageNumber().equals(index)) {
            result = currSpread.getRecto();
        }
        if (currSpread.getVerso() != null && currSpread.getVersoPageNumber().equals(index)) {
            result = currSpread.getVerso();
        }

        return result;
    }

    private int calculateNewIndex(String currPage) {
        int result = 0;
        for (Spread spread : fileEntries.values()) {
            if (spread.getVerso() != null && spread.getVerso().equals(currPage)) {
                result = spread.getVersoPageNumber();
                break;
            }
            if (spread.getRecto() != null && spread.getRecto().equals(currPage)) {
                result = spread.getRectoPageNumber();
                break;
            }
        }

        return result;
    }

    private Map<Integer, Spread> invertPagesOrder () {
        pages = invertMap(pages);
        previews = invertMap(previews);
        dimensions = invertDimensionsMap(dimensions);

        Map<Integer, Spread> result = null;

        try {
            result = groupPages();
        } catch (InsufficientDataException e) {
            logger.error(e.getMessage());
        }

        return result;
    }

    private Map<Integer, String> invertMap (Map<Integer, String> map) {
        Map<Integer, String> result = new HashMap<>();
        int currPage = totalPages;

        for (String value : map.values()) {
            result.put(currPage, value);
            currPage--;
        }

        return result;
    }

    private Map<Integer, Dimension> invertDimensionsMap (Map<Integer, Dimension> map) {
        Map<Integer, Dimension> result = new HashMap<>();
        int currPage = totalPages;

        for (Dimension dimension : map.values()) {
            result.put(currPage, dimension);
            currPage--;
        }

        return result;
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
                spreads.put(index, fillSpread(spread, index, true));

                isFirst = false;
                spread = null;
            } else {
                if (spread == null) {
                    spread = new Spread();
                    spreads.put(index, fillSpread(spread, index, true));
                } else {
                    spreads.put(index, fillSpread(spread, index, false));
                    spread = null;
                }
            }
        }

        return spreads;
    }

    private Spread fillSpread(Spread spread, Integer index, boolean isEmpty) {
        if (isEmpty) {
            fillVerso(spread, index);
        }
        if (!isEmpty) {
            fillRecto(spread, index);
        }

        return spread;
    }

    private void fillRecto (Spread spread, Integer index) {
        spread.setRecto(pages.get(index));
        spread.setRectoPageNumber(index);
        spread.setRectoSize(dimensions.get(index));
        spread.setRectoPreview(previews.get(index));
    }

    private void fillVerso(Spread spread, Integer index) {
        spread.setVerso(pages.get(index));
        spread.setVersoPageNumber(index);
        spread.setVersoSize(dimensions.get(index));
        spread.setVersoPreview(previews.get(index));
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
            logger.error(e.getMessage());
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
        long startTime = System.currentTimeMillis();
        logger.info("Constructing preview images");

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
                logger.error(e.getMessage());
            }
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        logger.info("Constructed preview images in {} milliseconds", elapsedTime);
    }

    private String generatePreviewImagePath(String path, Path folderPath) {
        if (path.isEmpty()) {
            return "";
        }
        if (folderPath == null) {
            return "";
        }

        String result = "";
        Path imagePath = Paths.get(path);

        try {
            String extension = FilenameUtils.getExtension(imagePath.getFileName().toString());
            if (!extension.isEmpty()) {
                extension = "." + extension;
            }

            String baseName = URLDecoder.decode(FilenameUtils.getBaseName(imagePath.getFileName().toString()), StandardCharsets.UTF_8.toString());

            Path previewImagePath = Files.createTempFile(folderPath, baseName, extension);
            previewImagePath.toFile().deleteOnExit();

            result = previewImagePath.toUri().toURL().toString();
        } catch (IOException e) {
            logger.error(e.getMessage());
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
            logger.error(e.getMessage());
        }

        return previewFolderPath;
    }
}
