package com.arraywork.imagedrive.entity;

import com.arraywork.springforce.databind.GenericEnum;
import com.arraywork.springforce.databind.GenericEnumConverter;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Rating
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/06/04
 */
@AllArgsConstructor
@Getter
public enum Rating implements GenericEnum<String> {

    GENERAL("G", "General Audiences"),
    GUIDANCE("P", "Parental Guidance Suggested"),
    RESTRICTED("R", "Restricted"),
    ADULT("A", "Adults Only");

    private final String code;
    private final String label;

    public static class Converter extends GenericEnumConverter<Rating, String> {}

}