package com.epam.test.common.test;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.gm4java.engine.GMConnection;
import org.gm4java.engine.GMService;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;

import java.io.File;
import java.util.Date;

/**
 * Created by Aleksandr_Stasko on 10/28/2016.
 */
public class ExecutorGM implements Runnable {
    private Resolution resolution;
    private GMService service;
    private String filter;
    private int density;
    private String outputImagePath;
    private File image;
    private final Logger logger = Logger.getLogger(this.getClass());

    public ExecutorGM(GMService service, File image, String outputImagePath, Resolution resolution, String filter, int density) {
        this.service = service;
        this.image = image;
        this.outputImagePath = outputImagePath;
        this.resolution = resolution;
        this.filter = filter;
        this.density = density;
    }

    public void run() {
        StopWatch stopWatch =new Log4JStopWatch(this.getClass() + " " + outputImagePath);
        GMConnection conn = null;
        try {
            conn = service.getConnection();
            conn.execute("convert", "-size", resolution.getWidth() + "x" + resolution.getHeight(), image.getPath(), "-filter", "Sinc", "-resize", resolution.getWidth() + "x" + resolution.getHeight() + "^",
                    "-gravity", "center", "-crop", resolution.getWidth() + "x" + resolution.getHeight() + "+0+0", "-unsharp", "0.25x0.25+8+0.065", "-quality", "82.0", outputImagePath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        stopWatch.stop();
    }
}
