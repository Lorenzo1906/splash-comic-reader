package library.service.impl

import library.service.LibraryService
import library.utils.FileUtils

actual class LibraryServiceImpl : LibraryService {
    override fun indexFolder(path: String): String {
        return "test"
    }

    override fun retrieveAllSeries(path: String): String {
        TODO("Not yet implemented")
    }

    override fun retrieveSeriesContains(path: String): String {
        TODO("Not yet implemented")
    }

    override fun retrieveFileInfo(path: String): String {
        TODO("Not yet implemented")
    }

    override fun search(path: String): String {
        TODO("Not yet implemented")
    }

}