package com.arraywork.imagedrive.entity;

import java.time.LocalDateTime;

import lombok.Data;

/**
 *
 * @author AiChen
 * @created 2024/06/04
 */
@Data
public class ImageCatalog {

    private String code;
    private String title;
    private String studio;
    private String rating;
    private String[] models;
    private String[] tags;
    private LocalDateTime lastModified;

}