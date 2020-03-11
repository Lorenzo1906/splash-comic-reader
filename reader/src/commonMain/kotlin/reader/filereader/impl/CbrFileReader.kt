package reader.filereader.impl

import reader.filereader.FileReader

/**
 * Implementation of the FileReader for files with extension .cbr
 */
expect class CbrFileReader(filePath: String): FileReader