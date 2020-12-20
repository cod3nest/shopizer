package com.salesmanager.shop.model.shop;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageInformation implements Serializable {

	private static final long serialVersionUID = 1L;
	private String pageTitle;
	private String pageDescription;
	private String pageKeywords;
	private String pageUrl;
}
