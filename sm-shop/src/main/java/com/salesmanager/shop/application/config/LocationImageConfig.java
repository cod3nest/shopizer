package com.salesmanager.shop.application.config;

import com.salesmanager.shop.application.config.properties.CmsProperties;
import com.salesmanager.shop.utils.CloudFilePathUtils;
import com.salesmanager.shop.utils.ImageFilePath;
import com.salesmanager.shop.utils.LocalImageFilePathUtils;
import lombok.RequiredArgsConstructor;
import org.drools.core.util.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class LocationImageConfig {

    private final CmsProperties cmsProperties;

    @Bean
    public ImageFilePath img() {

        if (!StringUtils.isEmpty(cmsProperties.getMethod()) && !cmsProperties.getMethod().equals("default")) {
            CloudFilePathUtils cloudFilePathUtils = new CloudFilePathUtils();
            cloudFilePathUtils.setBasePath(cmsProperties.getContentUrl());
            return cloudFilePathUtils;

        } else {
            LocalImageFilePathUtils localImageFilePathUtils = new LocalImageFilePathUtils();
            localImageFilePathUtils.setBasePath(cmsProperties.getStaticPath());
            return localImageFilePathUtils;
        }


    }


}
