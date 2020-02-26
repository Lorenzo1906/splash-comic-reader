package reader.filereader

import reader.exception.UnsupportedFileTypeException
import reader.filereader.impl.CbrFileReader
import reader.filereader.impl.CbzFileReader

fun buildFileReader(type: FileReaderType, path: String ): FileReader {
    val reader = when (type) {
        FileReaderType.CBR -> CbrFileReader(path)
        FileReaderType.CBZ -> CbzFileReader(path)
        else -> {
            throw UnsupportedFileTypeException("Unsupported File Type: $type")
        }
    }

    reader.initialize()

    return reader
}
