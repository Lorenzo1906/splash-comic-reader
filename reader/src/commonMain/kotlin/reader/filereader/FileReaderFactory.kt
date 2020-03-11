package reader.filereader

import reader.exception.UnsupportedFileTypeException
import reader.filereader.impl.CbrFileReader
import reader.filereader.impl.CbzFileReader

/**
 * Receives a [FileReaderType] with name [type] and a [String] with name [path], containing the path of a file.
 * Returns the respective implementation of [FileReader] for the given type
 */
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
