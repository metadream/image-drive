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
public enum Rating implements GenericEnum<Integer> {

    GENERAL(1, "G", "General Audiences"),
    GUIDANCE(2, "P", "Parental Guidance Suggested"),
    RESTRICTED(3, "R", "Restricted"),
    ADULT(4, "A", "Adults Only");

    private final Integer code;
    private final String abbr;
    private final String label;

    public static class Converter extends GenericEnumConverter<Rating, Integer> {}

}