package com.salesmanager.core.business.services.shipping;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.shipping.ShippingQuoteRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.shipping.Quote;
import com.salesmanager.core.model.shipping.ShippingSummary;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.Validate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
class ShippingQuoteServiceImpl extends SalesManagerEntityServiceImpl<Long, Quote> implements ShippingQuoteService {

    private final ShippingQuoteRepository shippingQuoteRepository;
    private final ShippingService shippingService;

    public ShippingQuoteServiceImpl(ShippingQuoteRepository repository, @Lazy ShippingService shippingService) {
        super(repository);
        this.shippingQuoteRepository = repository;
        // TODO Auto-generated constructor stub
        this.shippingService = shippingService;
    }

    @Override
    public List<Quote> findByOrder(Order order) throws ServiceException {
        Validate.notNull(order, "Order cannot be null");
        return this.shippingQuoteRepository.findByOrder(order.getId());
    }

    @Override
    public ShippingSummary getShippingSummary(Long quoteId, MerchantStore store) throws ServiceException {

        Validate.notNull(quoteId, "quoteId must not be null");

        Quote q = shippingQuoteRepository.getOne(quoteId);


        ShippingSummary quote = null;

        if (q != null) {

            quote = new ShippingSummary();
            quote.setDeliveryAddress(q.getDelivery());
            quote.setShipping(q.getPrice());
            quote.setShippingModule(q.getModule());
            quote.setShippingOption(q.getOptionName());
            quote.setShippingOptionCode(q.getOptionCode());
            quote.setHandling(q.getHandling());

            if (shippingService.hasTaxOnShipping(store)) {
                quote.setTaxOnShipping(true);
            }


        }


        return quote;

    }


}
