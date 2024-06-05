package com.arraywork.imagedrive;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.arraywork.springforce.BaseApplication;

import jakarta.annotation.PostConstruct;

/**
 * Drive Application
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/06/03
 */
@SpringBootApplication
public class DriveApplication extends BaseApplication {

    @Value("${app.dir.image-lib}")
    private String imageLib;

    public static void main(String[] args) {
        SpringApplication.run(DriveApplication.class, args);
    }

    @PostConstruct
    public void startMonitor() throws Exception {
        DirectoryMonitor monitor = new DirectoryMonitor(imageLib, 3000);
        monitor.start();
    }

}