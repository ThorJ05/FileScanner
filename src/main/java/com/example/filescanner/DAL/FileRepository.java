package com.example.filescanner.DAL;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class FileRepository {

    public String saveTiff(BufferedImage img, int documentId, int pageNumber) throws Exception {
        String dir = "data/documents/" + documentId;
        new File(dir).mkdirs();

        String path = dir + "/page_" + pageNumber + ".tiff";
        ImageIO.write(img, "TIFF", new File(path));

        return path;
    }
}
