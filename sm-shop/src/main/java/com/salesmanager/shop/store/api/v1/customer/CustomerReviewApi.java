package com.salesmanager.shop.store.api.v1.customer;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import java.util.List;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.customer.review.CustomerReviewService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.PersistableCustomerReview;
import com.salesmanager.shop.model.customer.ReadableCustomerReview;
import com.salesmanager.shop.store.facade.customer.CustomerFacade;
import com.salesmanager.shop.store.facade.store.StoreFacade;
import com.salesmanager.shop.utils.LanguageUtils;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class CustomerReviewApi {

	private final CustomerFacade customerFacade;
	private final StoreFacade storeFacade;
	private final LanguageUtils languageUtils;
	private final CustomerService customerService;
	private final CustomerReviewService customerReviewService;

  /**
   * Reviews made for a given customer
   *
   * @param id
   * @param review
   * @return
   * @throws Exception
   */
  @PostMapping("/private/customers/{id}/reviews")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiImplicitParams({
      @ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT"),
      @ApiImplicitParam(name = "lang", dataType = "string", defaultValue = "en")
  })
  public PersistableCustomerReview create(
      @PathVariable final Long id,
      @Valid @RequestBody PersistableCustomerReview review,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language) {
    return customerFacade.createCustomerReview(id, review, merchantStore, language);
  }

  @GetMapping("/customers/{id}/reviews")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT"),
      @ApiImplicitParam(name = "lang", dataType = "string", defaultValue = "en")
  })
  public List<ReadableCustomerReview> getAll(
      @PathVariable final Long id, @ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {
    return customerFacade.getAllCustomerReviewsByReviewed(id, merchantStore, language);
  }

	@PutMapping("/private/customers/{id}/reviews/{reviewid}")
  public PersistableCustomerReview update(
      @PathVariable final Long id,
      @PathVariable final Long reviewId,
      @Valid @RequestBody PersistableCustomerReview review,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language) {
      return customerFacade.updateCustomerReview(id, reviewId, review, merchantStore, language);
	}

  @DeleteMapping("/private/customers/{id}/reviews/{reviewId}")
  public void delete(
      @PathVariable final Long id,
      @PathVariable final Long reviewId,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language) {
    customerFacade.deleteCustomerReview(id, reviewId, merchantStore, language);
  }
}
