package com.salesmanager.shop.store.api.v0.customer;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.customer.attribute.CustomerOptionService;
import com.salesmanager.core.business.services.customer.attribute.CustomerOptionValueService;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.system.EmailService;
import com.salesmanager.core.business.services.user.GroupService;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.user.Group;
import com.salesmanager.core.model.user.GroupType;
import com.salesmanager.shop.admin.model.userpassword.UserReset;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.customer.ReadableCustomer;
import com.salesmanager.shop.model.customer.attribute.PersistableCustomerOption;
import com.salesmanager.shop.model.customer.attribute.PersistableCustomerOptionValue;
import com.salesmanager.shop.populator.customer.CustomerPopulator;
import com.salesmanager.shop.populator.customer.PersistableCustomerOptionPopulator;
import com.salesmanager.shop.populator.customer.PersistableCustomerOptionValuePopulator;
import com.salesmanager.shop.populator.customer.ReadableCustomerPopulator;
import com.salesmanager.shop.utils.EmailTemplatesUtils;
import com.salesmanager.shop.utils.LabelUtils;
import com.salesmanager.shop.utils.LocaleUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/services")
public class CustomerRESTController {

    private final CustomerService customerService;
    private final CustomerOptionValueService customerOptionValueService;
    private final CustomerOptionService customerOptionService;
    private final MerchantStoreService merchantStoreService;
    private final LanguageService languageService;
    private final CountryService countryService;
    private final GroupService groupService;
    private final ZoneService zoneService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final LabelUtils messages;
    private final EmailTemplatesUtils emailTemplatesUtils;
    private final CustomerPopulator customerPopulator;

    /**
     * Returns a single customer for a given MerchantStore
     */
    @RequestMapping(value = "/private final /{store}/customer/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ReadableCustomer getCustomer(@PathVariable final String store, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MerchantStore merchantStore = (MerchantStore) request.getAttribute(Constants.MERCHANT_STORE);
        if (merchantStore != null) {
            if (!merchantStore.getCode().equals(store)) {
                merchantStore = null;
            }
        }

        if (merchantStore == null) {
            merchantStore = merchantStoreService.getByCode(store);
        }

        if (merchantStore == null) {
            LOGGER.error("Merchant store is null for code " + store);
            response.sendError(503, "Merchant store is null for code " + store);
            return null;
        }

        Customer customer = customerService.getById(id);
        com.salesmanager.shop.model.customer.Customer customerProxy;
        if (customer == null) {
            response.sendError(404, "No Customer found with id : " + id);
            return null;
        }

        ReadableCustomerPopulator populator = new ReadableCustomerPopulator();
        ReadableCustomer readableCustomer = new ReadableCustomer();
        populator.populate(customer, readableCustomer, merchantStore, merchantStore.getDefaultLanguage());

        return readableCustomer;
    }


