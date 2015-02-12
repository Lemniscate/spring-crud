package com.github.lemniscate.spring.crud.security;

import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.GenericTypeResolver;
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
    public void secureFind(Class<?> domain, Iterable<E> results) { }

    @Override
    public void secureFindOne(Class<?> domain, ID id, E result) { }

    @Override
    public void secureFindByIds(Class<?> domain, Iterable<ID> ids, Iterable<E> results) { }

    @Override
    public void secureSave(Class<?> domain, E entity) { }

    @Override
    public void secureSaveMany(Class<?> domain, Iterable<E> entities) { }

    @Override
    public void secureDelete(Class<?> domain, E entity) { }

    @Override
    public void secureDeleteMany(Class<?> domain, Iterable<E> entities) { }

    @Override
    public void secureDeleteAll(Class<?> domain) { }

    @Override
    public void secureSearch(Class<?> domain, Map<String, Object> search, Iterable<E> results) { }

    @Override
    public void secureCatchAll(Class<?> domain, Object controller, MethodSignature signature, Object result) { }

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
