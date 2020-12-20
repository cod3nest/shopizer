package com.salesmanager.shop.model.shop;

import com.salesmanager.core.model.reference.language.Language;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Breadcrumb implements Serializable {

    private static final long serialVersionUID = 1L;
    private BreadcrumbItemType itemType;
    private Language language;
    private String urlRefContent = null;
    private List<BreadcrumbItem> breadCrumbs = new ArrayList<>();
}
