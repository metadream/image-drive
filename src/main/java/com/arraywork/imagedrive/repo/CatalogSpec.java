package com.arraywork.imagedrive.repo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.arraywork.imagedrive.entity.Catalog;
import com.arraywork.imagedrive.entity.Rating;
import com.arraywork.springforce.util.Arrays;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Catalog Specification
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/06/05
 */
public class CatalogSpec implements Specification<Catalog> {

    private static final long serialVersionUID = 820455927386914751L;
    private Catalog condition;

    public CatalogSpec(Catalog condition) {
        this.condition = condition;
    }

    @Override
    public Predicate toPredicate(Root<Catalog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        String keyword = condition.getKeyword();
        if (StringUtils.hasText(keyword)) {
            predicates.add(cb.or(
                cb.like(root.get("code"), "%" + keyword + "%"),
                cb.like(root.get("title"), "%" + keyword + "%"),
                cb.like(root.get("studio"), "%" + keyword + "%"),
                cb.and(
                    root.get("models").isNotNull(),
                    cb.like(root.get("models"), cb.literal("%\"" + keyword + "\"%"))),
                cb.and(
                    root.get("locations").isNotNull(),
                    cb.like(root.get("locations"), cb.literal("%\"" + keyword + "\"%"))),
                cb.and(
                    root.get("tags").isNotNull(),
                    cb.like(root.get("tags"), cb.literal("%\"" + keyword + "\"%")))
            // THE END
            ));
        }

        String code = condition.getCode();
        if (StringUtils.hasText(code)) {
            predicates.add(cb.equal(root.get("code"), code));
        }

        String studio = condition.getStudio();
        if (StringUtils.hasText(studio)) {
            predicates.add(cb.equal(root.get("studio"), studio));
        }

        String[] models = condition.getModels();
        if (!Arrays.isEmpty(models)) {
            predicates.add(cb.and(root.get("models").isNotNull(),
                cb.like(root.get("models"), cb.literal("%\"" + models[0] + "\"%"))));
        }

        String[] locations = condition.getLocations();
        if (!Arrays.isEmpty(locations)) {
            predicates.add(cb.and(root.get("locations").isNotNull(),
                cb.like(root.get("locations"), cb.literal("%\"" + locations[0] + "\"%"))));
        }

        String[] tags = condition.getTags();
        if (!Arrays.isEmpty(tags)) {
            predicates.add(cb.and(root.get("tags").isNotNull(),
                cb.like(root.get("tags"), cb.literal("%\"" + tags[0] + "\"%"))));
        }

        Rating rating = condition.getRating();
        if (rating != null) {
            predicates.add(cb.equal(root.get("rating"), rating));
        }

        boolean starred = condition.isStarred();
        if (starred) {
            predicates.add(cb.isTrue(root.get("starred")));
        }

        if (predicates.size() > 0) {
            Predicate[] p = new Predicate[predicates.size()];
            query.where(cb.and(predicates.toArray(p)));
        }
        query.orderBy(cb.desc(root.get("issueDate")), cb.desc(root.get("code")));
        return query.getRestriction();
    }

}