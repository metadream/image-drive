package com.arraywork.imagedrive.entity;

import java.util.List;

import lombok.Data;

/**
 * Page Information
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/06/03
 */
@Data
public class PageInfo {

    private int pageNumber;
    private int pageSize;
    private int totalSize;
    private int totalPages;
    private List<ImageObject> list;

}