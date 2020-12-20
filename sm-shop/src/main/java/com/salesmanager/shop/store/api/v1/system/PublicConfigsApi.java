package com.salesmanager.shop.store.api.v1.system;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.system.Configs;
import com.salesmanager.shop.store.facade.store.StoreFacade;
import com.salesmanager.shop.store.facade.system.MerchantConfigurationFacade;
import com.salesmanager.shop.utils.LanguageUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class PublicConfigsApi {

    private final StoreFacade storeFacade;
    private final LanguageUtils languageUtils;
    private final MerchantConfigurationFacade configurationFacade;

    /**
     * Get public set of merchant configuration --- allow online purchase --- social links
     *
     * @return
     */
    @GetMapping("/config")
    @ApiOperation(
            httpMethod = "GET",
            value = "Get public configuration for a given merchant store",
            notes = "",
            produces = "application/json",
            response = Configs.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
            @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
    })
    public Configs getConfig(@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {
        return configurationFacade.getMerchantConfig(merchantStore, language);
    }
}
