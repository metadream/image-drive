package com.arraywork.imagedrive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import com.arraywork.springforce.BaseApplication;

/**
 * Drive Application
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/06/03
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class DriveApplication extends BaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(DriveApplication.class, args);
    }

}