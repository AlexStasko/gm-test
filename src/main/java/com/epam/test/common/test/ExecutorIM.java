package com.epam.test.common.test;

import org.apache.log4j.Logger;
import org.gm4java.engine.GMConnection;
import org.gm4java.engine.GMService;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessTask;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Aleksandr_Stasko on 10/28/2016.
 */
public class ExecutorIM implements Runnable {
    private Resolution resolution;
    private ConvertCmd cmd;
    private String filter;
    private int density;
    private String outputImagePath;
    private File image;
    private final Logger logger = Logger.getLogger(this.getClass());
    public ExecutorIM(ConvertCmd cmd, File image, String outputImagePath, Resolution resolution, String filter, int density) {
        this.cmd = cmd;
        this.image = image;
        this.outputImagePath = outputImagePath;
        this.resolution = resolution;
        this.filter = filter;
        this.density = density;
    }

    public void run() {
        StopWatch stopWatch = new Log4JStopWatch(outputImagePath);

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
//            ProcessTask pt = cmd.getProcessTask(op);
//            ExecutorService exec = Executors.newSingleThreadExecutor();
//            exec.execute(pt);
//            exec.shutdown();
//            while (!exec.isTerminated()) {}
            cmd.run(op);
        } catch (Exception e) {
            e.printStackTrace();
        }

        stopWatch.stop();
    }
}
