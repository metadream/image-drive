package com.arraywork.imagedrive.controller;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arraywork.imagedrive.entity.Catalog;
import com.arraywork.imagedrive.entity.ImageObject;
import com.arraywork.imagedrive.entity.PageInfo;
import com.arraywork.imagedrive.service.DriveService;
import com.arraywork.springforce.StaticResourceHandler;
import com.arraywork.springforce.util.Pagination;
import com.arraywork.springforce.util.Strings;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Drive Controller
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/06/03
 */
@RestController
public class DriveController {

    @Resource
    private StaticResourceHandler resourceHandler;
    @Resource
    private DriveService driveService;

    // List catalogs
    @GetMapping("/")
    public Pagination<Catalog> listCatalogs(Catalog condition, String page) throws IOException {
        page = page != null && page.matches("[1-9]\\d*") ? page : "1";
        return driveService.listCatalogs(condition, Integer.parseInt(page));
    }

    // List images
    @GetMapping({ "/{catalog}/", "/{catalog}/{page:[1-9]\\d*}" })
    public PageInfo<ImageObject> listImages(@PathVariable String catalog, @PathVariable(required = false) Integer page)
        throws IOException {
        return driveService.listImages(catalog, page != null ? page : 1);
    }

    // Serve poster image
    @GetMapping("/{catalog}/poster.jpg")
    public void serve(HttpServletRequest request, HttpServletResponse response, @PathVariable String catalog)
        throws IOException {
        Path path = driveService.getPosterPath(catalog);
        resourceHandler.serve(path, request, response);
    }

    // Serve image
    @GetMapping("/{id}")
    public void serve(HttpServletRequest request, HttpServletResponse response,
        @PathVariable String id, @RequestParam(required = false) String s) throws IOException {
        int size = Strings.isInteger(s) ? Integer.parseInt(s) : 0;
        Path path = driveService.getImagePath(id, size);
        resourceHandler.serve(path, request, response);
    }

}