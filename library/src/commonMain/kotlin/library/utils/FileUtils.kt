package library.utils

import library.models.File
import java.nio.file.Files
import java.nio.file.Paths

class FileUtils {
    companion object {
        fun listAllFilesByPath(path: String) : List<File> {
            val result: List<File> = mutableListOf()

            Files.walk(Paths.get(path)).use { paths ->
                paths
                    .filter(Files::isRegularFile)
                    .forEach(System.out::println)
            }

            return result
        }
    }
}