package com.salesmanager.core.business.repositories.catalog.catalog;

import com.salesmanager.core.model.catalog.catalog.CatalogCategoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogEntryRepository extends JpaRepository<CatalogCategoryEntry, Long> {

}
