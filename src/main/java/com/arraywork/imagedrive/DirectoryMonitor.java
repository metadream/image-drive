package com.arraywork.imagedrive;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import com.arraywork.imagedrive.service.DirectoryListener;

/**
 * Directory Monitor
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/06/03
 */
public class DirectoryMonitor {

    private FileAlterationMonitor monitor;

    // Create directory monitor
    public DirectoryMonitor(String rootDirectory, long intervalMillis) {
        FileAlterationObserver observer = new FileAlterationObserver(rootDirectory);
        observer.addListener(new DirectoryListener());
        monitor = new FileAlterationMonitor(intervalMillis);
        monitor.addObserver(observer);
    }

    public void start() throws Exception {
        monitor.start();
    }

    public void stop() throws Exception {
        monitor.stop();
    }

}