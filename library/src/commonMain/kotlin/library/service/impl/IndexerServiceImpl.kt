package library.service.impl

import library.service.IndexerService

expect object IndexerServiceImpl : IndexerService {
    override fun indexFolder(path: String)
    override fun retrieveAllSeries(): MutableList<String>
}