package com.arraywork.imagedrive.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import com.arraywork.imagedrive.entity.Catalog;
import com.arraywork.imagedrive.entity.ImageObject;
import com.arraywork.imagedrive.entity.PageInfo;
import com.arraywork.imagedrive.repo.CatalogRepo;
import com.arraywork.imagedrive.repo.CatalogSpec;
import com.arraywork.imagedrive.util.AesUtil;
import com.arraywork.imagedrive.util.ImageInfo;
import com.arraywork.imagedrive.util.ImageUtil;
import com.arraywork.springforce.util.Arrays;
import com.arraywork.springforce.util.Numbers;
import com.arraywork.springforce.util.Pagination;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * Drive Service
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/06/03
 */
@Service
@Slf4j
public class DriveService {

    @Value("${app.key.aes}")
    private String aesKey;

    @Value("${app.path.image-lib}")
    private String imageLib;

    @Value("${app.path.thumbnail}")
    private String thumbDir;

    @Value("${app.pagesize}")
    private int pageSize;

    @Resource
    private CatalogRepo catalogRepo;

    // Initialize thumbnail directory
    @PostConstruct
    public void init() {
        File dir = new File(thumbDir);
        if (!dir.exists()) dir.mkdirs();
    }

    // List catalogs
    public Pagination<Catalog> listCatalogs(Catalog condition, int page) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Catalog> pageInfo = catalogRepo.findAll(new CatalogSpec(condition), pageable);
        return new Pagination<Catalog>(pageInfo);
    }

    // List images
    public PageInfo<ImageObject> listImages(String catalog, int page) throws IOException {
        List<File> files = listFiles(catalog);
        int totalSize = files.size();
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalSize);

        PageInfo<ImageObject> pageInfo = new PageInfo<>();
        List<ImageObject> imageObjects = new ArrayList<>();
        pageInfo.setList(imageObjects);
        pageInfo.setPageNumber(page);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotalSize(totalSize);
        pageInfo.setTotalPages((totalSize + pageSize - 1) / pageSize);

        StopWatch sw = new StopWatch();
        for (int i = start; i < end; i++) {
            File file = files.get(i);

            sw.start();
            ImageInfo imageInfo = new ImageInfo(file);
            sw.stop();
            log.info("Get image info [{}]: {}, {} ms", i, Numbers.formatBytes(file.length()),
                sw.lastTaskInfo().getTimeMillis());

            if (imageInfo.getMimeType() != null) {
                ImageObject imageObject = new ImageObject();
                imageObject.setId(AesUtil.encrypt(file.getPath(), aesKey));
                imageObject.setName(file.getName());
                imageObject.setSize(file.length());
                imageObject.setLastModified(file.lastModified());
                imageObject.setMimeType(imageInfo.getMimeType());
                imageObject.setWidth(imageInfo.getWidth());
                imageObject.setHeight(imageInfo.getHeight());
                imageObjects.add(imageObject);
            }
        }
        log.info("Total time [listImages]: {}ms", sw.getTotalTimeMillis());
        return pageInfo;
    }

    // Get poster path
    public Path getPosterPath(String catalog) throws IOException {
        List<File> files = listFiles(catalog);
        Assert.isTrue(!files.isEmpty(), "Catalog poster not found");
        String id = AesUtil.encrypt(files.get(0).getPath(), aesKey);
        return getImagePath(id, 480);
    }

    // Get image path
    public Path getImagePath(String id, int size) throws IOException {
        String path = AesUtil.decrypt(id, aesKey);
        Assert.notNull(path, "Resource '" + id + "' not found");

        Path srcPath = Path.of(path);
        Path thumbPath = Path.of(thumbDir, id + "_" + size + ".jpg");
        File thumbFile = thumbPath.toFile();

        if (size > 0) {
            if (!thumbFile.exists()) {
                StopWatch sw = new StopWatch();
                sw.start();
                BufferedImage srcImage = ImageIO.read(srcPath.toFile());
                sw.stop();
                log.info("Read image: {}ms", sw.lastTaskInfo().getTimeMillis());

                sw.start();
                BufferedImage thumbImage = ImageUtil.resizeByThumbnailator(srcImage, size);
                ImageIO.write(thumbImage, "jpg", thumbFile);
                sw.stop();
                log.info("Resize image: {}ms", sw.getTotalTimeMillis());
            }
            return thumbPath;
        }
        return srcPath;
    }

    // List files
    private List<File> listFiles(String catalog) {
        File dir = Path.of(imageLib, catalog).toFile();
        Assert.isTrue(dir.exists() && dir.isDirectory(), "Catalog '" + catalog + "' not found");

        String pattern = "(?i).+\\.(jpg|jpeg|png|gif|bmp|webp|tiff)$";
        List<File> files = Arrays.filter(dir.listFiles(), v -> v.isFile() && v.getName().matches(pattern));
        if (!files.isEmpty()) {
            files.sort((a, b) -> a.getName().compareTo(b.getName()));
        }
        return files;
    }

}