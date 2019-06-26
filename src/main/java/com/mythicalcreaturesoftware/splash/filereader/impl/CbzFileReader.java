package com.mythicalcreaturesoftware.splash.filereader.impl;

import com.mythicalcreaturesoftware.splash.filereader.FileReader;
import com.mythicalcreaturesoftware.splash.filereader.FileReaderType;
import com.mythicalcreaturesoftware.splash.model.Spread;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CbzFileReader extends FileReader {

    private static Logger logger = LoggerFactory.getLogger(CbzFileReader.class);

    public CbzFileReader(String path) {
        super(FileReaderType.CBZ, path);
        construct();
    }

    @Override
    protected void construct() {
        long startTime = System.currentTimeMillis();
        logger.info("Constructing cbz file");

        ZipInputStream zipIs = null;
        try {
            Map<Integer, Spread> spreads;

            Path mainDirectory = Files.createTempDirectory(FilenameUtils.getBaseName(filePath));
            mainDirectory.toFile().deleteOnExit();
            logger.info("Temp folder created at " + mainDirectory.toUri());

            try (FileInputStream fis = new FileInputStream(filePath);
                BufferedInputStream bis = new BufferedInputStream(fis);
                ZipInputStream stream = new ZipInputStream(bis)) {

                int pageIndex = 1;
                ZipEntry entry;
                Map<Integer, String> pages = new HashMap<>();
                Map<Integer, Dimension> dimensions = new HashMap<>();

                while ((entry = stream.getNextEntry()) != null) {
                    if (entry.isDirectory()) {
                        Files.createDirectory(mainDirectory.resolve(entry.getName()));
                    } else {

                        Path filePath = processFileEntry(mainDirectory, entry, stream);
                        String url = filePath.toUri().toURL().toString();
                        Dimension dimension = getPageSize(filePath);

                        pages.put(pageIndex, url);
                        dimensions.put(pageIndex, dimension);

                        totalPages++;
                        pageIndex++;
                    }
                }

                spreads = groupPages(pages, dimensions);
            }

            fileEntries = spreads;
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

    private Path processFileEntry (Path directory, ZipEntry entry, ZipInputStream stream) throws IOException {
        byte[] buffer = new byte[2048];

        Path filePath = directory.resolve(entry.getName());
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
