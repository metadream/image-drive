package com.arraywork.imagedrive.controller;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.arraywork.imagedrive.entity.PageInfo;
import com.arraywork.imagedrive.service.DriveService;
import com.arraywork.springforce.StaticResourceHandler;

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

    // List image objects by directory name
    @GetMapping("/{name}/")
    public PageInfo list(@PathVariable String name) throws IOException {
        return driveService.list(name, 1);
    }

    // List image objects by directory name
    @GetMapping("/{name}/{page:[1-9]\\d*}")
    public PageInfo list(@PathVariable String name, @PathVariable int page) throws IOException {
        return driveService.list(name, page);
    }

    // Serve image content by id
    @GetMapping("/{id}")
    public void serve(HttpServletRequest request, HttpServletResponse response,
        @PathVariable String id, String s) throws IOException {
        Path path = driveService.getPath(id, s);
        resourceHandler.serve(path, request, response);
    }

}