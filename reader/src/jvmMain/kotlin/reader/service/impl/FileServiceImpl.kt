package reader.service.impl

import org.apache.commons.io.FilenameUtils
import reader.filereader.FileReaderType
import reader.service.FileService

actual object FileServiceImpl : FileService() {
    override fun getFilenameFromPath(path: String): String {
        return FilenameUtils.getName(path)
    }

    override fun getFileReaderTypeFromPath(path: String): FileReaderType? {
        val extension = FilenameUtils.getExtension(path).toLowerCase()
        var type: FileReaderType? = null

        when (extension) {
            "cbr" -> type = FileReaderType.CBR
            "cbz" -> type = FileReaderType.CBZ
        }

        return type;
    }
}