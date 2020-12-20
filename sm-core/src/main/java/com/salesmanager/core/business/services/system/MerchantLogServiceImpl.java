package com.salesmanager.core.business.services.system;

import com.salesmanager.core.business.repositories.system.MerchantLogRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.system.MerchantLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
class MerchantLogServiceImpl extends
        SalesManagerEntityServiceImpl<Long, MerchantLog> implements
        MerchantLogService {

    private final MerchantLogRepository merchantLogRepository;

    public MerchantLogServiceImpl(
            MerchantLogRepository merchantLogRepository) {
        super(merchantLogRepository);
        this.merchantLogRepository = merchantLogRepository;
    }
}
