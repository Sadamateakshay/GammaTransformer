package com.gamma.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

@RestController
@RequestMapping("/gamma")
public class GammaController {

    @PostMapping("/transform")
    public byte[] gammaTransform(
            @RequestParam("gamma") double gamma,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        // Check if the file is empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Please select an image file.");
        }

        // Read the input image
        BufferedImage inputImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));

        // Perform gamma transformation
        BufferedImage outputImage = applyGammaTransformation(inputImage, gamma);

        // Convert the transformed image to bytes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(outputImage, "png", outputStream);

        return outputStream.toByteArray();
    }

    private BufferedImage applyGammaTransformation(BufferedImage inputImage, double gamma) {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = inputImage.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xFF;
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;

                // Apply gamma transformation to the RGB values
                red = (int) (255 * Math.pow(red / 255.0, gamma));
                green = (int) (255 * Math.pow(green / 255.0, gamma));
                blue = (int) (255 * Math.pow(blue / 255.0, gamma));

                int newPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                outputImage.setRGB(x, y, newPixel);
            }
        }

        return outputImage;
    }
}
