package com.mythicalcreaturesoftware.splash.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ImageHelper {

    /**
     * Resize an image to a absolute width and height (the image may not be proportional). Both paths should be coming as an encode url, they will be decode during the execution
     * @param inputImagePath Path of the original image
     * @param outputImagePath Path to save the resized image
     * @param scaledWidth absolute width in pixels
     * @param scaledHeight absolute height in pixels
     * @throws IOException IOException
     */
    public static void resize(String inputImagePath, String outputImagePath, int scaledWidth, int scaledHeight) throws IOException {
        inputImagePath = URLDecoder.decode(inputImagePath, StandardCharsets.UTF_8.toString());
        outputImagePath = URLDecoder.decode(outputImagePath, StandardCharsets.UTF_8.toString());

        // reads input image
        File inputFile = new File(cleanPath(inputImagePath));
        BufferedImage inputImage = ImageIO.read(inputFile);

        // creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());

        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        // extracts extension of output file
        String formatName = outputImagePath.substring(outputImagePath.lastIndexOf(".") + 1);

        // writes to output file
        File outputFile = new File(cleanPath(outputImagePath));
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }
        ImageIO.write(outputImage, formatName, outputFile);
    }

    /**
     * Resize an image by a percentage of original size (proportional). Both paths should be coming as an encode url, they will be decode during the execution
     * @param inputImagePath Path of the original image
     * @param outputImagePath Path to save the resized image
     * @param percent a double number specifies percentage of the output image. Eg: 10
     * over the input image.
     * @throws IOException IOException
     */
    public static void resize(String inputImagePath, String outputImagePath, double percent) throws IOException {
        if (inputImagePath == null || inputImagePath.equals("")) {
            return;
        }
        if (outputImagePath == null || outputImagePath.equals("")) {
            return;
        }

        inputImagePath = URLDecoder.decode(inputImagePath, StandardCharsets.UTF_8.toString());
        outputImagePath = URLDecoder.decode(outputImagePath, StandardCharsets.UTF_8.toString());

        File file = new File(cleanPath(inputImagePath));

        BufferedImage inputImage = ImageIO.read(file);

        int scaledWidth = (int) MathHelper.percentageValue(percent, inputImage.getWidth());
        int scaledHeight = (int) MathHelper.percentageValue(percent, inputImage.getHeight());

        resize(inputImagePath, outputImagePath, scaledWidth, scaledHeight);
    }

    /**
     * Remove the leading "file:" from the indicated path
     * @param path path to clean
     * @return the cleaned path
     */
    public static String cleanPath(String path) {
        return path.replace("file:", "");
    }
}
