package com.salesmanager.shop.store.api.v1.references;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.currency.Currency;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.references.MeasureUnit;
import com.salesmanager.shop.model.references.ReadableCountry;
import com.salesmanager.shop.model.references.ReadableZone;
import com.salesmanager.shop.model.references.SizeReferences;
import com.salesmanager.shop.model.references.WeightUnit;
import com.salesmanager.shop.store.facade.country.CountryFacade;
import com.salesmanager.shop.store.facade.currency.CurrencyFacade;
import com.salesmanager.shop.store.facade.language.LanguageFacade;
import com.salesmanager.shop.store.facade.store.StoreFacade;
import com.salesmanager.shop.store.facade.zone.ZoneFacade;
import com.salesmanager.shop.utils.LanguageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * Get system Language, Country and Currency objects
 *
 * @author c.samson
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ReferencesApi {

  private final StoreFacade storeFacade;
  private final LanguageUtils languageUtils;
  private final LanguageFacade languageFacade;
  private final CountryFacade countryFacade;
  private final ZoneFacade zoneFacade;
  private final CurrencyFacade currencyFacade;

  /**
   * Search languages by language code private/languages returns everything
   *
   * @return
   */
  @GetMapping("/languages")
  public List<Language> getLanguages() {
    return languageFacade.getLanguages();
  }

  /**
   * Returns a country with zones (provinces, states) supports language set in parameter
   * ?lang=en|fr|ru...
   *
   * @param request
   * @return
   */
  @GetMapping("/country")
  public List<ReadableCountry> getCountry(HttpServletRequest request) {
    MerchantStore merchantStore = storeFacade.getByCode(request);
    Language lang = languageUtils.getRESTLanguage(request);
    return countryFacade.getListCountryZones(lang, merchantStore);
  }

  @GetMapping("/zones")
  public List<ReadableZone> getZones(
      @RequestParam("code") String code, HttpServletRequest request) {
    MerchantStore merchantStore = storeFacade.getByCode(request);
    Language lang = languageUtils.getRESTLanguage(request);
    return zoneFacade.getZones(code, lang, merchantStore);
  }

  /**
   * Currency
   *
   * @return
   */
  @GetMapping("/currency")
  public List<Currency> getCurrency() {
    return currencyFacade.getList();
  }

  @GetMapping("/measures")
  public SizeReferences measures() {
    SizeReferences sizeReferences = new SizeReferences();
    sizeReferences.setMeasures(Arrays.asList(MeasureUnit.values()));
    sizeReferences.setWeights(Arrays.asList(WeightUnit.values()));
    return sizeReferences;
  }
}
