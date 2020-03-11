package reader.filereader.impl

import de.innosystec.unrar.Archive
import de.innosystec.unrar.rarfile.FileHeader
import mu.KotlinLogging
import org.apache.commons.io.FilenameUtils
import reader.filereader.FileReader
import reader.utils.cleanPath
import reader.utils.getPageSize
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

/**
 * Actual implementation of CbrFileReader for te JVM. Reads the cbr file. Creates a temp directory and put the files
 * in that folder. After that initialize pages and dimensions maps to store the values
 */
actual class CbrFileReader actual constructor(filePath: String) : FileReader(filePath) {
    private val logger = KotlinLogging.logger {}

    /**
     * Reads the cbr and initialize the maps
     */
    override fun construct() {
        val startTime = System.currentTimeMillis()
        logger.info("Constructing cbr file")

        try {
            val tempFolder = Files.createTempDirectory(FilenameUtils.getBaseName(filePath))
            tempFolderPath = tempFolder.toUri().toString()
            tempFolder.toFile().deleteOnExit()
            logger.info("Temp folder created at " + tempFolder.toUri())

            val local = File(filePath)
            val archive = Archive(local)
            val list = sortFileHeaderList(archive.fileHeaders)

            var pageIndex = 1
            pages = mutableMapOf()
            dimensions = mutableMapOf()

            for (header: FileHeader in list) {
                val result = processFileHeader(tempFolder, header, archive, pageIndex)
                if (result) {
                    pageIndex++
                }
            }
        } catch (e: IOException) {
            logger.error(e) { e.message }
        }

        val stopTime = System.currentTimeMillis()
        val elapsedTime = stopTime - startTime
        logger.info("File loaded in {} milliseconds", elapsedTime)
    }

    /**
     * Sort the filenames alphabetically
     */
    private fun sortFileHeaderList(list: List<FileHeader>): List<FileHeader>  {
        return list.stream()
                .sorted(Comparator.comparing(FileHeader::getFileNameString))
                .collect(Collectors.toList())
    }

    /**
     * Reads the file from the archive and calculates the dimesions and store the info on the maps
     */
    private fun processFileHeader (directory: Path, header: FileHeader, archive: Archive, pageIndex: Int ): Boolean {
        var result = false

        if (!header.isDirectory) {
            val filePath = processFileEntry(directory, header)
            archive.extractFile(header, FileOutputStream(filePath.toFile()))

            val url = filePath.toUri().toURL().toString()
            val dimension = getPageSize(filePath.toUri().toString())

            if (dimension.height > 0 && dimension.width > 0) {
                pages[pageIndex] = url
                dimensions[pageIndex] = dimension

                totalPages++
                result = true
            }
        }

        return result
    }

    private fun processFileEntry (directory: Path, entry: FileHeader): Path {
        val filename = FilenameUtils.getBaseName(entry.fileNameString) + "." + FilenameUtils.getExtension(entry.fileNameString)

        val filePath: Path = directory.resolve(filename)
        filePath.toFile().deleteOnExit()
        return filePath
    }
}