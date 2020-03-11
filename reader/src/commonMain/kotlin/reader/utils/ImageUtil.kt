package reader.utils

import reader.model.Dimension

/**
 * Returns the [Dimension] of the file located on the given [path]
 */
expect fun getPageSize(path: String): Dimension

/**
 * Generate the previews images on the temp folder. Receives the [Map] with the paths of the files and the [String] with
 * the path of the temp folder and returns a new map with the paths of the previews
 */
expect fun generatePreviewImages(pages: Map<Int, String>?, tempFolderPath: String): MutableMap<Int, String>