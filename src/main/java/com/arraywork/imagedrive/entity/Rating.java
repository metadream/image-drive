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

    UNIVERSAL(1, "U"),
    GUIDANCE(2, "G"),
    RESTRICTED(3, "R"),
    ADULT(4, "A");

    private final Integer code;
    private final String label;

    public static class Converter extends GenericEnumConverter<Rating, Integer> {}

}