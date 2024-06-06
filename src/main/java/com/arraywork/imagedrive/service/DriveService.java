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

import com.arraywork.imagedrive.entity.Catalog;
import com.arraywork.imagedrive.entity.ImageObject;
import com.arraywork.imagedrive.entity.PageInfo;
import com.arraywork.imagedrive.repo.CatalogRepo;
import com.arraywork.imagedrive.repo.CatalogSpec;
import com.arraywork.imagedrive.util.AesUtil;
import com.arraywork.imagedrive.util.ImageInfo;
import com.arraywork.imagedrive.util.ImageUtil;
import com.arraywork.springforce.util.Arrays;
import com.arraywork.springforce.util.Files;
import com.arraywork.springforce.util.Pagination;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

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

    // Get image objects by catalog
    public PageInfo<ImageObject> listImages(String catalog, int page) throws IOException {
        List<File> files = listFiles(catalog);
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

    // Get catalog poster
    public Path getPosterPath(String catalog) throws IOException {
        List<File> files = listFiles(catalog);
        Assert.isTrue(!files.isEmpty(), "Catalog poster not found");
        Path srcPath = Path.of(files.get(0).getPath());
        return createThumbnail(srcPath, 480);
    }

    // Decrypt image path by id
    public Path getImagePath(String id, int size) throws IOException {
        String path = AesUtil.decrypt(id, aesKey);
        Assert.notNull(path, "Resource '" + id + "' not found");
        Path srcPath = Path.of(path);
        return size > 0 ? createThumbnail(srcPath, size) : srcPath;
    }

    // Create image thumbnail
    private Path createThumbnail(Path srcPath, int size) throws IOException {
        File srcFile = srcPath.toFile();
        Path thumbPath = Path.of(thumbDir, Files.getName(srcFile.getName()) + "_" + size + ".jpg");
        File thumbFile = thumbPath.toFile();

        if (!thumbFile.exists()) {
            BufferedImage srcImage = ImageIO.read(srcFile);
            ImageIO.write(ImageUtil.resize(srcImage, size), "jpg", thumbFile);
        }
        return thumbPath;
    }

    // List image files in catalog
    private List<File> listFiles(String catalog) {
        File dir = Path.of(imageLib, catalog).toFile();
        Assert.isTrue(dir.exists() && dir.isDirectory(), "Catalog '" + catalog + "' not found");

        String pattern = ".+\\.(jpg|jpeg|png|gif|bmp|webp|tiff)$";
        List<File> files = Arrays.filter(dir.listFiles(), v -> v.isFile() && v.getName().matches(pattern));
        if (!files.isEmpty()) {
            files.sort((a, b) -> a.getName().compareTo(b.getName()));
        }
        return files;
    }

}