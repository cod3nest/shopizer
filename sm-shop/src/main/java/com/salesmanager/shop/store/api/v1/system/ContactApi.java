package com.salesmanager.shop.store.api.v1.system;

import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.shop.ContactForm;
import com.salesmanager.shop.utils.EmailTemplatesUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ContactApi {

    private final LanguageService languageService;
    private final EmailTemplatesUtils emailTemplatesUtils;

    @PostMapping("/contact")
    @ApiOperation(
            httpMethod = "POST",
            value = "Sends an email contact us to store owner",
            notes = "",
            produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
            @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
    })
    public ResponseEntity<Void> contact(
            @Valid @RequestBody ContactForm contact,
            @ApiIgnore MerchantStore merchantStore,
            @ApiIgnore Language language,
            HttpServletRequest request) {
        Locale locale = languageService.toLocale(language, merchantStore);
        emailTemplatesUtils.sendContactEmail(contact, merchantStore, locale, request.getContextPath());
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
