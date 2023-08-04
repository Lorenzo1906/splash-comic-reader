package library.service

interface LibraryService {
    fun indexFolder(path: String): String
    fun retrieveAllSeries(path: String): String
    fun retrieveSeriesContains(path: String): String
    fun retrieveFileInfo(path: String): String
    fun search(path: String): String
}