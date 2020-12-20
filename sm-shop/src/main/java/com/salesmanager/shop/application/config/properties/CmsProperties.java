package com.salesmanager.shop.application.config.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class CmsProperties {

    @Value("${config.cms.contentUrl}")
    private String contentUrl;

    @Value("${config.cms.method}")
    private String method;

    @Value("${config.cms.static.path}")
    private String staticPath;
}
