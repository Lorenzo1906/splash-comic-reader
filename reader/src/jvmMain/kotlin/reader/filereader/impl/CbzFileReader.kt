package reader.filereader.impl

import mu.KotlinLogging
import org.apache.commons.io.FilenameUtils
import reader.filereader.FileReader
import reader.model.Dimension
import reader.utils.getPageSize
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * Actual implementation of CbzFileReader for te JVM. Reads the cbz file. Creates a temp directory and put the files
 * in that folder. After that initialize pages and dimensions maps to store the values
 */
actual class CbzFileReader actual constructor(filePath: String) : FileReader(filePath) {
    private val logger = KotlinLogging.logger {}

    /**
     * Reads the cbz and initialize the maps
     */
    override fun construct() {
        val startTime = System.currentTimeMillis()
        logger.info ("Constructing cbz file")

        val zipIs:ZipInputStream? = null
        try {
            processFileEntries()
        } catch (e: IOException) {
            logger.error(e) { e.message }
            throw IOException("Error while trying to open file")
        } finally {
            zipIs?.close()
        }

        val stopTime = System.currentTimeMillis()
        val elapsedTime = stopTime - startTime
        logger.info("File loaded in {} milliseconds", elapsedTime)
    }

    private fun processFileEntries() {
        val tempFolder = Files.createTempDirectory(FilenameUtils.getBaseName(filePath))
        tempFolderPath = tempFolder.toUri().toString()
        tempFolder.toFile().deleteOnExit()
        logger.info("Temp folder created at " + tempFolder.toUri())

        val fis = FileInputStream(filePath)
        val bis = BufferedInputStream(fis)
        val stream = ZipInputStream(bis)

        var pageIndex = 1
        pages = mutableMapOf()
        dimensions = mutableMapOf()

        do {
            val entry:ZipEntry? = stream.nextEntry ?: break
            val result = checkProcessEntry(tempFolder, entry, stream, pageIndex)
            if (result) {
                pageIndex++
            }
        } while (true)
    }

    private fun checkProcessEntry (directory: Path, entry: ZipEntry?, stream: ZipInputStream, pageIndex: Int ): Boolean {
        var result = false
        if (entry != null && !entry.isDirectory) {
            val filePath: Path = processFileEntry(directory, entry, stream)
            val url: String  = filePath.toUri().toURL().toString()
            val dimension: Dimension = getPageSize(filePath.toUri().toString())

            if (dimension.height > 0 && dimension.width > 0) {
                pages[pageIndex] = url
                dimensions[pageIndex] = dimension

                totalPages++
                result = true
            }
        }

        return result
    }

    private fun processFileEntry (directory: Path, entry: ZipEntry, stream: ZipInputStream): Path {
        val filename = FilenameUtils.getBaseName(entry.name) + "." + FilenameUtils.getExtension(entry.name)

        val filePath: Path = directory.resolve(filename)

        Files.copy(stream, filePath)
        filePath.toFile().deleteOnExit()

        return filePath
    }
}