package com.arraywork.imagedrive.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.arraywork.imagedrive.entity.Catalog;

/**
 * Catalog Repository
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/06/04
 */
public interface CatalogRepo extends JpaRepository<Catalog, String>, JpaSpecificationExecutor<Catalog> {}