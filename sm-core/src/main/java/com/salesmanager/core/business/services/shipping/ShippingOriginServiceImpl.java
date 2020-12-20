package com.salesmanager.core.business.services.shipping;

import com.salesmanager.core.business.repositories.shipping.ShippingOriginRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.shipping.ShippingOrigin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
class ShippingOriginServiceImpl extends SalesManagerEntityServiceImpl<Long, ShippingOrigin> implements ShippingOriginService {

    private final ShippingOriginRepository shippingOriginRepository;

    public ShippingOriginServiceImpl(ShippingOriginRepository shippingOriginRepository) {
        super(shippingOriginRepository);
        this.shippingOriginRepository = shippingOriginRepository;
    }


    @Override
    public ShippingOrigin getByStore(MerchantStore store) {
        // TODO Auto-generated method stub
        return shippingOriginRepository.findByStore(store.getId());
    }


}
