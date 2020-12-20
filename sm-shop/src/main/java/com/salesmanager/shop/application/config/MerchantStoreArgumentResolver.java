package com.salesmanager.shop.application.config;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.shop.store.controller.store.facade.StoreFacade;
import com.salesmanager.shop.store.controller.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.salesmanager.core.business.constants.Constants.DEFAULT_STORE;

@Slf4j
@RequiredArgsConstructor
@Component
public class MerchantStoreArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String REQUEST_PARAMATER_STORE = "store";

    private final StoreFacade storeFacade;
    private final UserFacade userFacade;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(MerchantStore.class);
    }


    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory)
            throws Exception {
        String storeValue = Optional.ofNullable(webRequest.getParameter(REQUEST_PARAMATER_STORE))
                .filter(StringUtils::isNotBlank)
                .orElse(DEFAULT_STORE);
        //todo get from cache
        MerchantStore storeModel = storeFacade.get(storeValue);

        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);

        //TODO required filter
        //authorize request
        boolean authorized = userFacade.authorizeStore(storeModel, httpServletRequest.getRequestURI());
        LOGGER.debug("is request authorized {} for {} and store {}", authorized, httpServletRequest.getRequestURI(), storeModel.getCode());
        return storeModel;
    }
}
