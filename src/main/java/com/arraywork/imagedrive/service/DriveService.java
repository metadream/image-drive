package com.arraywork.imagedrive.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.arraywork.imagedrive.entity.Catalog;
import com.arraywork.imagedrive.entity.ImageObject;
import com.arraywork.imagedrive.entity.PageInfo;
import com.arraywork.imagedrive.repo.CatalogRepo;
import com.arraywork.imagedrive.repo.CatalogSpec;
import com.arraywork.imagedrive.util.AesUtil;
import com.arraywork.imagedrive.util.ImageInfo;
import com.arraywork.springforce.util.Pagination;
import com.arraywork.springforce.util.Strings;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import net.coobird.thumbnailator.Thumbnails;

/**
 * Drive Service
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/06/03
 */
@Service
public class DriveService {

    @Value("${app.key.aes}")
    private String aesKey;

    @Value("${app.dir.image-lib}")
    private String imageLib;

    @Value("${app.dir.thumbnail}")
    private String thumbDir;

    @Value("${app.pagesize}")
    private int pageSize;

    @Resource
    private CatalogRepo catalogRepo;

    // Init thumbnail folder
    @PostConstruct
    public void init() {
        File dir = new File(thumbDir);
        if (!dir.exists()) dir.mkdirs();
    }

    // List catalog index
    public Pagination<Catalog> listCatalogs(Catalog condition, int page) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Catalog> pageInfo = catalogRepo.findAll(new CatalogSpec(condition), pageable);
        return new Pagination<Catalog>(pageInfo);
    }

    // Get image objects by path
    public PageInfo<ImageObject> listImages(String name, int page) throws IOException {
        File storage = Path.of(imageLib, name).toFile();
        Assert.isTrue(storage.exists() && storage.isDirectory(), "Path '" + name + "' not found");

        List<File> files = Arrays.asList(storage.listFiles());
        files.sort((a, b) -> a.getName().compareTo(b.getName()));
        int totalSize = files.size();

        PageInfo<ImageObject> pageInfo = new PageInfo<>();
        List<ImageObject> imageObjects = new ArrayList<>();
        pageInfo.setList(imageObjects);
        pageInfo.setPageNumber(page);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotalSize(totalSize);
        pageInfo.setTotalPages((totalSize + pageSize - 1) / pageSize);

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalSize);
        for (int i = start; i < end; i++) {
            File file = files.get(i);
            if (!file.isFile()) continue;

            // Parse image info
            ImageInfo imageInfo = new ImageInfo(file);
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
        return pageInfo;
    }

    // Decrypt image path by id
    public Path getImagePath(String id, String s) throws IOException {
        String origiPath = AesUtil.decrypt(id, aesKey);
        Assert.notNull(origiPath, "Resource '" + id + "' not found");

        // Generate thumbnail
        if (Strings.isInteger(s)) {
            int size = Integer.parseInt(s);
            Path thumbPath = Path.of(thumbDir, id + "_" + size + ".jpg");
            if (!thumbPath.toFile().exists()) {
                Thumbnails.of(origiPath).size(size, size).outputQuality(0.9).toFile(thumbPath.toString());
            }
            return thumbPath;
        }
        return Path.of(origiPath);
    }

}