package com.arraywork.imagedrive.entity;

import java.time.LocalDate;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.arraywork.springforce.util.KeyGenerator;
import com.arraywork.springforce.util.Validator;

import io.hypersistence.utils.hibernate.type.json.JsonStringType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Column(unique = true)
    @NotBlank(message = "Catalog code cannot be blank")
    @Size(max = 20, message = "Catalog code cannot exceed {max} characters")
    private String code;

    @NotBlank(message = "Catalog title cannot be blank", groups = Validator.Update.class)
    @Size(max = 255, message = "Catalog title cannot exceed {max} characters")
    private String title;

    @NotNull(message = "Rating cannot be null", groups = Validator.Update.class)
    @Convert(converter = Rating.Converter.class)
    private Rating rating;

    @NotNull(message = "Issue date cannot be null", groups = Validator.Update.class)
    private LocalDate issueDate;

    @Size(max = 20, message = "Studio cannot exceed {max} characters")
    private String studio;

    @Type(JsonStringType.class)
    @Column(columnDefinition = "JSON DEFAULT NULL")
    private String[] models;

    @Type(JsonStringType.class)
    @Column(columnDefinition = "JSON DEFAULT NULL")
    private String[] locations;

    @Type(JsonStringType.class)
    @Column(columnDefinition = "JSON DEFAULT NULL")
    private String[] tags;

    private boolean starred;

    @Transient
    private String keyword;

}