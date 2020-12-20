package com.salesmanager.shop.store.facade.language;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
class LanguageFacadeImpl implements LanguageFacade {

    private final LanguageService languageService;

    @Override
    public List<Language> getLanguages() {
        try {
            List<Language> languages = languageService.getLanguages();
            if (languages.isEmpty()) {
                throw new ResourceNotFoundException("No languages found");
            }
            return languages;
        } catch (ServiceException e) {
            throw new ServiceRuntimeException(e);
        }

    }
}
