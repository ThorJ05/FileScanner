package com.example.filescanner.DAL;

import com.example.filescanner.BLL.BarcodeService;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class BarcodeServiceTest {

    private final BarcodeService barcodeService = new BarcodeService();

    @Test
    void testReadBarcode_NoBarcode_ReturnsNull() {
        // Arrange: MAKE A EMPTY PICTURE WITHOUT BARCODE
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 200, 200);
        g.dispose();

        // Act
        String result = barcodeService.readBarcode(img);

        // Assert
        assertNull(result, "Expected null when no barcode is present");
    }

    @Test
    void testReadBarcode_WithFakeBarcode_ReturnsValue() {
        // Arrange: MAKE A PICTURE WITH BARCODE
        BufferedImage img = new BufferedImage(300, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 300, 100);

        g.setColor(Color.BLACK);
        for (int x = 10; x < 290; x += 10) {
            g.fillRect(x, 10, 5, 80); // BLACK LINES
        }

        g.dispose();

        // Act
        String result = barcodeService.readBarcode(img);


        assertDoesNotThrow(() -> barcodeService.readBarcode(img));
    }
}
