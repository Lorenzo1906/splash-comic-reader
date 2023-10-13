package library.service.impl

import io.github.oshai.kotlinlogging.KotlinLogging
import library.models.FieldName
import library.models.FieldValue
import library.service.IndexerService
import org.apache.commons.io.FilenameUtils
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.ByteBuffersDirectory
import org.apache.lucene.store.Directory
import java.io.File
import java.util.*

actual object IndexerServiceImpl : IndexerService {

    private val logger = KotlinLogging.logger {  }

    private val index: Directory by lazy { ByteBuffersDirectory() }
    private val extensions = arrayOf("cbr", "cbz")

    private lateinit var rootPath: String
    private val analyzer = StandardAnalyzer()

    actual override fun retrieveAllSeries(): MutableList<String> {

        val documents = mutableListOf<String>()

        val query = QueryParser(FieldName.TYPE.value, analyzer).parse(FieldValue.SERIES.value)

        val indexReader = DirectoryReader.open(index)
        val searcher = IndexSearcher(indexReader)

        val topDocs = searcher.search(query, 10)
        topDocs.scoreDocs.forEach {
            documents.add(searcher.doc(it.doc).get(FieldName.NAME.value))
        }

        return documents
    }

    actual override fun indexFolder(path: String) {
        logger.info { "Indexing path: $path" }

        val config = IndexWriterConfig(analyzer)
        val writer = IndexWriter(index, config)
        rootPath = path

        indexFolderContent(path, writer)

        writer.close()
    }

    private fun indexFolderContent(path: String, writer: IndexWriter) {

        File(path).listFiles()?.forEach {
            if (it.isFile) {
                logger.debug { "Indexing file: ${it.name}" }
                indexFileDocument(it, writer)
            } else {
                logger.debug { "Indexing folder: ${it.name}" }
                indexFolderDocument(it, writer)
                indexFolderContent(it.path, writer)
            }
        }
    }

    private fun indexFolderDocument(file: File, writer: IndexWriter) {

        val document = Document()

        document.add(TextField(FieldName.NAME.value, file.name, Field.Store.YES))
        document.add(TextField(FieldName.PATH.value, file.path, Field.Store.YES))

        val parentIsRoot = file.parent.equals(rootPath)
        if (parentIsRoot) {

            document.add(TextField(FieldName.TYPE.value, FieldValue.SERIES.value, Field.Store.YES))
            writer.addDocument(document)
        } else {

            val parentOfParentIsRoot = file.parentFile.parent.equals(rootPath)
            if (parentOfParentIsRoot) {
                document.add(TextField(FieldName.TYPE.value, FieldValue.VOLUME.value, Field.Store.YES))
                document.add(TextField(FieldName.SERIES.value, file.parent, Field.Store.YES))
                writer.addDocument(document)
            }
        }
    }

    private fun indexFileDocument(file: File, writer: IndexWriter) {

        val extension = FilenameUtils.getExtension(file.path).lowercase(Locale.getDefault())

        if (extensions.contains(extension)) {
            val document = Document()

            document.add(TextField(FieldName.NAME.value, file.name, Field.Store.YES))
            document.add(TextField(FieldName.PATH.value, file.path, Field.Store.YES))
            document.add(TextField(FieldName.TYPE.value, FieldValue.FILE.value, Field.Store.YES))
            document.add(TextField(FieldName.EXTENSION.value, FieldValue.FILE.value, Field.Store.YES))

            writer.addDocument(document)
        }
    }
}