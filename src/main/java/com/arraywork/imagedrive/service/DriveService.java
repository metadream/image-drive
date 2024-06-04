package com.arraywork.imagedrive.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import com.arraywork.imagedrive.entity.ImageObject;
import com.arraywork.imagedrive.util.AesUtil;
import com.arraywork.imagedrive.util.ImageInfo;
import com.arraywork.springforce.util.Strings;

import jakarta.annotation.PostConstruct;
import net.coobird.thumbnailator.Thumbnails;

/**
 * Drive Service
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/06/03
 */
@Service
public class DriveService {

    @Value("${app.secret-key}")
    private String secretKey;

    @Value("${app.folder.storage}")
    private String storageFolder;

    @Value("${app.folder.thumbnail}")
    private String thumbnailFolder;

    // Init thumbnail folder
    @PostConstruct
    public void init() {
        File thumbnail = new File(thumbnailFolder);
        if (!thumbnail.exists()) thumbnail.mkdirs();
    }

    // Get image objects by path
    public List<ImageObject> list(String path) throws IOException {
        File storage = Path.of(storageFolder, path).toFile();
        Assert.isTrue(storage.exists() && storage.isDirectory(), "Path '" + path + "' not found");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // int total = storage.list().length; // list=null test, start 判断
        // int index = 0;

        List<ImageObject> imageObjects = new ArrayList<>();
        for (File file : storage.listFiles()) {
            if (!file.isFile()) continue;

            // Parse image info
            ImageInfo imageInfo = new ImageInfo(file);
            if (imageInfo.getMimeType() != null) {
                ImageObject imageObject = new ImageObject();
                imageObject.setId(AesUtil.encrypt(file.getPath(), secretKey));
                imageObject.setName(file.getName());
                imageObject.setSize(file.length());
                imageObject.setLastModified(file.lastModified());
                imageObject.setMimeType(imageInfo.getMimeType());
                imageObject.setWidth(imageInfo.getWidth());
                imageObject.setHeight(imageInfo.getHeight());
                imageObjects.add(imageObject);
            }
        }

        stopWatch.stop();
        System.out.println(imageObjects.size() + ", " + stopWatch.getTotalTimeMillis());
        return imageObjects;
    }

    // Decrypt image path by id
    public Path getPath(String id, String s) throws IOException {
        String origiPath = AesUtil.decrypt(id, secretKey);
        Assert.notNull(origiPath, "Image '" + id + "' not found");

        // Generate thumbnail
        if (Strings.isInteger(s)) {
            int size = Integer.parseInt(s);
            Path thumbPath = Path.of(thumbnailFolder, id + "_" + size + ".jpg");
            if (!thumbPath.toFile().exists()) {
                Thumbnails.of(origiPath).size(size, size).outputQuality(0.9).toFile(thumbPath.toString());
            }
            return thumbPath;
        }
        return Path.of(origiPath);
    }

}