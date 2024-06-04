package com.arraywork.imagedrive.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import com.arraywork.springforce.util.KeyGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Catalog Index
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/06/04
 */
@Entity
@Data
public class Catalog {

    @Id
    @Column(length = 24, insertable = false, updatable = false)
    @GenericGenerator(name = "nano-id-generator", type = KeyGenerator.NanoId.class)
    @GeneratedValue(generator = "nano-id-generator")
    private String id;

    // 编号
    @Column(unique = true)
    @NotBlank(message = "编号不能为空")
    @Size(max = 20, message = "编号不能超过{max}个字符")
    private String code;

    // 标题
    @NotBlank(message = "标题不能为空")
    @Size(max = 255, message = "标题不能超过 {max} 个字符")
    private String title;

    // 工作室
    @Column(length = 20)
    private String studio;

    // 分级
    @Convert(converter = Rating.Converter.class)
    private Rating rating;

    // 模特
    @Column(columnDefinition = "JSON DEFAULT NULL")
    private String[] models;

    // 标签
    @Column(columnDefinition = "JSON DEFAULT NULL")
    private String[] tags;

    private boolean starred;

    @UpdateTimestamp
    private LocalDateTime lastModified;

}