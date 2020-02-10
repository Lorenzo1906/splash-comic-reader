package com.mythicalcreaturesoftware.splash.filereader.impl;

import com.mythicalcreaturesoftware.splash.filereader.FileReader;
import com.mythicalcreaturesoftware.splash.filereader.FileReaderType;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CbzFileReader extends FileReader {

    private static Logger logger = LoggerFactory.getLogger(CbzFileReader.class);

    public CbzFileReader(String path) {
        super(FileReaderType.CBZ, path);
    }

    @Override
    protected void construct() {
        long startTime = System.currentTimeMillis();
        logger.info("Constructing cbz file");

        ZipInputStream zipIs = null;
        try {
            processFileEntries();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                if (zipIs != null) {
                    zipIs.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        logger.info("File loaded in {} milliseconds", elapsedTime);
    }

    private void processFileEntries() throws IOException {
        tempFolderPath =  Files.createTempDirectory(FilenameUtils.getBaseName(filePath));
        tempFolderPath.toFile().deleteOnExit();
        logger.info("Temp folder created at " + tempFolderPath.toUri());

        try (FileInputStream fis = new FileInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ZipInputStream stream = new ZipInputStream(bis)) {

            int pageIndex = 1;
            ZipEntry entry;
            pages = new HashMap<>();
            dimensions = new HashMap<>();

            while ((entry = stream.getNextEntry()) != null) {
                pageIndex = checkProcessEntry(entry, stream, pageIndex);
            }
        }
    }

    private int checkProcessEntry (ZipEntry entry, ZipInputStream stream, int pageIndex) throws IOException {
        if (!entry.isDirectory()) {
            Path filePath = processFileEntry(tempFolderPath, entry, stream);
            String url = filePath.toUri().toURL().toString();
            Dimension dimension = getPageSize(filePath);

            if (dimension.height > 0 && dimension.width > 0) {
                pages.put(pageIndex, url);
                dimensions.put(pageIndex, dimension);

                totalPages++;
                pageIndex++;
            }
        }

        return pageIndex;
    }

    private Path processFileEntry (Path directory, ZipEntry entry, ZipInputStream stream) throws IOException {
        byte[] buffer = new byte[2048];

        String filename = FilenameUtils.getBaseName(entry.getName()) + "." + FilenameUtils.getExtension(entry.getName());

        Path filePath = directory.resolve(filename);
        filePath.toFile().deleteOnExit();

        try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
             BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length)) {

            int len;
            while ((len = stream.read(buffer)) > 0) {
                bos.write(buffer, 0, len);
            }
        }

        return filePath;
    }
}
