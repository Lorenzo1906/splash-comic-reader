package reader.utils

import reader.model.Dimension

expect fun getPageSize(path: String): Dimension
expect fun generatePreviewImages(pages: Map<Int, String>?, tempFolderPath: String): MutableMap<Int, String>