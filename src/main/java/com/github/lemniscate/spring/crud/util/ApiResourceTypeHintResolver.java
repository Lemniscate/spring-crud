package com.github.lemniscate.spring.crud.util;

import com.github.lemniscate.spring.crud.repo.ApiResourceRepository;
import com.github.lemniscate.spring.crud.svc.ApiResourceService;
import com.github.lemniscate.spring.crud.web.ApiResourceController;
import com.github.lemniscate.spring.crud.web.assembler.IApiResourceAssembler;
import com.github.lemniscate.spring.typehint.annotation.TypeHints;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.Assert;

/**
 * Created by dave on 2/25/15.
 */
public class ApiResourceTypeHintResolver implements TypeHints.TypeHintResolver {

    @Override
    public Class<?>[] getTypeHints(Class<?> source, Class<?> type) {
        Class[] classes = new Class[]{
                IApiResourceAssembler.class, ApiResourceController.class,
                ApiResourceService.class, ApiResourceRepository.class
        };

        Class<?> superType = null;
        for( Class<?> c : classes){
            if( c.isAssignableFrom(source) ){
                superType = c;
                break;
            }
        }

        Assert.notNull(superType, "Could not determine superType to compare " + source.getName());
        return GenericTypeResolver.resolveTypeArguments(source, superType);
    }
}
