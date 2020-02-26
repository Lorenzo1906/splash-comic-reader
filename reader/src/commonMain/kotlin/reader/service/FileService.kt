package reader.service

import mu.KotlinLogging
import reader.exception.UnsupportedFileTypeException
import reader.filereader.FileReader
import reader.filereader.FileReaderType
import reader.filereader.buildFileReader
import reader.model.Dimension

abstract class FileService {
    private val logger = KotlinLogging.logger {}

    private var fileReader: FileReader? = null

    fun unloadFile() {
        logger.info { "Unloading file" }
        fileReader = null
    }

    fun loadFile(path: String): String {
        logger.info { "Loading file" }

        try {
            fileReader = getFileReaderTypeFromPath(path)?.let { buildFileReader(it, path) }
        } catch (e: Exception) {
            logger.error(e) { e.message }
        }

        return getFilenameFromPath(path);
    }

    fun getCurrentRecto(): String {
        return getCurrentPageValue(false)
    }

    fun getCurrentVerso(): String {
        return getCurrentPageValue(true)
    }

    private fun getCurrentPageValue(verso: Boolean): String {
        var result = ""

        if (fileReader != null) {

            val spread = fileReader!!.getCurrentPath()

            if (spread?.verso != null) {
                result = if (verso) spread.verso else spread.recto
            }
        }

        return result
    }

    fun getCurrentPage(): String {
        var result = ""

        if (fileReader != null) {
            result = getPageByPageNumber(fileReader!!.index)
        }

        return result
    }

    fun getCurrentPreviewByPageNumber(pageNumber: Int): String {
        return getPageValueByNumber(pageNumber, true)
    }

    fun getPageByPageNumber(pageNumber: Int): String {
        return getPageValueByNumber(pageNumber, false)
    }

    private fun getPageValueByNumber(pageNumber: Int, preview: Boolean): String {
        var result = ""

        if (fileReader != null) {
            val spread = fileReader!!.getPath(pageNumber)

            if (spread != null && spread.versoPageNumber == pageNumber) {
                result = if (preview) spread.versoPreview else spread.verso
            }

            if (spread != null && spread.rectoPageNumber == pageNumber) {
                result = if (preview) spread.rectoPreview else spread.recto
            }
        }

        return result
    }

    fun getCurrentRectoSize(): Dimension {
        return getSize(false)
    }

    fun getCurrentVersoSize(): Dimension {
        return getSize(true)
    }

    private fun getSize(verso: Boolean): Dimension {
        var result: Dimension? = null

        if (fileReader != null) {

            val spread = fileReader!!.getCurrentPath()

            if (spread?.verso != null) {
                result = if (verso) spread.versoSize else spread.rectoSize
            }
        }

        if (result == null) {
            result = Dimension(1, 1)
        }

        return result
    }

    fun getCurrentPageSize(): Dimension {
        var result: Dimension? = null

        if (fileReader != null) {
            val spread = fileReader!!.getPath(fileReader!!.index)

            if (spread != null && spread.versoPageNumber == fileReader!!.index) {
                result = spread.versoSize
            }

            if (spread != null && spread.rectoPageNumber == fileReader!!.index) {
                result = spread.rectoSize
            }
        }

        if (result == null) {
            result = Dimension(1, 1)
        }

        return result
    }

    fun getTotalPages(): Int {
        if (fileReader == null) {
            return 1
        }

        return fileReader!!.totalPages
    }

    fun getCurrentPageNumber(): Int {
        if (fileReader == null) {
            return 1
        }

        return fileReader!!.index
    }

    fun canChangeToNextPage(isTwoPage: Boolean): Boolean {
        if (fileReader == null) {
            return false
        }

        var result = true

        if (isTwoPage) {
            val spread = fileReader!!.getPath(fileReader!!.index)

            if (spread != null && spread.rectoPageNumber == fileReader!!.totalPages) {
                result = false
            }

            if (spread != null && spread.versoPageNumber == fileReader!!.totalPages) {
                result = false
            }
        } else {
            if (fileReader!!.index >= fileReader!!.totalPages) {
                result = false
            }
        }

        return result
    }

    fun isReverseReadingDirection(): Boolean {
        var result = false
        if (fileReader != null) {
            result = fileReader!!.isReverseReadingDirection
        }
        return result
    }

    fun  changeReadingDirection(): Boolean {
        fileReader?.changeReadingDirection()

        return isReverseReadingDirection()
    }

    fun setCurrentPage(pageNumber: Int) {
        if (fileReader == null) {
            return
        }

        fileReader!!.index =pageNumber
    }

    fun updateNextPage(isTwoPage: Boolean) {
        if (fileReader == null){
            return
        }

        if (!isTwoPage && fileReader!!.index + 1 <= fileReader!!.totalPages) {

            fileReader!!.index = fileReader!!.index + 1
        } else {
            updateSpreadPage(1)
        }
    }

    fun updatePreviousPage(isTwoPage: Boolean) {
        if (fileReader == null){
            return
        }

        if (!isTwoPage && fileReader!!.index - 1 == 1) {

            fileReader!!.index = fileReader!!.index - 1
        } else {
            updateSpreadPage(-1)
        }
    }

    private fun updateSpreadPage(sign: Int) {
        val currentSpread = fileReader?.getCurrentPath();
        val nextSpread = fileReader?.getPath(fileReader!!.index + sign);

        if (currentSpread != null) {
            if (currentSpread == nextSpread && (fileReader!!.index) + 2 <= fileReader!!.totalPages) {
                fileReader!!.index = fileReader!!.index + (2 * sign)
            } else if ((fileReader!!.index) + sign <= fileReader!!.totalPages) {
                fileReader!!.index = fileReader!!.index + sign
            }
        }
    }

    abstract fun getFilenameFromPath(path: String): String

    abstract fun getFileReaderTypeFromPath (path: String): FileReaderType?
}