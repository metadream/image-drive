package com.arraywork.imagedrive.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.arraywork.imagedrive.entity.ImageObject;
import com.arraywork.imagedrive.service.DriveService;
import com.arraywork.springforce.StaticResourceHandler;
import com.arraywork.springforce.util.HttpUtils;

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

    // List image objects by path
    @GetMapping("/**/")
    public List<ImageObject> list(HttpServletRequest request) throws IOException {
        String path = HttpUtils.getWildcard(request, "");
        return driveService.list(path);
    }

    // Serve image content by id
    @GetMapping("/{id}")
    public void serve(HttpServletRequest request, HttpServletResponse response,
        @PathVariable String id, String s) throws IOException {
        Path path = driveService.getPath(id, s);
        resourceHandler.serve(path, request, response);
    }

}