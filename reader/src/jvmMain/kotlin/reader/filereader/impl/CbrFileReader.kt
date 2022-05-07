package reader.filereader.impl

import de.innosystec.unrar.Archive
import de.innosystec.unrar.rarfile.FileHeader
import io.github.aakira.napier.Napier
import org.apache.commons.io.FilenameUtils
import reader.filereader.FileReader
import reader.utils.cleanPathBeginning
import reader.utils.getPageSize
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

/**
 * Actual implementation of CbrFileReader for te JVM. Reads the cbr file. Creates a temp directory and put the files
 * in that folder. After that initialize pages and dimensions maps to store the values
 */
actual class CbrFileReader actual constructor(filePath: String) : FileReader(filePath) {
    /**
     * Reads the cbr and initialize the maps
     */
    override fun construct() {
        val startTime = System.currentTimeMillis()
        Napier.i("Constructing cbr file")

        try {
            val tempFolder = Files.createTempDirectory(FilenameUtils.getBaseName(filePath))
            tempFolderPath = tempFolder.toUri().toString()
            tempFolder.toFile().deleteOnExit()
            Napier.i("Temp folder created at $tempFolder.toUri()")

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
            e.message?.let { Napier.e(it, e) }
            throw IOException("Error while trying to open file")
        }

        val stopTime = System.currentTimeMillis()
        val elapsedTime = stopTime - startTime
        Napier.i("File loaded in {} milliseconds $elapsedTime")
    }

    override fun deleteFiles() {
        try {
            for (filePreview: String in previews.values) {
                val cleanedPath = URLDecoder.decode(cleanPathBeginning(filePreview), StandardCharsets.UTF_8.toString())
                val filePath: Path = Paths.get(cleanedPath)
                filePath.toFile().delete()
            }
            for (file: String in pages.values) {
                val cleanedPath = URLDecoder.decode(cleanPathBeginning(file), StandardCharsets.UTF_8.toString())
                val filePath: Path = Paths.get(cleanedPath)
                filePath.toFile().delete()
            }
        } catch (e: IOException) {
            e.message?.let { Napier.e(it, e) }
            throw IOException("Error while trying to open file")
        }
    }

    private fun sortFileHeaderList(list: List<FileHeader>): List<FileHeader>  {
        return list.stream()
                .sorted(Comparator.comparing(FileHeader::getFileNameString))
                .collect(Collectors.toList())
    }

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