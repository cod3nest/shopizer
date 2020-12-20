package com.salesmanager.shop.model.shop;

import lombok.Data;

import java.io.Serializable;

@Data
public class BreadcrumbItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String label;
	private String url;
	private BreadcrumbItemType itemType;
}
