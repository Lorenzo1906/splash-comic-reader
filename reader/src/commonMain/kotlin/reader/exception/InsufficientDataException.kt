package reader.exception

/**
 * Exception used when FileReader does not have enough data to create the file correctly
 */
class InsufficientDataException (message: String): Exception(message)