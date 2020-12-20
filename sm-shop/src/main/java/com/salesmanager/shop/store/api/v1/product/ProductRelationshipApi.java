package com.salesmanager.shop.store.api.v1.product;

import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.review.ProductReviewService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.product.ReadableProduct;
import com.salesmanager.shop.store.controller.product.facade.ProductFacade;
import com.salesmanager.shop.store.facade.store.StoreFacade;
import com.salesmanager.shop.utils.LanguageUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/api/v1")
public class ProductRelationshipApi {

    private final ProductFacade productFacade;
    private final StoreFacade storeFacade;
    private final LanguageUtils languageUtils;
    private final ProductService productService;
    private final ProductReviewService productReviewService;

  /*	@RequestMapping( value={"/private/products/{id}/related","/auth/products/{id}/related"}, method=RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public PersistableProductReview create(@PathVariable final Long id, @Valid @RequestBody PersistableProductReview review, HttpServletRequest request, HttpServletResponse response) throws Exception {


  	try {

  		MerchantStore merchantStore = storeFacade.getByCode(request);
  		Language language = languageUtils.getRESTLanguage(request, merchantStore);

  		//rating already exist
  		ProductReview prodReview = productReviewService.getByProductAndCustomer(review.getProductId(), review.getCustomerId());
  		if(prodReview!=null) {
  			response.sendError(500, "A review already exist for this customer and product");
  			return null;
  		}

  		//rating maximum 5
  		if(review.getRating()>Constants.MAX_REVIEW_RATING_SCORE) {
  			response.sendError(503, "Maximum rating score is " + Constants.MAX_REVIEW_RATING_SCORE);
  			return null;
  		}

  		review.setProductId(id);



  		productFacade.saveOrUpdateReview(review, merchantStore, language);

  		return review;

  	} catch (Exception e) {
  		LOGGER.error("Error while saving product review",e);
  		try {
  			response.sendError(503, "Error while saving product review" + e.getMessage());
  		} catch (Exception ignore) {
  		}

  		return null;
  	}
  }*/

    @RequestMapping(value = "/products/{id}/related", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
            httpMethod = "GET",
            value =
                    "Get product related items. This is used for doing cross-sell and up-sell functionality on a product details page",
            notes = "",
            produces = "application/json",
            response = List.class)
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
            @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
    })
    public List<ReadableProduct> getAll(
            @PathVariable final Long id,
            @ApiIgnore MerchantStore merchantStore,
            @ApiIgnore Language language,
            HttpServletResponse response)
            throws Exception {

        try {
            // product exist
            Product product = productService.getById(id);

            if (product == null) {
                response.sendError(404, "Product id " + id + " does not exists");
                return null;
            }

            List<ReadableProduct> relatedItems =
                    productFacade.relatedItems(merchantStore, product, language);

            return relatedItems;

        } catch (Exception e) {
            LOGGER.error("Error while getting product reviews", e);
            try {
                response.sendError(503, "Error while getting product reviews" + e.getMessage());
            } catch (Exception ignore) {
            }

            return null;
        }
    }

  /*
  @RequestMapping( value={"/private/products/{id}/reviews/{reviewid}","/auth/products/{id}/reviews/{reviewid}"}, method=RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public PersistableProductReview update(@PathVariable final Long id, @PathVariable final Long reviewId, @Valid @RequestBody PersistableProductReview review, HttpServletRequest request, HttpServletResponse response) throws Exception {


  	try {

  		MerchantStore merchantStore = storeFacade.getByCode(request);
  		Language language = languageUtils.getRESTLanguage(request, merchantStore);

  		ProductReview prodReview = productReviewService.getById(reviewId);
  		if(prodReview==null) {
  			response.sendError(404, "Product review with id " + reviewId + " does not exist");
  			return null;
  		}

  		if(prodReview.getCustomer().getId().longValue() != review.getCustomerId().longValue()) {
  			response.sendError(404, "Product review with id " + reviewId + " does not exist");
  			return null;
  		}

  		//rating maximum 5
  		if(review.getRating()>Constants.MAX_REVIEW_RATING_SCORE) {
  			response.sendError(503, "Maximum rating score is " + Constants.MAX_REVIEW_RATING_SCORE);
  			return null;
  		}

  		review.setProductId(id);


  		productFacade.saveOrUpdateReview(review, merchantStore, language);

  		return review;

  	} catch (Exception e) {
  		LOGGER.error("Error while saving product review",e);
  		try {
  			response.sendError(503, "Error while saving product review" + e.getMessage());
  		} catch (Exception ignore) {
  		}

  		return null;
  	}
  }

  @RequestMapping( value={"/private/products/{id}/reviews/{reviewid}","/auth/products/{id}/reviews/{reviewid}"}, method=RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void delete(@PathVariable final Long id, @PathVariable final Long reviewId, HttpServletRequest request, HttpServletResponse response) throws Exception {


  	try {

  		MerchantStore merchantStore = storeFacade.getByCode(request);
  		Language language = languageUtils.getRESTLanguage(request, merchantStore);

  		ProductReview prodReview = productReviewService.getById(reviewId);
  		if(prodReview==null) {
  			response.sendError(404, "Product review with id " + reviewId + " does not exist");
  			return;
  		}

  		if(prodReview.getProduct().getId().longValue() != id.longValue()) {
  			response.sendError(404, "Product review with id " + reviewId + " does not exist");
  			return;
  		}


  		productFacade.deleteReview(prodReview, merchantStore, language);



  	} catch (Exception e) {
  		LOGGER.error("Error while deleting product review",e);
  		try {
  			response.sendError(503, "Error while deleting product review" + e.getMessage());
  		} catch (Exception ignore) {
  		}

  		return;
  	}
  }*/

}
