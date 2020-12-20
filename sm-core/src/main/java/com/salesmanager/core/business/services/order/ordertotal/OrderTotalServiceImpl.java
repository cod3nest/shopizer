package com.salesmanager.core.business.services.order.ordertotal;

import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.OrderSummary;
import com.salesmanager.core.model.order.OrderTotal;
import com.salesmanager.core.model.order.OrderTotalVariation;
import com.salesmanager.core.model.order.RebatesOrderTotalVariation;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.core.modules.order.total.OrderTotalPostProcessorModule;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
class OrderTotalServiceImpl implements OrderTotalService {

    @Resource(name = "orderTotalsPostProcessors")
    private List<OrderTotalPostProcessorModule> orderTotalPostProcessors;

    private final ProductService productService;
    private final LanguageService languageService;

    @Override
    public OrderTotalVariation findOrderTotalVariation(OrderSummary summary, Customer customer, MerchantStore store, Language language)
            throws Exception {

        RebatesOrderTotalVariation variation = new RebatesOrderTotalVariation();

        List<OrderTotal> totals = null;

        if (orderTotalPostProcessors != null) {
            for (OrderTotalPostProcessorModule module : orderTotalPostProcessors) {
                //TODO check if the module is enabled from the Admin

                List<ShoppingCartItem> items = summary.getProducts();
                for (ShoppingCartItem item : items) {

                    Long productId = item.getProductId();
                    Product product = productService.getProductForLocale(productId, language, languageService.toLocale(language, store));

                    OrderTotal orderTotal = module.caculateProductPiceVariation(summary, item, product, customer, store);
                    if (orderTotal == null) {
                        continue;
                    }
                    if (totals == null) {
                        totals = new ArrayList<OrderTotal>();
                        variation.setVariations(totals);
                    }

                    //if product is null it will be catched when invoking the module
                    orderTotal.setText(StringUtils.isNoneBlank(orderTotal.getText()) ? orderTotal.getText() : product.getProductDescription().getName());
                    variation.getVariations().add(orderTotal);
                }
            }
        }


        return variation;
    }

}
