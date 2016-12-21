package com.epam.test.common.test;

import org.apache.log4j.*;
import org.gm4java.engine.GMService;
import org.gm4java.engine.support.GMConnectionPoolConfig;
import org.gm4java.engine.support.PooledGMService;
import org.gm4java.im4java.GMBatchCommand;
import org.im4java.core.CompositeCmd;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.Info;
import org.im4java.process.ProcessEvent;
import org.im4java.process.ProcessEventListener;
import org.im4java.process.ProcessStarter;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Aleksandr_Stasko on 10/21/2016.
 */
public class Main {

    private static final String EXTENSION = ".jpg";
    private static final String GM_PATH = "D:\\programs\\GraphicsMagick-1.3.25-Q16";
    private static final String IM_PATH = "D:\\programs\\ImageMagick";
    private static final String FILTER = "Sinc";
    private static final int DENSITY = 96;



    public static void main(String[] args) throws IOException {
        Logger logger = Logger.getLogger("org.perf4j.TimingLogger");
        Appender myAppender = new ConsoleAppender(new SimpleLayout());
        Appender myAppender1 = new FileAppender(new SimpleLayout(), "log.txt");
        logger.addAppender(myAppender);
        logger.addAppender(myAppender1);
        StopWatch stopWatch = new Log4JStopWatch("main");

        Map<String, Resolution> resolutions = new HashMap<>();
        resolutions.put("small", new Resolution(480, 320));
        resolutions.put("medium", new Resolution(768, 512));
        resolutions.put("big", new Resolution(992, 661));
        resolutions.put("xl", new Resolution(1200, 800));

        ProcessStarter.setGlobalSearchPath("D:\\programs\\ImageMagick");
        ConvertCmd cmd = new ConvertCmd();
//        cmd.setAsyncMode(true);


        GMConnectionPoolConfig config = new GMConnectionPoolConfig();
        config.setMaxIdle(7);
        config.setMaxActive(7);

        GMService service = new PooledGMService(config);
        GMBatchCommand command = new GMBatchCommand(service, "convert");

        ExecutorService serv = Executors.newFixedThreadPool(7);
        File srcDir = new File("build\\resources\\main\\source");
        File[] fileList = srcDir.listFiles();
        Map<String, TimeHandler> timeHandlers = new LinkedHashMap();
        for (File img : fileList) {
            String fileName = img.getAbsolutePath();
            String name = fileName.replace("\\source\\", "\\out\\");
            final String fileNameWithoutExt = name.substring(0, name.lastIndexOf("."));
            File newFile = new File(fileNameWithoutExt);
            if (!newFile.exists()) {
                newFile.getParentFile().mkdirs();
            }


            for (String key : resolutions.keySet()) {
                int index = fileNameWithoutExt.indexOf("out\\") + 4;
                String timeName = fileNameWithoutExt.substring(index, index + 1) + "_" + key;
                TimeHandler timeHandler;
                if(timeHandlers.containsKey(timeName)) {
                    timeHandler = timeHandlers.get(timeName);
                } else {
                    timeHandler = new TimeHandler(timeName);
                    timeHandlers.put(timeName, timeHandler);
                }
//                for (int i = 0; i < 1000; i++) {

//                new ExecutorGM2(command, img, fileNameWithoutExt + "_" + key + "_gm2" /*+ i */+ EXTENSION, resolutions.get(key), FILTER, DENSITY, timeHandler).run();
                new ExecutorIM(cmd, img, fileNameWithoutExt + "_" + key + "_im" + EXTENSION, resolutions.get(key), FILTER, DENSITY).run();
//                serv.execute(new ExecutorGM2(command, img, fileNameWithoutExt + "_" + key + "_gm2" /*+ i */+ EXTENSION, resolutions.get(key), FILTER, DENSITY, timeHandler));
//                serv.execute(new ExecutorIM(cmd, img, fileNameWithoutExt + "_" + key + "_im" + EXTENSION, resolutions.get(key), FILTER, DENSITY));
//                serv.execute(new ExecutorGM(service, img, fileNameWithoutExt + "_" + key + "_gm" + EXTENSION, resolutions.get(key), FILTER, DENSITY));
//                }
            }
        }

        serv.shutdown();
        while (!serv.isTerminated()) {
        }

//        new ImageComparator(service).run();
        stopWatch.stop();
        for (String key :timeHandlers.keySet()) {
            TimeHandler timeHandler = timeHandlers.get(key);
            System.out.println(timeHandler.getName() + " " + timeHandler.getTime());
            System.out.println("min: " + timeHandler.getMin() + " max: " + timeHandler.getMax());
        }
    }

    public static class TimeHandler {
        private long min = Long.MAX_VALUE;
        private long max = Long.MIN_VALUE;
        private long time = 0;
        private int count = 0;
        private String name;

        public TimeHandler(String name) {
            this.name = name;
        }

        public synchronized void addTime(Long time) {
            this.time += time;
            count++;
            min = min > time ? time : min;
            max = max < time ? time : max;
        }

        public long getTime() {
            return time / count;
        }

        public String getName() {
            return name;
        }

        public long getMax() {
            return max;
        }

        public long getMin() {

            return min;
        }
    }
}
