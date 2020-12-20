package com.salesmanager.core.business.services.system;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.system.SystemConfigurationRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.system.SystemConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
class SystemConfigurationServiceImpl extends
        SalesManagerEntityServiceImpl<Long, SystemConfiguration> implements
        SystemConfigurationService {

    private final SystemConfigurationRepository systemConfigurationReposotory;

    public SystemConfigurationServiceImpl(
            SystemConfigurationRepository systemConfigurationReposotory) {
        super(systemConfigurationReposotory);
        this.systemConfigurationReposotory = systemConfigurationReposotory;
    }

    public SystemConfiguration getByKey(String key) throws ServiceException {
        return systemConfigurationReposotory.findByKey(key);
    }


}
