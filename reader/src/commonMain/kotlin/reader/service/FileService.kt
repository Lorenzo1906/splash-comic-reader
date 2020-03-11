package reader.service

import mu.KotlinLogging
import reader.filereader.FileReader
import reader.filereader.FileReaderType
import reader.filereader.buildFileReader
import reader.model.Dimension

/**
 * Service class to serve as interface between the graphical part and the reader
 */
abstract class FileService {
    private val logger = KotlinLogging.logger {}

    private var fileReader: FileReader? = null

    /**
     * Cleans the current [fileReader]
     */
    fun unloadFile() {
        logger.info { "Unloading file" }
        fileReader = null
    }

    /**
     * Loads a new [FileReader] using the given [path]. Returns the filename of the archive
     */
    fun loadFile(path: String): String {
        logger.info { "Loading file" }

        try {
            fileReader = getFileReaderTypeFromPath(path)?.let { buildFileReader(it, path) }
        } catch (e: Exception) {
            logger.error(e) { e.message }
        }

        return getFilenameFromPath(path)
    }

    /**
     * Returns the current recto from the [fileReader]
     */
    fun getCurrentRecto(): String {
        return getCurrentPageValue(false)
    }

    /**
     * Returns the current verso from the [fileReader]
     */
    fun getCurrentVerso(): String {
        return getCurrentPageValue(true)
    }

    /**
     * Returns a [String] with the path of the current page. If the value of [verso] is true it will returns a verso,
     * otherwise it will returns a recto
     */
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

    /**
     * Returns a [String] with the path of the current page
     */
    fun getCurrentPage(): String {
        var result = ""

        if (fileReader != null) {
            result = getPageByPageNumber(fileReader!!.index)
        }

        return result
    }

    /**
     * Returns a [String] with the path of the preview with the given [pageNumber]
     */
    fun getCurrentPreviewByPageNumber(pageNumber: Int): String {
        return getPageValueByNumber(pageNumber, true)
    }

    /**
     * Returns a [String] with the path with the given [pageNumber]
     */
    private fun getPageByPageNumber(pageNumber: Int): String {
        return getPageValueByNumber(pageNumber, false)
    }

    /**
     * Returns a [String] with the path with the given [pageNumber], if the value of [preview] is true, it will returns
     * the preview, otherwise it will return the oage
     */
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

    /**
     * Returns the [Dimension] for the current recto
     */
    fun getCurrentRectoSize(): Dimension {
        return getSize(false)
    }

    /**
     * Returns the [Dimension] for the current verso
     */
    fun getCurrentVersoSize(): Dimension {
        return getSize(true)
    }

    /**
     * Returns the [Dimension] for the current page. If [verso] is true, it will return the verso, otherwise
     * it will return the recto
     */
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

    /**
     * Returns the [Dimension] for the current page.
     */
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

    /**
     * Returns a [Int] with the total pages
     */
    fun getTotalPages(): Int {
        if (fileReader == null) {
            return 1
        }

        return fileReader!!.totalPages
    }

    /**
     * Returns a [Int] with the current page number
     */
    fun getCurrentPageNumber(): Int {
        if (fileReader == null) {
            return 1
        }

        return fileReader!!.index
    }

    /**
     * Returns a [Boolean] if changing to the next page is possible. It will return false if the current index is
     * equals to the total pages. If [isTwoPage] it will take on consideration the recto and the verso.
     */
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

    /**
     * It will return a [Boolean] with the value set to true if the [fileReader] is using a reverse reading direction.
     * ie: the first page is on the right side
     */
    private fun isReverseReadingDirection(): Boolean {
        var result = false
        if (fileReader != null) {
            result = fileReader!!.isReverseReadingDirection
        }
        return result
    }

    /**
     * Changes the current reading direction. Returns true is reverse reading direction
     */
    fun  changeReadingDirection(): Boolean {
        fileReader?.changeReadingDirection()

        return isReverseReadingDirection()
    }

    /**
     * Sets the [fileReader] index to the given [pageNumber]
     */
    fun setCurrentPage(pageNumber: Int) {
        if (fileReader == null) {
            return
        }

        fileReader!!.index =pageNumber
    }

    /**
     * Moves the index to the next page. If [isTwoPage] it will take on consideration the verso and the recto
     */
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

    /**
     * Moves the index to the previous page. If [isTwoPage] it will take on consideration the verso and the recto
     */
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
        val currentSpread = fileReader?.getCurrentPath()
        val nextSpread = fileReader?.getPath(fileReader!!.index + sign)

        if (currentSpread != null) {
            if (currentSpread == nextSpread && (fileReader!!.index) + 2 <= fileReader!!.totalPages) {
                fileReader!!.index = fileReader!!.index + (2 * sign)
            } else if ((fileReader!!.index) + sign <= fileReader!!.totalPages) {
                fileReader!!.index = fileReader!!.index + sign
            }
        }
    }

    /**
     * Returns the file of the given [path]
     */
    abstract fun getFilenameFromPath(path: String): String

    /**
     * Returns of the [FileReaderType] of the given [path]
     */
    abstract fun getFileReaderTypeFromPath (path: String): FileReaderType?
}