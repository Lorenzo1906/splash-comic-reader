package com.mythicalcreaturesoftware.splash.filereader.impl;

import com.mythicalcreaturesoftware.splash.filereader.FileReader;
import com.mythicalcreaturesoftware.splash.filereader.FileReaderType;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
        logger.info("Constructing cbz file");

        ZipInputStream zipIs = null;
        try {
            List<String> paths = new ArrayList<>();

            Path mainDirectory = Files.createTempDirectory(FilenameUtils.getBaseName(filePath));
            mainDirectory.toFile().deleteOnExit();
            logger.info("Temp folder created at " + mainDirectory.toUri());

            try (FileInputStream fis = new FileInputStream(filePath);
                BufferedInputStream bis = new BufferedInputStream(fis);
                ZipInputStream stream = new ZipInputStream(bis)) {

                ZipEntry entry;
                while ((entry = stream.getNextEntry()) != null) {
                    Path filePath = processFileEntry(mainDirectory, entry, stream);
                    paths.add(filePath.toUri().toURL().toString());
                }
            }

            fileEntries = new String[paths.size()];
            fileEntries = paths.toArray(fileEntries);
            totalPages = paths.size();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                zipIs.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
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
