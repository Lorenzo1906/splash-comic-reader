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

class ImageResizeTask (var inputImagePath: String?,
                       var outputImagePath: String?,
                       var percent: Double) : Runnable {

    private val logger = KotlinLogging.logger {}

    override fun run() {
        try {
            resize(inputImagePath, outputImagePath, percent)
        } catch (e: IOException) {
            logger.error("Error starting thread", e)
        }
    }

    /**
     * Resize an image to a absolute width and height (the image may not be proportional). Both paths should be coming as an encode url, they will be decode during the execution
     * @param inputImagePath Path of the original image
     * @param outputImagePath Path to save the resized image
     * @param scaledWidth absolute width in pixels
     * @param scaledHeight absolute height in pixels
     * @throws IOException IOException
     */
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

    /**
     * Resize an image by a percentage of original size (proportional). Both paths should be coming as an encode url, they will be decode during the execution
     * @param inputImagePath Path of the original image
     * @param outputImagePath Path to save the resized image
     * @param percent a double number specifies percentage of the output image. Eg: 10
     * over the input image.
     * @throws IOException IOException
     */
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