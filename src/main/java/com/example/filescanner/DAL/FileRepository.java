package com.example.filescanner.DAL;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileRepository {

    // -----------------------------------------
    // SAVE TIFF TO DISK (FAST)
    // -----------------------------------------
    public String saveTiff(BufferedImage img, int documentId, int pageNumber) throws Exception {

        String dir = "data/documents/" + documentId;
        new File(dir).mkdirs();

        String filePath = dir + "/page_" + pageNumber + ".tiff";

        ImageIO.write(img, "TIFF", new File(filePath));

        return filePath;
    }

    // -----------------------------------------
    // READ TIFF BYTES FROM DISK (FAST)
    // -----------------------------------------
    public byte[] readBytes(String filePath) throws IOException {

        File file = new File(filePath);
        byte[] bytes = new byte[(int) file.length()];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(bytes);
        }

        return bytes;
    }
}