    /**
     * Create a customer option value
     *
     * @param store
     * @param optionValue
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/private final /{store}/customer/optionValue", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PersistableCustomerOptionValue createCustomerOptionValue(@PathVariable final String store, @Valid @RequestBody PersistableCustomerOptionValue optionValue, HttpServletRequest request, HttpServletResponse response) throws Exception {


        try {

            MerchantStore merchantStore = (MerchantStore) request.getAttribute(Constants.MERCHANT_STORE);
            if (merchantStore != null) {
                if (!merchantStore.getCode().equals(store)) {
                    merchantStore = null;
                }
            }

            if (merchantStore == null) {
                merchantStore = merchantStoreService.getByCode(store);
            }

            if (merchantStore == null) {
                LOGGER.error("Merchant store is null for code " + store);
                response.sendError(503, "Merchant store is null for code " + store);
                return null;
            }

            PersistableCustomerOptionValuePopulator populator = new PersistableCustomerOptionValuePopulator();
            populator.setLanguageService(languageService);

            com.salesmanager.core.model.customer.attribute.CustomerOptionValue optValue = new com.salesmanager.core.model.customer.attribute.CustomerOptionValue();
            populator.populate(optionValue, optValue, merchantStore, merchantStore.getDefaultLanguage());

            customerOptionValueService.save(optValue);

            optionValue.setId(optValue.getId());

            return optionValue;

        } catch (Exception e) {
            LOGGER.error("Error while saving customer option value", e);
            try {
                response.sendError(503, "Error while saving product option value" + e.getMessage());
            } catch (Exception ignore) {
            }
            return null;
        }
    }

    /**
     * Create a customer option
     *
     * @param store
     * @param option
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/private final /{store}/customer/option", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PersistableCustomerOption createCustomerOption(@PathVariable final String store, @Valid @RequestBody PersistableCustomerOption option, HttpServletRequest request, HttpServletResponse response) throws Exception {


        try {

            MerchantStore merchantStore = (MerchantStore) request.getAttribute(Constants.MERCHANT_STORE);
            if (merchantStore != null) {
                if (!merchantStore.getCode().equals(store)) {
                    merchantStore = null;
                }
            }

            if (merchantStore == null) {
                merchantStore = merchantStoreService.getByCode(store);
            }

            if (merchantStore == null) {
                LOGGER.error("Merchant store is null for code " + store);
                response.sendError(503, "Merchant store is null for code " + store);
                return null;
            }

            PersistableCustomerOptionPopulator populator = new PersistableCustomerOptionPopulator();
            populator.setLanguageService(languageService);

            com.salesmanager.core.model.customer.attribute.CustomerOption opt = new com.salesmanager.core.model.customer.attribute.CustomerOption();
            populator.populate(option, opt, merchantStore, merchantStore.getDefaultLanguage());

            customerOptionService.save(opt);

            option.setId(opt.getId());

            return option;

        } catch (Exception e) {
            LOGGER.error("Error while saving customer option", e);
            try {
                response.sendError(503, "Error while saving product option value" + e.getMessage());
            } catch (Exception ignore) {
            }
            return null;
        }
    }


    /**
     * Returns all customers for a given MerchantStore
     */
    @RequestMapping(value = "/private final /{store}/customer", method = RequestMethod.GET)
    @ResponseBody
    public List<ReadableCustomer> getCustomers(@PathVariable final String store, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MerchantStore merchantStore = (MerchantStore) request.getAttribute(Constants.MERCHANT_STORE);
        if (merchantStore != null) {
            if (!merchantStore.getCode().equals(store)) {
                merchantStore = null;
            }
        }

        if (merchantStore == null) {
            merchantStore = merchantStoreService.getByCode(store);
        }

        if (merchantStore == null) {
            LOGGER.error("Merchant store is null for code " + store);
            response.sendError(503, "Merchant store is null for code " + store);
            return null;
        }

        List<Customer> customers = customerService.getListByStore(merchantStore);
        List<ReadableCustomer> returnCustomers = new ArrayList<ReadableCustomer>();
        for (Customer customer : customers) {

            ReadableCustomerPopulator populator = new ReadableCustomerPopulator();
            ReadableCustomer readableCustomer = new ReadableCustomer();
            populator.populate(customer, readableCustomer, merchantStore, merchantStore.getDefaultLanguage());
            returnCustomers.add(readableCustomer);

        }

        return returnCustomers;
    }


    /**
     * Deletes a customer for a given MerchantStore
     */
    @RequestMapping(value = "/private final /{store}/customer/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable final String store, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {


        try {

            Customer customer = customerService.getById(id);

            if (customer == null) {
                response.sendError(404, "No Customer found for ID : " + id);
                return;
            }

            MerchantStore merchantStore = merchantStoreService.getByCode(store);
            if (merchantStore == null) {
                response.sendError(404, "Invalid merchant store : " + store);
                return;
            }

            if (merchantStore.getId().intValue() != customer.getMerchantStore().getId().intValue()) {
                response.sendError(404, "Customer id: " + id + " is not part of store " + store);
                return;
            }

            customerService.delete(customer);


        } catch (ServiceException se) {
            LOGGER.error("Cannot delete customer", se);
            response.sendError(404, "An exception occured while removing the customer");
            return;
        }

    }


    /**
     * Create new customer for a given MerchantStore
     */
    @RequestMapping(value = "/private final /{store}/customer", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @Deprecated
    public PersistableCustomer createCustomer(@PathVariable final String store, @Valid @RequestBody PersistableCustomer customer, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MerchantStore merchantStore = (MerchantStore) request.getAttribute(Constants.MERCHANT_STORE);
        if (merchantStore != null) {
            if (!merchantStore.getCode().equals(store)) {
                merchantStore = null;
            }
        }

        if (merchantStore == null) {
            merchantStore = merchantStoreService.getByCode(store);
        }

        if (merchantStore == null) {
            LOGGER.error("Merchant store is null for code " + store);
            response.sendError(503, "Merchant store is null for code " + store);
            return null;
        }

        Customer cust = new Customer();
		
/*		CustomerPopulator populator = new CustomerPopulator();
		populator.setCountryService(countryService);
		populator.setCustomerOptionService(customerOptionService);
		populator.setCustomerOptionValueService(customerOptionValueService);
		populator.setLanguageService(languageService);
		populator.setZoneService(zoneService);
		populator.setGroupService(groupService);*/
        customerPopulator.populate(customer, cust, merchantStore, merchantStore.getDefaultLanguage());

        List<Group> groups = groupService.listGroup(GroupType.ADMIN);
        cust.setGroups(groups);

        Locale customerLocale = LocaleUtils.getLocale(cust.getDefaultLanguage());

        String password = customer.getPassword();
        if (StringUtils.isBlank(password)) {
            password = UserReset.generateRandomString();
            customer.setPassword(password);
        }


        customerService.save(cust);
        customer.setId(cust.getId());

        emailTemplatesUtils.sendRegistrationEmail(customer, merchantStore, customerLocale, request.getContextPath());


        return customer;
    }

}
