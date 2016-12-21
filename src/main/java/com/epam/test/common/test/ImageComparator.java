package com.epam.test.common.test;

import org.gm4java.engine.GMService;

import java.io.File;

/**
 * Created by Aleksandr_Stasko on 10/31/2016.
 */
public class ImageComparator implements Runnable{

    private GMService service;

    public ImageComparator(GMService service) {
        this.service = service;
    }

    @Override
    public void run() {

        File[] files = (new File("build\\resources\\main\\out")).listFiles();
        for (File file: files) {
            if (file.getPath().contains("gm")) {
                String compareFile = file.getPath().replace("_gm.", "_im.");
                String outputFile = file.getPath().replace("_gm.", "_compare.");
                try {
                    service.execute("compare", "-highlight-style", "Tint", "-highlight-color", "purple", "-file", outputFile, file.getPath(), compareFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
