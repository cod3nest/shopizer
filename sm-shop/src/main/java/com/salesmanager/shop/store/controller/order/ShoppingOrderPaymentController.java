package com.salesmanager.shop.store.controller.order;

import com.salesmanager.core.business.modules.integration.payment.impl.PayPalExpressCheckoutPayment;
import com.salesmanager.core.business.modules.integration.payment.impl.Stripe3Payment;
import com.salesmanager.core.business.services.payments.PaymentService;
import com.salesmanager.core.business.services.payments.TransactionService;
import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.OrderTotalSummary;
import com.salesmanager.core.model.payments.PaypalPayment;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shipping.ShippingSummary;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.core.model.system.IntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.core.modules.integration.payment.model.PaymentModule;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.order.ShopOrder;
import com.salesmanager.shop.store.controller.AbstractController;
import com.salesmanager.shop.store.facade.order.OrderFacade;
import com.salesmanager.shop.store.facade.shoppingCart.ShoppingCartFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Initialization of different payment services
 *
 * @author carlsamson
 */
@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(Constants.SHOP_URI)
public class ShoppingOrderPaymentController extends AbstractController {

    private final static String INIT_ACTION = "init";

    private final ShoppingCartFacade shoppingCartFacade;
    private final PaymentService paymentService;
    private final OrderFacade orderFacade;
    private final TransactionService transactionService;
    private final CoreConfiguration coreConfiguration;

    /**
     * Recalculates shipping and tax following a change in country or province
     *
     * @param order
     * @param request
     * @param response
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"/order/payment/{action}/{paymentmethod}.html"}, method = RequestMethod.POST)
    public @ResponseBody
    String paymentAction(@Valid @ModelAttribute(value = "order") ShopOrder order,
                         @PathVariable String action, @PathVariable String paymentmethod, HttpServletRequest request,
                         HttpServletResponse response, Locale locale) throws Exception {

        Language language = (Language) request.getAttribute("LANGUAGE");
        MerchantStore store = (MerchantStore) request.getAttribute(Constants.MERCHANT_STORE);
        String shoppingCartCode = getSessionAttribute(Constants.SHOPPING_CART, request);

        Validate.notNull(shoppingCartCode, "shoppingCartCode does not exist in the session");
        AjaxResponse ajaxResponse = new AjaxResponse();

        try {

            com.salesmanager.core.model.shoppingcart.ShoppingCart cart = shoppingCartFacade
                    .getShoppingCartModel(shoppingCartCode, store);

            Set<ShoppingCartItem> items = cart.getLineItems();
            List<ShoppingCartItem> cartItems = new ArrayList<ShoppingCartItem>(items);
            order.setShoppingCartItems(cartItems);

            // validate order first
            Map<String, String> messages = new TreeMap<String, String>();
            orderFacade.validateOrder(order, new BeanPropertyBindingResult(order, "order"), messages, store, locale);

            if (CollectionUtils.isNotEmpty(messages.values())) {
                for (String key : messages.keySet()) {
                    String value = messages.get(key);
                    ajaxResponse.addValidationMessage(key, value);
                }
                ajaxResponse.setStatus(AjaxResponse.RESPONSE_STATUS_VALIDATION_FAILED);
                return ajaxResponse.toJSONString();
            }

            IntegrationConfiguration config = paymentService.getPaymentConfiguration(order.getPaymentModule(), store);
            IntegrationModule integrationModule = paymentService.getPaymentMethodByCode(store,
                    order.getPaymentModule());

            // OrderTotalSummary orderTotalSummary =
            // orderFacade.calculateOrderTotal(store, order, language);
            OrderTotalSummary orderTotalSummary = super.getSessionAttribute(Constants.ORDER_SUMMARY, request);
            if (orderTotalSummary == null) {
                orderTotalSummary = orderFacade.calculateOrderTotal(store, order, language);
                super.setSessionAttribute(Constants.ORDER_SUMMARY, orderTotalSummary, request);
            }

            ShippingSummary summary = (ShippingSummary) request.getSession().getAttribute("SHIPPING_SUMMARY");

            if (summary != null) {
                order.setShippingSummary(summary);
            }

            if (action.equals(INIT_ACTION)) {
                if (paymentmethod.equals("PAYPAL")) {
                    try {
                        PaymentModule module = paymentService.getPaymentModule("paypal-express-checkout");
                        PayPalExpressCheckoutPayment p = (PayPalExpressCheckoutPayment) module;
                        PaypalPayment payment = new PaypalPayment();
                        payment.setCurrency(store.getCurrency());
                        Transaction transaction = p.initPaypalTransaction(store, cartItems, orderTotalSummary, payment,
                                config, integrationModule);
                        transactionService.create(transaction);

                        super.setSessionAttribute(Constants.INIT_TRANSACTION_KEY, transaction, request);

                        StringBuilder urlAppender = new StringBuilder();

                        urlAppender.append(coreConfiguration.getProperty("PAYPAL_EXPRESSCHECKOUT_REGULAR"));

                        urlAppender.append(transaction.getTransactionDetails().get("TOKEN"));

                        if (config.getEnvironment()
                                .equals(com.salesmanager.core.business.constants.Constants.PRODUCTION_ENVIRONMENT)) {
                            StringBuilder url = new StringBuilder()
                                    .append(coreConfiguration.getProperty("PAYPAL_EXPRESSCHECKOUT_PRODUCTION"))
                                    .append(urlAppender.toString());
                            ajaxResponse.addEntry("url", url.toString());
                        } else {
                            StringBuilder url = new StringBuilder()
                                    .append(coreConfiguration.getProperty("PAYPAL_EXPRESSCHECKOUT_SANDBOX"))
                                    .append(urlAppender.toString());
                            ajaxResponse.addEntry("url", url.toString());
                        }

                        // keep order in session when user comes back from pp
                        super.setSessionAttribute(Constants.ORDER, order, request);
                        ajaxResponse.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

                    } catch (Exception e) {
                        ajaxResponse.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
                    }
                } else if (paymentmethod.equals("stripe3")) {

                    try {

                        PaymentModule module = paymentService.getPaymentModule(paymentmethod);
                        Stripe3Payment p = (Stripe3Payment) module;

                        PaypalPayment payment = new PaypalPayment();
                        payment.setCurrency(store.getCurrency());
                        Transaction transaction = p.initTransaction(store, null, orderTotalSummary.getTotal(), null,
                                config, integrationModule);

                        transactionService.create(transaction);

                        super.setSessionAttribute(Constants.INIT_TRANSACTION_KEY, transaction, request);
                        // keep order in session when user comes back from pp
                        super.setSessionAttribute(Constants.ORDER, order, request);

                        ajaxResponse.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
                        ajaxResponse.setDataMap(transaction.getTransactionDetails());

                    } catch (Exception e) {
                        ajaxResponse.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error while performing payment action " + action + " for payment method " + paymentmethod, e);
            ajaxResponse.setErrorMessage(e);
            ajaxResponse.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);

        }

        return ajaxResponse.toJSONString();
    }

    // cancel - success paypal order
    @RequestMapping(value = {"/paypal/checkout.html/{code}"}, method = RequestMethod.GET)
    public String returnPayPalPayment(@PathVariable String code, HttpServletRequest request,
                                      HttpServletResponse response, Locale locale) throws Exception {
        if (Constants.SUCCESS.equals(code)) {
            return "redirect:" + Constants.SHOP_URI + "/order/commitPreAuthorized.html";
        } else {// process as cancel
            return "redirect:" + Constants.SHOP_URI + "/order/checkout.html";
        }
    }

}
