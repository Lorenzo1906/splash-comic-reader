package com.mythicalcreaturesoftware.splash.filereader.impl;

import com.mythicalcreaturesoftware.splash.filereader.FileReader;
import com.mythicalcreaturesoftware.splash.filereader.FileReaderType;
import com.mythicalcreaturesoftware.splash.model.Spread;
import de.innosystec.unrar.Archive;
import de.innosystec.unrar.exception.RarException;
import de.innosystec.unrar.rarfile.FileHeader;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CbrFileReader extends FileReader {

    private static Logger logger = LoggerFactory.getLogger(CbrFileReader.class);

    public CbrFileReader(String path) {
        super(FileReaderType.CBR, path);
        construct();
    }

    @Override
    protected void construct() {
        long startTime = System.currentTimeMillis();
        logger.info("Constructing cbr file");

        try {
            Map<Integer, Spread> spreads;

            Path mainDirectory = Files.createTempDirectory(FilenameUtils.getBaseName(filePath));
            mainDirectory.toFile().deleteOnExit();
            logger.info("Temp folder created at " + mainDirectory.toUri());

            File local = new File(filePath);
            Archive archive = new Archive(local);
            List<FileHeader> list = archive.getFileHeaders();

            int pageIndex = 1;
            Map<Integer, String> pages = new HashMap<>();
            Map<Integer, Dimension> dimensions = new HashMap<>();

            for (FileHeader header : list) {

                if (!header.isDirectory()) {
                    Path filePath = processFileEntry(mainDirectory, header);
                    archive.extractFile(header, new FileOutputStream(filePath.toFile()));

                    String url = filePath.toUri().toURL().toString();
                    Dimension dimension = getPageSize(filePath);

                    pages.put(pageIndex, url);
                    dimensions.put(pageIndex, dimension);

                    totalPages++;
                    pageIndex++;
                }
            }

            spreads = groupPages(pages, dimensions);
            fileEntries = spreads;
        } catch (RarException | IOException e) {
            logger.error(e.getMessage());
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        logger.info("File loaded in {} milliseconds", elapsedTime);
    }

    private Path processFileEntry (Path directory, FileHeader entry) {
        Path filePath = directory.resolve(entry.getFileNameString());
        filePath.toFile().deleteOnExit();

        return filePath;
    }
}
