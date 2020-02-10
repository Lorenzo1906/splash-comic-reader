package com.mythicalcreaturesoftware.splash.filereader.impl;

import com.mythicalcreaturesoftware.splash.filereader.FileReader;
import com.mythicalcreaturesoftware.splash.filereader.FileReaderType;
import de.innosystec.unrar.Archive;
import de.innosystec.unrar.exception.RarException;
import de.innosystec.unrar.rarfile.FileHeader;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CbrFileReader extends FileReader {

    private static Logger logger = LoggerFactory.getLogger(CbrFileReader.class);

    public CbrFileReader(String path) {
        super(FileReaderType.CBR, path);
    }

    @Override
    protected void construct() {
        long startTime = System.currentTimeMillis();
        logger.info("Constructing cbr file");

        try {
            tempFolderPath = Files.createTempDirectory(FilenameUtils.getBaseName(filePath));
            tempFolderPath.toFile().deleteOnExit();
            logger.info("Temp folder created at " + tempFolderPath.toUri());

            File local = new File(filePath);
            Archive archive = new Archive(local);
            List<FileHeader> list = sortFileHeaderList(archive.getFileHeaders());

            int pageIndex = 1;
            pages = new HashMap<>();
            dimensions = new HashMap<>();

            for (FileHeader header : list) {
                pageIndex = processFileHeader(header, archive, pageIndex);
            }
        } catch (RarException | IOException e) {
            logger.error(e.getMessage());
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        logger.info("File loaded in {} milliseconds", elapsedTime);
    }

    private List<FileHeader> sortFileHeaderList(List<FileHeader> list) {
        return list.stream()
                .sorted(Comparator.comparing(FileHeader::getFileNameString))
                .collect(Collectors.toList());
    }

    private int processFileHeader (FileHeader header, Archive archive, int pageIndex) throws MalformedURLException, FileNotFoundException, RarException {
        if (!header.isDirectory()) {
            Path filePath = processFileEntry(tempFolderPath, header);
            archive.extractFile(header, new FileOutputStream(filePath.toFile()));

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

    private Path processFileEntry (Path directory, FileHeader entry) {
        String filename = FilenameUtils.getBaseName(entry.getFileNameString()) + "." + FilenameUtils.getExtension(entry.getFileNameString());

        Path filePath = directory.resolve(filename);
        filePath.toFile().deleteOnExit();

        return filePath;
    }
}
