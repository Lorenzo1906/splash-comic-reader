package reader.tasks

import mu.KotlinLogging
import reader.utils.cleanPath
import reader.utils.percentageValue
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.imageio.ImageIO

/**
 * Start a new process that receive a image path with an image [inputImagePath] and copy and reduce the image in another
 * path [outputImagePath] using the give [percent]
 */
class ImageResizeTask (private var inputImagePath: String?,
                       private var outputImagePath: String?,
                       private var percent: Double) : Runnable {

    private val logger = KotlinLogging.logger {}

    override fun run() {
        try {
            resize(inputImagePath, outputImagePath, percent)
        } catch (e: IOException) {
            logger.error("Error starting thread", e)
        }
    }

    private fun resize(inputImagePath: String, outputImagePath: String, scaledWidth: Int, scaledHeight: Int) {
        val cleanedInputImagePath = URLDecoder.decode(inputImagePath, StandardCharsets.UTF_8.toString())
        val cleanedOutputImagePath = URLDecoder.decode(outputImagePath, StandardCharsets.UTF_8.toString())

        // reads input image
        val inputFile = File(cleanPath(cleanedInputImagePath))
        val inputImage = ImageIO.read(inputFile)

        // creates output image
        val outputImage = BufferedImage(scaledWidth, scaledHeight, inputImage.type)

        // scales the input image to the output image
        val g2d = outputImage.createGraphics()
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null)
        g2d.dispose()

        // extracts extension of output file
        val formatName = cleanedOutputImagePath.substring(cleanedOutputImagePath.lastIndexOf(".") + 1)

        // writes to output file
        val outputFile = File(cleanPath(cleanedOutputImagePath))
        if (!outputFile.exists()) {
            outputFile.createNewFile()
        }
        ImageIO.write(outputImage, formatName, outputFile)
    }

    private fun resize(inputImagePath: String?, outputImagePath: String?, percent: Double) {
        if (inputImagePath == null || inputImagePath == "") {
            return
        }
        if (outputImagePath == null || outputImagePath == "") {
            return
        }

        val cleanedInputImagePath  = URLDecoder.decode(inputImagePath, StandardCharsets.UTF_8.toString())
        val cleanedOutputImagePath = URLDecoder.decode(outputImagePath, StandardCharsets.UTF_8.toString())

        val file = File(cleanPath(cleanedInputImagePath))

        val inputImage: BufferedImage?  = ImageIO.read(file)

        if (inputImage != null) {
            val scaledWidth: Int = percentageValue(percent, inputImage.width.toDouble()).toInt()
            val scaledHeight: Int = percentageValue(percent, inputImage.height.toDouble()).toInt()

            resize(cleanedInputImagePath, cleanedOutputImagePath, scaledWidth, scaledHeight)
        }
    }
}