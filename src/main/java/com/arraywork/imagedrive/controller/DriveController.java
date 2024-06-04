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

    // List catalogs index
    @GetMapping("/")
    public Pagination<Catalog> index(Catalog condition, @RequestParam(required = false) Integer page)
        throws IOException {
        return driveService.index(condition, page != null ? page : 1);
    }

    // List image objects by directory name
    @GetMapping({ "/{name}/", "/{name}/{page:[1-9]\\d*}" })
    public PageInfo<ImageObject> list(@PathVariable String name, @PathVariable(required = false) Integer page)
        throws IOException {
        return driveService.list(name, page != null ? page : 1);
    }

    // Serve image content by id
    @GetMapping("/{id}")
    public void serve(HttpServletRequest request, HttpServletResponse response,
        @PathVariable String id, String s) throws IOException {
        Path path = driveService.getPath(id, s);
        resourceHandler.serve(path, request, response);
    }

}