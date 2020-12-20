package com.salesmanager.shop.store.controller.order;

import com.salesmanager.core.business.services.order.OrderService;
import com.salesmanager.core.business.services.order.orderproduct.OrderProductDownloadService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.order.orderproduct.OrderProductDownload;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.order.ReadableOrderProductDownload;
import com.salesmanager.shop.model.order.v0.ReadableOrder;
import com.salesmanager.shop.populator.order.ReadableOrderProductDownloadPopulator;
import com.salesmanager.shop.store.controller.AbstractController;
import com.salesmanager.shop.store.controller.ControllerConstants;
import com.salesmanager.shop.store.facade.order.OrderFacade;
import com.salesmanager.shop.utils.LabelUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(Constants.SHOP_URI + "/order")
public class ShoppingOrderConfirmationController extends AbstractController {

    private final OrderService orderService;
    private final CountryService countryService;
    private final ZoneService zoneService;
    private final OrderFacade orderFacade;
    private final LabelUtils messages;
    private final OrderProductDownloadService orderProdctDownloadService;

    /**
     * Invoked once the payment is complete and order is fulfilled
     *
     * @param model
     * @param request
     * @param response
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping("/confirmation.html")
    public String displayConfirmation(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

        Language language = (Language) request.getAttribute("LANGUAGE");
        MerchantStore store = (MerchantStore) request.getAttribute(Constants.MERCHANT_STORE);

        Long orderId = super.getSessionAttribute(Constants.ORDER_ID, request);
        if (orderId == null) {
            return new StringBuilder().append("redirect:").append(Constants.SHOP_URI).toString();
        }

        //get the order
        Order order = orderService.getById(orderId);
        if (order == null) {
            LOGGER.warn("Order id [" + orderId + "] does not exist");
            throw new Exception("Order id [" + orderId + "] does not exist");
        }

        if (order.getMerchant().getId().intValue() != store.getId().intValue()) {
            LOGGER.warn("Store id [" + store.getId() + "] differs from order.store.id [" + order.getMerchant().getId() + "]");
            return new StringBuilder().append("redirect:").append(Constants.SHOP_URI).toString();
        }

        if (super.getSessionAttribute(Constants.ORDER_ID_TOKEN, request) != null) {
            //set this unique token for performing unique operations in the confirmation
            model.addAttribute("confirmation", "confirmation");
        }

        //remove unique token
        super.removeAttribute(Constants.ORDER_ID_TOKEN, request);


        String[] orderMessageParams = {store.getStorename()};
        String orderMessage = messages.getMessage("label.checkout.text", orderMessageParams, locale);
        model.addAttribute("ordermessage", orderMessage);

        String[] orderIdParams = {String.valueOf(order.getId())};
        String orderMessageId = messages.getMessage("label.checkout.orderid", orderIdParams, locale);
        model.addAttribute("ordermessageid", orderMessageId);

        String[] orderEmailParams = {order.getCustomerEmailAddress()};
        String orderEmailMessage = messages.getMessage("label.checkout.email", orderEmailParams, locale);
        model.addAttribute("orderemail", orderEmailMessage);

        ReadableOrder readableOrder = orderFacade.getReadableOrder(orderId, store, language);


        //resolve country and Zone for GA
        String countryCode = readableOrder.getCustomer().getBilling().getCountry();
        Map<String, Country> countriesMap = countryService.getCountriesMap(language);
        Country billingCountry = countriesMap.get(countryCode);
        if (billingCountry != null) {
            readableOrder.getCustomer().getBilling().setCountry(billingCountry.getName());
        }

        String zoneCode = readableOrder.getCustomer().getBilling().getZone();
        Map<String, Zone> zonesMap = zoneService.getZones(language);
        Zone billingZone = zonesMap.get(zoneCode);
        if (billingZone != null) {
            readableOrder.getCustomer().getBilling().setZone(billingZone.getName());
        }


        model.addAttribute("order", readableOrder);

        //check if any downloads exist for this order
        List<OrderProductDownload> orderProductDownloads = orderProdctDownloadService.getByOrderId(order.getId());
        if (CollectionUtils.isNotEmpty(orderProductDownloads)) {
            ReadableOrderProductDownloadPopulator populator = new ReadableOrderProductDownloadPopulator();
            List<ReadableOrderProductDownload> downloads = new ArrayList<ReadableOrderProductDownload>();
            for (OrderProductDownload download : orderProductDownloads) {
                ReadableOrderProductDownload view = new ReadableOrderProductDownload();
                populator.populate(download, view, store, language);
                downloads.add(view);
            }
            model.addAttribute("downloads", downloads);
        }


        /** template **/
        StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Checkout.confirmation).append(".").append(store.getStoreTemplate());
        return template.toString();


    }


}
