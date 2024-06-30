package com.arraywork.imagewise.entity;

import lombok.Data;

/**
 * Image Object
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/06/03
 */
@Data
public class ImageObject {

    private String id;
    private String name;
    private String mimeType;
    private int width;
    private int height;
    private long size;
    private long lastModified;

}