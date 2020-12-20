package com.salesmanager.shop.store.facade.country;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.references.ReadableCountry;
import com.salesmanager.shop.populator.references.ReadableCountryPopulator;
import com.salesmanager.shop.store.api.exception.ConversionRuntimeException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
class CountryFacadeImpl implements CountryFacade {

    private final CountryService countryService;

    @Override
    public List<ReadableCountry> getListCountryZones(Language language, MerchantStore merchantStore) {
        return getListOfCountryZones(language)
                .stream()
                .map(country -> convertToReadableCountry(country, language, merchantStore))
                .collect(Collectors.toList());
    }

    private ReadableCountry convertToReadableCountry(Country country, Language language, MerchantStore merchantStore) {
        try {
            ReadableCountryPopulator populator = new ReadableCountryPopulator();
            return populator.populate(country, new ReadableCountry(), merchantStore, language);
        } catch (ConversionException e) {
            throw new ConversionRuntimeException(e);
        }
    }

    private List<Country> getListOfCountryZones(Language language) {
        try {
            return countryService.listCountryZones(language);
        } catch (ServiceException e) {
            throw new ServiceRuntimeException(e);
        }
    }
}
