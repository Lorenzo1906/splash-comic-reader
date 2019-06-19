package com.mythicalcreaturesoftware.splash.filereader.impl;

import com.mythicalcreaturesoftware.splash.filereader.FileReader;
import com.mythicalcreaturesoftware.splash.filereader.FileReaderType;
import com.mythicalcreaturesoftware.splash.model.Spread;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        logger.info("Constructing cbz file");

        ZipInputStream zipIs = null;
        try {
            Map<Integer, Spread> spreads = new HashMap<>();

            Path mainDirectory = Files.createTempDirectory(FilenameUtils.getBaseName(filePath));
            mainDirectory.toFile().deleteOnExit();
            logger.info("Temp folder created at " + mainDirectory.toUri());

            try (FileInputStream fis = new FileInputStream(filePath);
                BufferedInputStream bis = new BufferedInputStream(fis);
                ZipInputStream stream = new ZipInputStream(bis)) {

                int pageIndex = 1;
                boolean isFirst = true;
                Spread spread = null;
                ZipEntry entry;
                while ((entry = stream.getNextEntry()) != null) {
                    Path filePath = processFileEntry(mainDirectory, entry, stream);
                    String url = filePath.toUri().toURL().toString();

                    //The first one is always alone on the spread
                    if (isFirst) {
                        spread = new Spread();
                        spread.setRecto(url);
                        spread.setRectoPageNumber(pageIndex);

                        spreads.put(pageIndex, spread);

                        isFirst = false;
                        spread = null;
                    } else {
                        if (spread == null) {
                            spread = new Spread();
                            spread.setVerso(url);
                            spread.setVersoPageNumber(pageIndex);

                            spreads.put(pageIndex, spread);
                        } else {
                            spread.setRecto(url);
                            spread.setRectoPageNumber(pageIndex);

                            spreads.put(pageIndex, spread);
                            spread = null;
                        }
                    }

                    totalPages++;
                    pageIndex++;
                }
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
