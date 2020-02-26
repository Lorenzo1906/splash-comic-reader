package reader.model

/**
 * Logical representation of an spread. A spread is simply a set of pages (usually two) viewed together.
 * The parts of a spread are recto, and verso. Recto is the right hand page in a book; recto pages always have odd page numbering (e.g., pages 1, 3, 5).
 * Verso is the left hand page in a book; named for the reverse or back side of the page; verso pages always have even page numbering (e.g., pages 2, 4, 6)
 */
data class Spread(var recto: String = "",
                  var rectoPreview: String = "",
                  var rectoPageNumber: Int = 0,
                  var rectoSize: Dimension? = null,
                  var verso: String = "",
                  var versoPreview: String = "",
                  var versoPageNumber: Int = 0,
                  var versoSize: Dimension? = null) {
}