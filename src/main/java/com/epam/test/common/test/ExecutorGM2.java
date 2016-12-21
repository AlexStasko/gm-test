package com.epam.test.common.test;

import com.epam.test.common.test.Main.TimeHandler;
import org.gm4java.im4java.GMBatchCommand;
import org.gm4java.im4java.GMOperation;
import org.im4java.core.IMOperation;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;

import java.io.File;

/**
 * Date: 10/28/2016
 * @author Aleksandr_Stasko
 */
public class ExecutorGM2 implements Runnable {
    private Resolution resolution;
    private GMBatchCommand cmd;
    private String filter;
    private int density;
    private String outputImagePath;
    private File image;
    private TimeHandler timeHandler;

    public ExecutorGM2 (GMBatchCommand cmd, File image, String outputImagePath, Resolution resolution, String filter, int density, TimeHandler timeHandler) {
        this.cmd = cmd;
        this.image = image;
        this.outputImagePath = outputImagePath;
        this.resolution = resolution;
        this.filter = filter;
        this.density = density;
        this.timeHandler = timeHandler;
    }

    public void run() {
        StopWatch stopWatch = new Log4JStopWatch(this.getClass() + " " + outputImagePath);

        IMOperation op = new IMOperation();

        op.size(resolution.getWidth(), resolution.getHeight());
        op.addImage(image.getPath());
        op.filter(filter);
        op.resize(resolution.getWidth(), resolution.getHeight(), '^');
        op.gravity("center");
        op.crop(resolution.getWidth(), resolution.getHeight(), 0, 0);
        op.unsharp(0.25, 0.25, 4.0, 0.065);
        op.quality(82.0);
        op.units("PixelsPerInch");
        op.density(density);
        op.resample(density);
        op.addImage(outputImagePath);

        try {
            cmd.run(op);
        } catch (Exception e) {
            e.printStackTrace();
        }

        stopWatch.stop();
        timeHandler.addTime(stopWatch.getElapsedTime());
    }
}
