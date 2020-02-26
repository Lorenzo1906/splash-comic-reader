package reader.filereader

import mu.KotlinLogging
import reader.exception.InsufficientDataException
import reader.model.Dimension
import reader.model.Spread
import reader.utils.generatePreviewImages

/**
 * This class opens a file, group the pages in different spreads, and manage the number pages, dimensions and current page for the file
 */
abstract class FileReader (val filePath: String) {
    private val logger = KotlinLogging.logger {}

    private lateinit var fileEntries: MutableMap<Int, Spread>
    private lateinit var previews: MutableMap<Int, String>
    protected lateinit var pages: MutableMap<Int, String>
    protected lateinit var dimensions: MutableMap<Int, Dimension>
    protected lateinit var tempFolderPath: String
    var totalPages: Int = 0
    var isReverseReadingDirection: Boolean = false
    var index: Int = 1

    fun initialize() {
        try {
            construct()
            previews = generatePreviewImages(pages, tempFolderPath)
            fileEntries = groupPages()
        } catch (e: InsufficientDataException) {
            logger.error(e) { e.message }
        }
    }

    abstract fun construct()

    fun getCurrentPath(): Spread? {
        return fileEntries[index]
    }

    fun getPath(pageNumber: Int): Spread? {
        return fileEntries[pageNumber]
    }

    fun changeReadingDirection () {
        isReverseReadingDirection = !isReverseReadingDirection
        val currPage: String = getCurrentPage()

        fileEntries = invertPagesOrder()
        index = calculateNewIndex(currPage)
    }

    private fun getCurrentPage (): String {
        var result = ""
        val currSpread = getCurrentPath()

        if (currSpread?.recto != null && currSpread.rectoPageNumber == index){
            result = currSpread.recto
        }

        if (currSpread?.verso != null && currSpread.versoPageNumber == index){
            result = currSpread.verso
        }

        return result
    }

    private fun calculateNewIndex(currPage: String): Int {
        var result = 0

        for (spread: Spread in fileEntries.values) {
            if (spread.verso == currPage) {
                result = spread.versoPageNumber
                break
            }
            if (spread.recto == currPage) {
                result = spread.rectoPageNumber
                break
            }
        }

        return result
    }

    private fun invertPagesOrder(): MutableMap<Int, Spread> {
        pages = invertMap(pages)
        previews = invertMap(previews)
        dimensions = invertDimensionsMap(dimensions)

        var result: MutableMap<Int, Spread> = mutableMapOf()
        try {
            result = groupPages()
        } catch (e: InsufficientDataException) {
            logger.error(e) { e.message }
        }

        return result
    }

    private fun invertMap(map: MutableMap<Int, String>): MutableMap<Int, String> {
        val result = HashMap<Int, String>()
        var currPage:Int = totalPages

        for (value: String in map.values) {
            result[currPage] = value
            currPage--
        }

        return result
    }

    private fun invertDimensionsMap(map: MutableMap<Int, Dimension>): MutableMap<Int, Dimension> {
        val result = HashMap<Int, Dimension>()
        var currPage:Int = totalPages

        for (value: Dimension in map.values) {
            result[currPage] = value
            currPage--
        }

        return result
    }

    private fun groupPages(): MutableMap<Int, Spread> {
        val spreads = mutableMapOf<Int, Spread>()

        var isFirst = true
        var spread: Spread? = null

        for (index: Int in pages.keys) {
            //The first one is always alone on the spread
            if (isFirst || shouldBeAlone(dimensions[index])) {
                spread = Spread()
                spreads[index] = fillSpread(spread, index, true)

                isFirst = false
                spread = null
            } else {
                if (spread == null) {
                    spread = Spread()
                    spreads[index] = fillSpread(spread, index, true)
                } else {
                    spreads[index] = fillSpread(spread, index, false)
                    spread = null
                }
            }
        }

        return spreads
    }

    private fun fillSpread(spread: Spread, index: Int, isEmpty: Boolean): Spread {
        if (isEmpty) {
            fillVerso(spread, index)
        }
        if (!isEmpty) {
            fillRecto(spread, index)
        }

        return spread
    }

    private fun fillRecto(spread: Spread, index: Int) {
        spread.recto = pages[index] ?: ""
        spread.rectoPageNumber = index
        spread.rectoSize = dimensions[index]
        spread.rectoPreview = previews[index] ?: ""
    }

    private fun fillVerso(spread: Spread, index: Int) {
        spread.verso = pages[index] ?: ""
        spread.versoPageNumber = index
        spread.versoSize = dimensions[index]
        spread.versoPreview = previews[index] ?: ""
    }

    private fun shouldBeAlone(dimension: Dimension?): Boolean {
        if (dimension == null) {
            throw InsufficientDataException("No dimensions info")
        }

        var result = false
        if (dimension.width > dimension.height) {
            result = true
        }
        return result
    }
}