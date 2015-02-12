package com.github.lemniscate.spring.crud.security;

import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Identifiable;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by dave on 2/11/15.
 */
public abstract class BaseSecurityAdvisor<ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB>
        implements ApiResourceSecurityAdvisor<ID, E, CB, RB, UB> {
    
    @Override
    public Class<E> getDomainClass() {
        return (Class<E>) GenericTypeResolver.resolveTypeArguments(getClass(), BaseSecurityAdvisor.class)[1];
    }

    @Override
    public void secureGetAll(Class<?> domain, Page<RB> result) {}

    @Override
    public void secureGetOne(Class<?> domain, RB result) {}

    @Override
    public void secureDelete(Class<?> domain, ID idToDelete) {}

    @Override
    public void securePostOne(Class<?> domain, CB toCreate) {}

    @Override
    public void securePutOne(Class<?> domain, ID entityId, UB bean) {}

    @Override
    public void secureSearch(Class<?> domain, Map<String, Object> search, Page<RB> results) {}

    @Override
    public void secureCatchAll(Class<?> domain, Object controller, Class<?> domainClass, Object result) {}

    protected void fail(String message){
        throw new SecurityAdvisorRejectedException(message);
    }
    
    protected void fail(String message, Throwable t){
        throw new SecurityAdvisorRejectedException(message, t);
    }

    protected void fail(Throwable t){
        throw new SecurityAdvisorRejectedException(t);
    }
}
