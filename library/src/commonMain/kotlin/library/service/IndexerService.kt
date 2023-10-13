package library.service

interface IndexerService {
    fun indexFolder(path: String)
    fun retrieveAllSeries(): MutableList<String>
}